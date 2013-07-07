package org.altchain.neo4j.bitcoind;

//this code is copyright 2013 Joshua Mark Zeidner and
//licensed under the Apache 2.0 license, see LICENSE.txt

import org.json.simple.JSONObject;


public class Input {
	
	JSONObject obj;
	
	boolean isCoinbase;
	
	Input( JSONObject o ){
		obj = o;
		if ( o.get("coinbase") != null ) {
			isCoinbase = true;
		} else {
			isCoinbase = false;
		}
	}
	
	public boolean isCoinbase(){
		return isCoinbase;
	}
	
	public Long getSequence() {
		return (Long)obj.get("sequence");
	}
	
	public String getCoinbase() throws NotCoinbaseException {
		if ( !isCoinbase ) throw new NotCoinbaseException();
		return (String)obj.get("coinbase");
	}
	
	public String getTxid() throws IsCoinbaseException {
		if ( isCoinbase ) throw new IsCoinbaseException();
		return (String)obj.get("txid");
	}
	
	public Long getVout() throws IsCoinbaseException {
		if ( isCoinbase ) throw new IsCoinbaseException();
		return (Long)obj.get("vout");
	}
	
	public String getScriptSigAsm() throws IsCoinbaseException {
		if ( isCoinbase ) throw new IsCoinbaseException();
		return (String)((JSONObject)obj.get("scriptSig")).get("asm");
	}
	
	public String getScriptSigHex() throws IsCoinbaseException {
		if ( isCoinbase ) throw new IsCoinbaseException();
		return (String)((JSONObject)obj.get("scriptSig")).get("hex");
	}
	

}
