/**
 * 
 */
package org.altchain.neo4j;


import org.altchain.neo4j.bitcoind.BitcoinD;
import org.altchain.neo4j.bitcoind.BitcoindNotRunningException;
import org.altchain.neo4j.bitcoind.Block;
import org.altchain.neo4j.bitcoind.NotCoinbaseException;
import org.altchain.neo4j.bitcoind.Transaction;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jjzeidner
 *
 */
public class JsonRPCTest {
	
	final static Logger logger = LoggerFactory.getLogger(JsonRPCTest.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

/*	@Test
	public void getinfo() {
		
		ETL client = new ETL();
		JSONObject json = client.getInfo();
		assert( json != null );
		
		logger.debug( "TEST| getinfo(): " + json.toString() );
		
	}

	@Test
	public void getnewaddress() {
		
		ETL client = new ETL();
		String address = client.getNewAddress("address-label");
		assert( address != null );
	
		logger.debug( "TEST| getnewaddress(): " + address );
		
	}*/

	@Test
	public void getbalance() throws BitcoindNotRunningException {
		
		BitcoinD client = new BitcoinD();
		Double balance = client.getBalance("address-label");
		assert( balance == 0d );
		
		logger.debug( "TEST| getbalance(): " + balance );
				
	}
	
	@Test
	public void getHash() throws BitcoindNotRunningException {
		
		BitcoinD client = new BitcoinD();
		String hash = client.getBlockHash(10);
		
		logger.debug( "TEST| getHash(1): " + hash );
				
	}
	
	@Test
	public void getBlock() throws BitcoindNotRunningException {
		
		BitcoinD client = new BitcoinD();
		Block block = client.getBlockData(170);
		
		logger.debug( "TEST| getBlock(1): " + block.getJSONData() );
		logger.debug( "TEST| getBlock(1) retrieve Hash: " + block.getHash() );
		logger.debug( "TEST| getBlock(1) retrieve Bits: " + block.getBits() );
		logger.debug( "TEST| getBlock(1) retrieve MerkleRoot: " + block.getMerkleRoot() );
			
	}
	
	@Test
	public void getTransaction() throws BitcoindNotRunningException {
		
		BitcoinD client = new BitcoinD();
		
		Transaction t = client.getTransactionData("d3ad39fa52a89997ac7381c95eeffeaf40b66af7a57e9eba144be0a175a12b11");
		
		logger.debug( "TEST| getTransaction(): " + t.getJSONData() );
		logger.debug( "TEST| getTransaction() retrieve value ['blockhash']: " + t.getBlockHash() );
		logger.debug( "TEST| num Inputs : " + t.getInputs().length );
		logger.debug( "TEST| num Ouputs : " + t.getOutputs().length );
		logger.debug( "TEST| Output 1 value : " + t.getOutputs()[0].getValue() );
		
		try {
			logger.debug( "TEST| Input 1 coinbase : " + t.getInputs()[0].getCoinbase() );
		} catch (NotCoinbaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	


}
