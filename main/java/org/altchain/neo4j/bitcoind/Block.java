package org.altchain.neo4j.bitcoind;

//this code is copyright 2013 Joshua Mark Zeidner and
//licensed under the Apache 2.0 license, see LICENSE.txt

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Block {
	
	JSONObject obj;
	
	Block( JSONObject o ) {
		obj = o;
	}

	public String getJSONData() {
		return obj.toJSONString();
	}
	
	public JSONArray getTXArray() {
		return (JSONArray)obj.get("tx");

	}
	
	public String getPreviousBlockHash() {
		return (String)obj.get("previousblockhash");
	}
	
	public Long getConfirmations() {
		return (Long)obj.get("confirmations");
	}
	
	public String getHash() {
		return (String)obj.get("hash");
	}
	
	public String getNextBlockHash() {
		return (String)obj.get("nextblockhash");
	}
	
	public Double getDifficulty() {
		return (Double)obj.get("difficulty");
	}
	
	public String getMerkleRoot() {
		return (String)obj.get("merkleroot");
	}
	
	public Integer getSize() {
		return (Integer)obj.get("size");
	}
	
	public Integer getVersion() {
		return (Integer)obj.get("version");
	}
	
	public Integer getTime() {
		return (Integer)obj.get("time");
	}
	
	public Long getHeight() {
		return (Long)obj.get("height");
	}
	
	public Integer getNonce() {
		return (Integer)obj.get("nonce");
	}
	
	public String getBits() {
		return (String)obj.get("bits");
	}

}
