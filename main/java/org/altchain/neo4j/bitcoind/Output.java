package org.altchain.neo4j.bitcoind;

//this code is copyright 2013 Joshua Mark Zeidner and
//licensed under the Apache 2.0 license, see LICENSE.txt

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Output {
	
	JSONObject obj;
	
	public Output( JSONObject o ){
		obj = o;
	}
	
	public Double getValue() {
		return (Double)obj.get("value");
	}
	
	public Long getN() {
		return (Long)obj.get("n");
	}
	
	public String getScriptSigAsm()  {
		return (String)((JSONObject)obj.get("scriptPubKey")).get("asm");
	}
	
	public String getScriptSigHex() {
		return (String)((JSONObject)obj.get("scriptPubKey")).get("hex");
	}
	
	public Long getReqSigs() {
		return (Long)((JSONObject)obj.get("scriptPubKey")).get("reqSigs");
	}
	
	public String getType() {
		return (String)((JSONObject)obj.get("scriptPubKey")).get("type");
	}
	
	public JSONArray getAddresses() {
		return (JSONArray)((JSONObject)obj.get("scriptPubKey")).get("addresses");

	}
	

}
