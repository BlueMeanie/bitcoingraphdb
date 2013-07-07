package org.altchain.neo4j.bitcoind;

//this code is copyright 2013 Joshua Mark Zeidner and
//licensed under the Apache 2.0 license, see LICENSE.txt

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinD {
	
	// this code originally began as a code fragment I found on the net.  If you recognize your code, let me know, I'll credit you here.
	
	// ideally all the code should use Apache Jersey, but currently doesn't
	

	final static Logger logger = LoggerFactory.getLogger(BitcoinD.class);
	
	DefaultHttpClient httpclient = new DefaultHttpClient();
	
	final static String bitcoindID = "generated_by_armory";
	final static String bitcoinPassword = "6nkugwdEacEAgqjbCvvVyrgXcZj5Cxr38vTbZ513QJrf";
	final static String bitcoindHost = "localhost";
	final static int bitcoindPort = 8332;
	
	private static final String COMMAND_GET_BALANCE = "getbalance";
	private static final String COMMAND_GET_INFO = "getinfo";
	private static final String COMMAND_GET_NEW_ADDRESS = "getnewaddress";
	private static final String COMMAND_GET_BLOCK_HASH = "getblockhash";

	
    
	private JSONObject invokeRPC(String JSONRequestString) throws BitcoindNotRunningException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		
		JSONObject responseJsonObj = null;
		
		try {
			
			httpclient.getCredentialsProvider().setCredentials(new AuthScope( bitcoindHost , bitcoindPort ),
					new UsernamePasswordCredentials("generated_by_armory", "6nkugwdEacEAgqjbCvvVyrgXcZj5Cxr38vTbZ513QJrf"));
			
			StringEntity myEntity = new StringEntity( JSONRequestString );
			logger.debug( "JSON Request Object: " + JSONRequestString );
			
			HttpPost httppost = new HttpPost("http://" + this.bitcoindHost + ":" + this.bitcoindPort );
			httppost.setEntity(myEntity);

			logger.debug( "executing request: " + httppost.getRequestLine() );

			HttpEntity entity = null;
			
			try {
				
				HttpResponse response = httpclient.execute(httppost);
				entity = response.getEntity();

				logger.debug( "HTTP response: " + response.getStatusLine() );
				
			} catch ( Exception e ){
				logger.error( "CANNOT CONNECT TO BITCOIND.  IS BITCOIN RUNNING?" );
				throw new BitcoindNotRunningException();
			}
			
			if (entity != null) {
				
				logger.debug( "Response content length: " + entity.getContentLength() );

			}
			
			JSONParser parser = new JSONParser();
			responseJsonObj = (JSONObject) parser.parse( EntityUtils.toString(entity) );
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		} finally {
			
			httpclient.getConnectionManager().shutdown();
			
		}
		
		return responseJsonObj;
		
	}
	
	public Double getBalance( String account ) throws BitcoindNotRunningException {
		
		Object[] params = { account };
		String requestString = String.format( "{\"id\":\"%s\",\"method\":\"getbalance\",\"params\":[\"%s\"]}", UUID.randomUUID().toString(), account );
		JSONObject json = invokeRPC( requestString );

		return (Double)json.get("result");
		
	}
	

	public String getBlockHash( int index ) throws BitcoindNotRunningException { 

		String requestString = String.format( "{\"id\":\"%s\",\"method\":\"getblockhash\",\"params\":[%s]}", UUID.randomUUID().toString(), index );
		JSONObject json = invokeRPC( requestString );

		return (String)json.get("result");
			
	}
	
		
	public Block getBlockData( int index ) throws BitcoindNotRunningException {
		
		String hash = getBlockHash( index );
		String requestString = String.format( "{\"id\":\"%s\",\"method\":\"getblock\",\"params\":[\"%s\"]}", UUID.randomUUID().toString(), hash );
		JSONObject json = invokeRPC( requestString );

		return new Block( (JSONObject)json.get("result") );
		
	}
	
	
	public Transaction getTransactionData( String hash ) throws BitcoindNotRunningException {
		
		String requestString = String.format( "{\"id\":\"%s\",\"method\":\"getrawtransaction\",\"params\":[\"%s\",1]}", UUID.randomUUID().toString(), hash );
		JSONObject json = invokeRPC( requestString );

		return new Transaction( (JSONObject) json.get("result") );
		
	}
	

}
