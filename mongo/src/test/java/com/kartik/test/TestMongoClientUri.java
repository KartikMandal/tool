package com.kartik.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


public class TestMongoClientUri {

	/*@Test
	public void test() {
		MongodbInitInfo m=new MongodbInitInfo();
		m.setAuthMechanism("SCRAM-SHA-1");
		m.setDbName("StreamPermissionService");
		m.setDbId("DOCUMENTSTORE");
		Set<String> node=new HashSet<>();
		node.add("192.168.65.251:28017");
	    node.add("192.168.65.251:28018");
	    node.add("192.168.65.251:28019");
		m.setReplicaset(node);
		m.setUserName("app");
		m.setPassword("app@Strm");
		m.setMaxConnections(10);
		m.setConnectionTimeout(2000);
		m.setWriteconcern("SAFE");
	
		List<MongodbInitInfo> ml=new ArrayList<MongodbInitInfo>();
		ml.add(m);
		try {
			MongoDBManager mdb=new MongoDBManager(ml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//fail("Not yet implemented");
		
		
	}*/

}
