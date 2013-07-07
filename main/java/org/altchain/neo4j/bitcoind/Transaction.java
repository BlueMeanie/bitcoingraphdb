package org.altchain.neo4j.bitcoind;

//this code is copyright 2013 Joshua Mark Zeidner and
//licensed under the Apache 2.0 license, see LICENSE.txt


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Transaction {
	
	JSONObject obj;
	
	Input[] inputs;
	
	Output[] outputs;
	
	public Transaction( JSONObject o ){
		obj = o;
		
		JSONArray vin = (JSONArray)obj.get("vin");
		inputs = new Input[vin.size()]; int i=0;
		for( Object obj : vin ) {
			inputs[i++] = new Input((JSONObject)obj);
		}
		
		JSONArray vout = (JSONArray)obj.get("vout");
		outputs = new Output[vout.size()]; i=0;
		for( Object obj : vout ) {
			outputs[i++] = new Output((JSONObject)obj);
		}
		
	}
	
	public Output[] getOutputs() {
		return outputs;
	}
	
	public Input[] getInputs() {
		return inputs;
	}
	
	public String getJSONData() {
		return obj.toJSONString();
	}
	
	public String getHex() {
		return (String)obj.get("hex");
	}
	
	public String getTxID() {
		return (String)obj.get("txid");
	}
	
	public String getBlockHash() {
		return (String)obj.get("blockhash");
	}
	
	public Long getConfirmations() {
		return (Long)obj.get("confirmations");
	}
	
	public Long getTime() {
		return (Long)obj.get("time");
	}
	
	public Long getBlocktime() {
		return (Long)obj.get("blocktime");
	}


}
