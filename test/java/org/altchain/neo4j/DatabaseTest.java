package org.altchain.neo4j;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import org.altchain.neo4j.database.Database;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseTest {
	
	final static Logger logger = LoggerFactory.getLogger(DatabaseTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void simpleNodeCreateTest() {

		Database d = new Database();
		
		if ( d.isServerRunning() )
			
			logger.debug("SERVER IS RUNNING.");
		
		URI firstNode = d.createNode();
        d.addProperty( firstNode, "hash", "2321" );
        d.addProperty( firstNode, "blah", "Joe Strummer" );
		
	}
	
	@Test
	public void addNodeToIndexText() {

		Database d = new Database();
		
		if ( d.isServerRunning() )
			
			logger.debug("SERVER IS RUNNING.");
		
		URI firstNode = d.createNode();
		d.addNodeToIndex(firstNode, "blocks", "hash", "eeeeeee");
		
		
	}
	
	@Test
	public void testGetNodeFromIndex() {

		UUID uniqueID = UUID.randomUUID();
		
		Database d = new Database();
		
		if ( d.isServerRunning() )
			
			logger.debug("SERVER IS RUNNING.");
		
		URI firstNode = d.createNode();
		logger.debug("MADE NODE: " + firstNode.toString() );

		d.addNodeToIndex(firstNode, "blocks", "test", uniqueID.toString() );
		URI gotNode = d.getNodeFromIndexKey("blocks", "test", uniqueID.toString() );
		
		logger.debug("GOT NODE: " + gotNode.toString() );
		
		// the two NODE URIs should be the same
		assert ( firstNode.toString().compareTo( gotNode.toString() ) == 0 );
		
	}
	
	@Test
	public void testCypherQuery() {
		
		Database d = new Database();
		
		if ( d.isServerRunning() )
			
		logger.debug("SERVER IS RUNNING.");
		
		URI rootURI = d.cypherQueryGetSingle("START root=node(0) RETURN root");  // should just return the root node
		
		logger.debug("RETURNED: " + rootURI.toString() );
		
		assert( rootURI.toString().compareTo("http://localhost:7474/db/data/node/0") == 0 );
		
	}
	
	@Test
	public void testOutputQuery() {
		
		Database d = new Database();
		
		if ( d.isServerRunning() )
			
		logger.debug("SERVER IS RUNNING.");
		
		URI rootURI = d.cypherQueryGetSingle("START tx=node:transactions(hash=\"bd8cfe1837c88caa4ca37df77380d5b0af9692ed92ddbc247cd2aef388691d4f\") MATCH (tx)-[out:has_output]-(output) where out.n = 0 return output;"
);  // should just return the root node
		
		logger.debug("OUTPUT RETURNED: " + rootURI.toString() );
		
		
		
	}
	
	

}
