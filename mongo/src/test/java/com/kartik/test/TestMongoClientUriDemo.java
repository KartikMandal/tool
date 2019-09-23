package com.kartik.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.kartik.aggration.MongoAggration;

public class TestMongoClientUriDemo {

	@Test
	public void test() {
		//fail("Not yet implemented");
		MongoAggration mg=new MongoAggration();
		Set<String> node=new HashSet<>();
		node.add("192.168.65.251:28017");
	    node.add("192.168.65.251:28018");
	    node.add("192.168.65.251:28019");
		try {
			mg.getMongoTemplate("app", "app@Strm", "StreamPermissionService", node, "SCRAM-SHA-1", 2000, 10, 1000, "secondary()");
			
			mg.getMongoTemplate("app", "app@Strm", "StreamPermissionService", node, "SCRAM-SHA-1", 0, 10, 1000, null);
			
			mg.getMongoTemplate("app", "app@Strm", "StreamPermissionService", node, "SCRAM-SHA-1", 0, 10, 0, null);
			
			mg.getMongoTemplate("app", "app@Strm", "StreamPermissionService", node, "SCRAM-SHA-1", 0, 0, 0, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
