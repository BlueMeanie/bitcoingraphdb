package org.altchain.neo4j;

// this code is copyright 2013 Joshua Mark Zeidner and
// licensed under the Apache 2.0 license, see LICENSE.txt

import java.net.URI;

import org.altchain.neo4j.bitcoind.BitcoinD;
import org.altchain.neo4j.bitcoind.BitcoindNotRunningException;
import org.altchain.neo4j.bitcoind.Block;
import org.altchain.neo4j.bitcoind.Input;
import org.altchain.neo4j.bitcoind.Output;
import org.altchain.neo4j.bitcoind.Transaction;
import org.altchain.neo4j.database.Database;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETL {
 
	final static long BLOCK_INDEX_START = 0;
	final static long BLOCK_INDEX_END = 25000;

	
	final static Logger logger = LoggerFactory.getLogger(ETL.class);

	BitcoinD bitcoind;
	Database database;

	ETL() {

		bitcoind = new BitcoinD();
		database = new Database();

	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		ETL etl = new ETL();

		for (long blocknum = BLOCK_INDEX_START; blocknum < BLOCK_INDEX_END; blocknum++) {

			logger.info("@@@@@@@@@@@@@@@@@@ processing block #" + blocknum);

			Block block = null;
			try {
				block = etl.bitcoind.getBlockData((int) blocknum);
			} catch (BitcoindNotRunningException e1) {
				logger.error("BITCOIND NOT RUNNING.  Run Bitcoin-qt or Armory.");
				System.exit(1);
			}

			URI newblock = etl.database.createNode();
			etl.database.addProperty(newblock, "type", "block");
			etl.database.addProperty(newblock, "hash", block.getHash());
			etl.database.addProperty(newblock, "merkleroot",
					block.getMerkleRoot());
			etl.database.addProperty(newblock, "bits", block.getBits());
			etl.database.addProperty(newblock, "nextblockhash",
					block.getNextBlockHash());
			etl.database.addProperty(newblock, "previousblockhash",
					block.getPreviousBlockHash());
			etl.database.addProperty(newblock, "confirmations",
					block.getConfirmations());
			etl.database.addProperty(newblock, "difficulty",
					block.getDifficulty());
			etl.database.addProperty(newblock, "index", blocknum);

			JSONArray txs = block.getTXArray();

			int numTX = 0;

			for (Object txO : txs) {
				try {
					Transaction tx = etl.bitcoind
							.getTransactionData((String) txO);
					URI newTX = etl.database.createNode();
					etl.database.addProperty(newTX, "type", "transaction");
					etl.database.addProperty(newTX, "blockhash",
							tx.getBlockHash());
					etl.database.addProperty(newTX, "hex", tx.getHex());
					etl.database.addProperty(newTX, "blocktime",
							tx.getBlocktime());
					etl.database.addProperty(newTX, "confirmations",
							tx.getConfirmations());
					etl.database.addProperty(newTX, "time", tx.getTime());
					etl.database.addProperty(newTX, "hash", tx.getTxID());

					for (Input input : tx.getInputs()) {

						URI newInput = etl.database.createNode();
						if (input.isCoinbase()) {
							etl.database.addProperty(newInput, "coinbase",
									input.getCoinbase());
							etl.database.addProperty(newInput, "sequence",
									input.getSequence());
						} else { // non-coinbase tx

							etl.database.addProperty(newInput, "scriptSigAsm",
									input.getScriptSigAsm());
							etl.database.addProperty(newInput, "scriptSigHex",
									input.getScriptSigHex());
							etl.database.addProperty(newInput, "txid",
									input.getTxid());
							etl.database.addProperty(newInput, "vout",
									input.getVout());

							String getMatchingOutputQuery = String
									.format("START tx=node:transactions(hash=\"%s\") MATCH (tx)-[out:has_output]-(output) where out.n = %s return output;",
											input.getTxid(), input.getVout());
							URI matchingOutput = null;
							try {
								matchingOutput = etl.database
										.cypherQueryGetSingle(getMatchingOutputQuery);
								etl.database.addRelationship(matchingOutput,
										newInput, "output_to_input", "{}");
							} catch (Exception e) {
								logger.error(String
										.format("NO CORRESPONDING OUTPUT FOR INPUT, tx %s, output # %s",
												input.getTxid(),
												input.getVout()));
							}

						}
						etl.database.addProperty(newInput, "type", "input");
						URI relationshipUri = etl.database.addRelationship(
								newTX, newInput, "has_input", "{}");

					}

					for (Output output : tx.getOutputs()) {

						URI newOutput = etl.database.createNode();

						// TODO: req sigs is not an integer it's an array
						// etl.database.addProperty( newOutput, "ReqSigs",
						// output.getReqSigs() );

						etl.database.addProperty(newOutput, "type", "output");
						etl.database.addProperty(newOutput, "ScriptSigAsm",
								output.getScriptSigAsm());
						etl.database.addProperty(newOutput, "ScriptSigHex",
								output.getScriptSigHex());
						etl.database.addProperty(newOutput, "outputtype",
								output.getType());
						etl.database.addProperty(newOutput, "value",
								output.getValue());
						etl.database.addProperty(newOutput, "n", output.getN());
						etl.database.addProperty(newOutput, "addresses",
								output.getAddresses());
						for (Object address : output.getAddresses()) {

							// first convert the address to a string
							String addressString = address.toString();
							// logger.info("ADDRESS STRING: "+addressString);

							// now check and see if this address already exists
							URI addressNode = etl.database.getNodeFromIndexKey(
									"addresses", "address", addressString);

							// if not add it
							if (addressNode == null) {
								addressNode = etl.database.createNode();
								logger.info(">>NEW ADDRESS FOUND. "
										+ addressString);

								etl.database.addProperty(addressNode,
										"address", addressString);
								etl.database.addProperty(addressNode, "type",
										"address");
								etl.database.addNodeToIndex(addressNode,
										"addresses", "address", addressString);
							}

							// add a relationship to the output
							URI relationshipUri = etl.database.addRelationship(
									addressNode,
									newOutput,
									"address_out",
									"{\"txid\":\"" + tx.getTxID()
											+ "\",\"blockindex\":\""
											+ block.getHeight() + "\"}");
							// logger.info( ">>>>ADDING ADDRESS RELATION " +
							// addressString );
						}

						URI relationshipUri = etl.database.addRelationship(
								newTX, newOutput, "has_output", "{ \"n\" : "
										+ output.getN() + " }");

						// TODO: make each address a node
						// also make special relation 'appears741 first' in the
						// block it appears first

					}

					etl.database.addNodeToIndex(newTX, "transactions", "hash",
							tx.getTxID());

					// now create a relationship to the block

					URI relationshipUri = etl.database.addRelationship(
							newblock, newTX, "has_transaction",
							"{ \"number\" : " + numTX + " }");

					numTX++;

				} catch (Exception e) {
					logger.error("NO TX DATA: " + (String) txO + " blocknum: "
							+ block.getHeight() + " error " + e.getMessage());
					e.printStackTrace();
				}

			}

			etl.database.addNodeToIndex(newblock, "blocks", "hash",
					block.getHash());
			etl.database.addNodeToIndex(newblock, "blocks", "index", blocknum);

		}

	}

}
