package org.altchain.neo4j.database;

//this code is copyright 2013 Joshua Mark Zeidner and
//licensed under the Apache 2.0 license, see LICENSE.txt

import java.io.IOException;
import java.net.URI;

import org.altchain.neo4j.bitcoind.BitcoinD;
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
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Database {
	
	final static Logger logger = LoggerFactory.getLogger(Database.class);
	
	DefaultHttpClient httpclient = new DefaultHttpClient();
			
    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
	
	public boolean isServerRunning(){
		
		WebResource resource = Client.create()
		        .resource( SERVER_ROOT_URI );
		
		ClientResponse response = resource.get( ClientResponse.class );
		 
		logger.debug( String.format( "GET on [%s], status code [%d]", SERVER_ROOT_URI, response.getStatus() ) );
		response.close();
		
		if (response.getStatus() == 200) return true;
		return false;
		
	}


    public static void addMetadataToProperty( URI relationshipUri,
            String name, String value ) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( name, value );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        logger.debug( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }

    public static String toJsonNameValuePairCollection( String name,
            String value )
    {
        return String.format( "{ \"%s\" : \"%s\" }", name, value );
    }

    public static URI createNode()
    {
        final String nodeEntryPointUri = SERVER_ROOT_URI + "node";

        WebResource resource = Client.create()
                .resource( nodeEntryPointUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( "{}" )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        logger.debug( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                nodeEntryPointUri, response.getStatus(), location.toString() ) );
        response.close();

        return location;
    }

    public static URI addRelationship( URI startNode, URI endNode,
            String relationshipType, String jsonAttributes )
            throws URISyntaxException
    {
        URI fromUri = new URI( startNode.toString() + "/relationships" );
        String relationshipJson = generateJsonRelationship( endNode,
                relationshipType, jsonAttributes );

        WebResource resource = Client.create()
                .resource( fromUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( relationshipJson )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        logger.debug( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                fromUri, response.getStatus(), location.toString() ) );

        response.close();
        return location;
    }
    // END SNIPPET: insideAddRel

    public static String generateJsonRelationship( URI endNode,
            String relationshipType, String... jsonAttributes )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ \"to\" : \"" );
        sb.append( endNode.toString() );
        sb.append( "\", " );

        sb.append( "\"type\" : \"" );
        sb.append( relationshipType );
        if ( jsonAttributes == null || jsonAttributes.length < 1 )
        {
            sb.append( "\"" );
        }
        else
        {
            sb.append( "\", \"data\" : " );
            for ( int i = 0; i < jsonAttributes.length; i++ )
            {
                sb.append( jsonAttributes[i] );
                if ( i < jsonAttributes.length - 1 )
                { // Miss off the final comma
                    sb.append( ", " );
                }
            }
        }

        sb.append( " }" );
        return sb.toString();
    }

    public static void addProperty( URI nodeUri, String propertyName,
            Object propertyValue )
    {
        // START SNIPPET: addProp
        String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
        // http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

        WebResource resource = Client.create()
                .resource( propertyUri );
        
        ClientResponse response = null;
        
		if (propertyValue.getClass() == String.class) {
			response = resource.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.entity("\"" + propertyValue + "\"")
					.put(ClientResponse.class);
		} else {
			response = resource.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON)
					.entity(propertyValue.toString()).put(ClientResponse.class);
		}

        logger.debug( String.format( "PUT to [%s], status code [%d]",
                propertyUri, response.getStatus() ) );
        response.close();
    }
    

    static final String NODE_INDEX_ROOT = "http://localhost:7474/db/data/index/node/";
    
    @SuppressWarnings("unchecked")
	public static URI addNodeToIndex( URI nodeUri, String indexName, String key, Object value ){
    	
    	String indexUri = NODE_INDEX_ROOT + indexName;
    	
        WebResource resource = Client.create()
                .resource( indexUri );
        
        JSONObject params = new JSONObject();
        params.put( "value", value );
        params.put( "uri", nodeUri.toString() );
        params.put( "key", key );
     
		ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( params.toJSONString() )
                .post( ClientResponse.class );
		
		response.getEntity(String.class);
		
		final URI location = response.getLocation();
        logger.debug( String.format(
                "POST to [%s], status code [%d], location header [%s], JSON: %s",
                indexUri, response.getStatus(), location.toString(), params.toJSONString() ) );

        response.close();
        return location;
        
    }
  
    
	@SuppressWarnings("unchecked")
	public static URI cypherQueryGetSingle( String query ) {
		
		String cypherUri = "http://localhost:7474/db/data/cypher";
    	
		WebResource resource = Client.create()
	                .resource( cypherUri );
		
	    JSONObject queryObject = new JSONObject();
	    queryObject.put( "query", query );
	    queryObject.put( "params", new JSONObject() );
	    
		//logger.info("query: "+query );
		 
		ClientResponse clientResponse = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( queryObject.toJSONString() )
                .post( ClientResponse.class );
		
		String jsonString = clientResponse.getEntity( String.class );
        
		Object JSONResponse = JSONValue.parse( jsonString );
		//logger.info("json repsonse: "+jsonString );
		
		String path = null;
		try {
			path = (String)  ((JSONObject)((JSONArray)((JSONArray)((JSONObject)JSONResponse).get("data")).get(0)).get(0)).get("self");
		} catch ( Exception e ){
        	logger.info("PROBLEM WITH CYPHER QUERY: "+query+" json response: "+JSONResponse.toString());
		}
        
        URI location = URI.create(path);
        logger.debug( String.format(
                "POST to [%s], status code [%d], location header [%s], JSON: %s",
                cypherUri, clientResponse.getStatus(), location.toString(), queryObject.toJSONString() ) );

        clientResponse.close();

        return location;
        
	}    
    
	public static URI getNodeFromIndexKey( String indexName, String key, Object value ){
    	
    	String indexUri = NODE_INDEX_ROOT + indexName + "/" + key + "/" + value.toString();
    	
        WebResource resource = Client.create()
                .resource( indexUri );
                
		ClientResponse clientResponse = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .get( ClientResponse.class );

		String jsonString = clientResponse.getEntity( String.class );
		Object JSONResponse = JSONValue.parse( jsonString );
		
        // now get the node URI out of the object
		URI location = null;
			
		try{
			
			String path = (String)((JSONObject) ((JSONArray) JSONResponse).get(0)).get("self");
			location = URI.create(path);
			
		} catch ( Exception e ) {
			// didnt find anything
			return null;
		}
		
		logger.debug( String.format(
                "GET from [%s], status code [%d], location header [%s]",
                indexUri, clientResponse.getStatus(), location.toString() ) );

        clientResponse.close();
        return location;
        
    }
	
}
