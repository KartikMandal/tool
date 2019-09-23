package com.kartik.aggration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mongodb.AuthenticationMechanism;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public class MongoAggration {

	
	public MongoTemplate getMongoTemplate(String auth_user,String auth_pwd,String db_name,Set<String> host,String authMechanisum,int connectionTimeOut,int maxConnection,int socketTimeOut,String readPref) throws Exception {
		MongoTemplate mongoTemplate = null;
		String user = null;
		String password = null;
		String dbName=db_name;
		try {
			user = URLEncoder.encode(auth_user, "UTF-8");
			password = URLEncoder.encode(auth_pwd, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("MongoExecutor: Failed to encode user-name and password using UTF-8");
		}
		StringBuilder sb=new StringBuilder();
		int i=0;
			for (String node: host){
				if(i<host.size()-1){
				sb.append(node).append(',');
				}else{
					sb.append(node);
				}
			i++;
		}
		String clientUrl = "mongodb://" + user + ":" + password + "@" + sb.toString() + "/"
					+ dbName;
		if (authMechanisum != null) {
			clientUrl =clientUrl+ "?authMechanism=" + authMechanisum;
		} 
		
		ReadPreference preference = ReadPreference.primary();
		if (readPref != null) {
			try {
				Class<?> c = Class.forName("com.mongodb.ReadPreference");
				Method m = c.getMethod(readPref, null);
				preference = (ReadPreference) m.invoke(null, new Object[0]);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		
	MongoClientURI uri = new MongoClientURI(clientUrl,MongoClientOptions.builder().connectionsPerHost(maxConnection)
			.connectTimeout(connectionTimeOut).socketTimeout(socketTimeOut)
			.readPreference(preference));
		// Connecting to the mongodb server using the given client uri.
		System.out.println(clientUrl);
		System.out.println(uri.getURI());
		System.out.println(uri.getOptions().getConnectionsPerHost());
		System.out.println(uri.getCredentials().getAuthenticationMechanism());
		MongoClient mongoClient=new MongoClient(uri);
		SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient,"StreamPermissionService");
	   // MongoClientURI uri = new MongoClientURI(client_url);
	    //MongoClient m=new MongoClient(uri);
	   // MongoDatabase mm= m.getDatabase("StreamPermissionService");
	  //  DBCollection s= (DBCollection) mm.getCollection("SPS_USER_STATS");
		mongoTemplate = new MongoTemplate(new MongoClient(uri),"StreamPermissionService");
		return mongoTemplate;
		
	}
	
	
	
/*public MongoTemplate getMongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = null;
		// Mongodb initialization parameters.
        int port_no = 28017;
        String auth_user="app", auth_pwd = "app@Strm", host_name = "IN-L1155", db_name = "StreamPermissionService", db_col_name = "emp", encoded_pwd = "";
        try {
            encoded_pwd = URLEncoder.encode(auth_pwd, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        	System.out.println();       
        }
 
        // Mongodb connection string.
        String client_url = "mongodb://" + auth_user + ":" + encoded_pwd + "@192.168.65.251:28017,192.168.65.251:28018,192.168.65.251:28019"+ "/" + db_name+"?authMechanism=SCRAM-SHA-1&connectTimeoutMS=300000&replicaSet=envest&";
        MongoClientURI uri = new MongoClientURI(client_url);
 MongoClient m=new MongoClient(uri);
MongoDatabase mm= m.getDatabase("StreamPermissionService");
DBCollection s= (DBCollection) mm.getCollection("SPS_USER_STATS");
        
		mongoTemplate = new MongoTemplate(new MongoClient(uri),"StreamPermissionService");
		return mongoTemplate;
		
	}*/



public MongoTemplate getMongoTemplate() throws Exception {
	MongoTemplate mongoTemplate = null;
	// Mongodb initialization parameters.
    int port_no = 28017;
    String auth_user="app", auth_pwd = "app@Strm", host_name = "IN-L1155", db_name = "StreamPermissionService", db_col_name = "emp", encoded_pwd = "";
    try {
        encoded_pwd = URLEncoder.encode(auth_pwd, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
    	System.out.println();       
    }

    // Mongodb connection string.
   // String client_url = "mongodb://" + auth_user + ":" + encoded_pwd + "@192.168.65.251:28017,192.168.65.251:28018,192.168.65.251:28019"+ "/" + db_name+"?authMechanism=SCRAM-SHA-1&connectTimeoutMS=300000&replicaSet=envest&";
    
    String client_url = "mongodb://" + auth_user + ":" + encoded_pwd + "@192.168.65.251:28017,192.168.65.251:28018,192.168.65.251:28019"+ "/" + db_name+"?authMechanism=SCRAM-SHA-1";
	//mongodb://app:app%40Strm@192.168.65.251:28019,192.168.65.251:28017,192.168.65.251:28018/StreamPermissionService?authMechanism=SCRAM-SHA-1
    ReadPreference preference = ReadPreference.primary();
	
		try {
			Class<?> c = Class.forName("com.mongodb.ReadPreference");
			Method m = c.getMethod("primary", null);
			preference = (ReadPreference) m.invoke(null, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	

	WriteConcern writeConcern = WriteConcern.valueOf("SAFE");
	//writeConcern = WriteConcern.valueOf("SAFE".toUpperCase());
	
	
	MongoClientURI uri = new MongoClientURI(client_url,MongoClientOptions.builder().connectionsPerHost(20)
			.connectTimeout(1000).socketTimeout(1000)
			.readPreference(preference));
	System.out.println(client_url);
	System.out.println(uri.getURI());
	System.out.println(uri.getOptions().getConnectionsPerHost());
	System.out.println(uri.getCredentials().getAuthenticationMechanism());
	MongoClient mongoClient=new MongoClient(uri);
	SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient,"StreamPermissionService");
   // MongoClientURI uri = new MongoClientURI(client_url);
    //MongoClient m=new MongoClient(uri);
   // MongoDatabase mm= m.getDatabase("StreamPermissionService");
  //  DBCollection s= (DBCollection) mm.getCollection("SPS_USER_STATS");
	mongoTemplate = new MongoTemplate(new MongoClient(uri),"StreamPermissionService");
	return mongoTemplate;
	
}



/*public MongoTemplate getMongoTemplate() throws Exception {
	MongoTemplate mongoTemplate = null;
	// Mongodb initialization parameters.
    int port_no = 28017;
    String auth_user="app", auth_pwd = "app@Strm", db_name = "StreamPermissionService";
    try {
        encoded_pwd = URLEncoder.encode(auth_pwd, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
    	System.out.println();       
    }
    MongoCredential credential = MongoCredential.createCredential(auth_user, db_name, auth_pwd.toCharArray()).withMechanism(AuthenticationMechanism.valueOf("SCRAM_SHA_1"));

	List<ServerAddress> serversList = new ArrayList<ServerAddress>();
	serversList.add(new ServerAddress("192.168.65.251", 28017));
	 Set<String> node=new HashSet<>();
	    node.add("192.168.65.251:28018");
	    node.add("192.168.65.251:28019");
	Set<String> replicaset = node;
	if (replicaset != null && !replicaset.isEmpty()) {
		for (String replica : replicaset) {
			String[] rep = replica.split(":");
			serversList.add(new ServerAddress(rep[0], Integer.parseInt(rep[1])));
		}
	}
   
	
    ReadPreference preference = ReadPreference.primary();
	
		try {
			Class<?> c = Class.forName("com.mongodb.ReadPreference");
			Method m = c.getMethod("primary", null);
			preference = (ReadPreference) m.invoke(null, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	

	WriteConcern writeConcern = WriteConcern.valueOf("SAFE");
	//writeConcern = WriteConcern.valueOf("SAFE".toUpperCase());
	
	MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(20)
			.connectTimeout(1000).socketTimeout(1000)
			.readPreference(preference).build();
	MongoClient mongoClient = new MongoClient(serversList, credential, options);
	
	    
   // MongoClientURI uri = new MongoClientURI(client_url);
    //MongoClient m=new MongoClient(uri);
   // MongoDatabase mm= m.getDatabase("StreamPermissionService");
  //  DBCollection s= (DBCollection) mm.getCollection("SPS_USER_STATS");
	mongoTemplate = new MongoTemplate(mongoClient,"StreamPermissionService");
	return mongoTemplate;
	
}*/

public static void doSomthing(Long cobrandId,Long userId) throws Exception{
	MongoAggration mm = new MongoAggration();
	MongoTemplate mongoTemplate = mm.getMongoTemplate();
	
	DBObject ob=new BasicDBObject();
    ((BasicDBObject) ob).put("batchSize",1);
	Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where(SpsUserPermissionData.cobrandId).is(cobrandId).and(SpsUserPermissionData.userId).is(userId)),
    		Aggregation.group(SpsUserPermissionData.userId).count().as("count"),
    		Aggregation.project("count").and("cobrandId").previousOperation()
    		//Aggregation.sort(Sort.Direction.DESC, "total")
		);
		//Convert the aggregation result into a List
		AggregationResults<Document> groupResults = mongoTemplate.aggregate(agg, "SPS_USER_PERMISSION_DATA", Document.class);
		List<Document> ss=groupResults.getMappedResults();
		System.out.println(ss.size());
		long count=0;
		for(Document totalAccrossSiteLimit : ss){
			if(totalAccrossSiteLimit.get("count")!=null){
			count = Long.valueOf(totalAccrossSiteLimit.get("count").toString());
			}
		}
		System.out.println(count);
}


public static void doSomthingWith(Long cobrandId,Long userId,Long id) throws Exception{
	MongoAggration mm = new MongoAggration();
	MongoTemplate mongoTemplate = mm.getMongoTemplate();
	 Criteria c=Criteria.where(SpsUserStats.cobrandId).is(cobrandId).and(SpsUserStats.userId).is(userId);
	c=c.and(SpsUserStats.siteId).is(id);
	
	Aggregation agg = Aggregation.newAggregation(Aggregation.match(c),
 		Aggregation.group(SpsUserStats.total,SpsUserStats.success,SpsUserStats.failure).count().as("kartik")
 		//Aggregation.group(SpsUserStats.failure).count().as("failure"),
 		//Aggregation.project("total").and(SpsUserStats.cobrandId).previousOperation()
 		//Aggregation.project("failure").and(SpsUserStats.cobrandId).previousOperation()
 		//Aggregation.sort(Sort.Direction.DESC, "total")
		);
		//Convert the aggregation result into a List
		AggregationResults<Document> groupResults = mongoTemplate.aggregate(agg, SpsUserStats.collectionName, Document.class);
		List<Document> list=groupResults.getMappedResults();
		System.out.println();
}



public static void doUpdateSomthingWith(Long cobrandId,Long userId,Long id) throws Exception{
	MongoAggration mm = new MongoAggration();
	MongoTemplate mongoTemplate = mm.getMongoTemplate();
	 Criteria c=Criteria.where(SpsUserStats.cobrandId).is(cobrandId).and(SpsUserStats.userId).is(userId);
	c=c.and(SpsUserStats.siteId).is(id);
	Query q=new Query();
	q.addCriteria(c);
	//mongoTemplate.find(q, Document.class,  SpsUserStats.collectionName);
	Update update=new Update();
	update.inc(SpsUserStats.failure, 1);
	//UpdateResult up=mongoTemplate.upsert(q, update, SpsUserStats.collectionName);	
	SpsUserStats s=mongoTemplate.findAndModify(q, update, SpsUserStats.class, SpsUserStats.collectionName);//updateMultipleSpsUserStats failure
	doSomthingWith(cobrandId,userId,id);
	/*if(up.getModifiedCount()>0){
		System.out.println();
	}*/
	System.out.println();
		
}


public static void doInsertSomthingWithInster(Long cobrandId,Long userId,Long id) throws Exception{
	MongoAggration mm = new MongoAggration();
	MongoTemplate mongoTemplate = mm.getMongoTemplate();
	MongoCollection<Document> cobrandConfigCollection =mongoTemplate.getCollection(SpsUserStats.collectionName);
	
	Document d=new Document();
	d.put(SpsUserStats.cobrandId, cobrandId);
	d.put(SpsUserStats.userId, userId);
	d.put(SpsUserStats.siteId, id);
	d.put(SpsUserStats.success, 0);
	d.put(SpsUserStats.failure, 1);
	d.put(SpsUserStats.total, 1);
	SpsUserStats s=new SpsUserStats();
	Criteria c=Criteria.where(SpsUserStats.cobrandId).is(cobrandId).and(SpsUserStats.userId).is(userId);
	c=c.and(SpsUserStats.siteId).is(id);
	Query q=new Query();
	q.addCriteria(c);
	//mongoTemplate.find(q, Document.class,  SpsUserStats.collectionName);
	Update update=new Update();
	update.inc(SpsUserStats.failure, 1);
	//update.
	cobrandConfigCollection.insertOne(d);
	//mongoTemplate.save(person);
	MongoOperations mongoOperation = mongoTemplate;
	//mongoOperation.upsert(query, update, collectionName);
	System.out.println();
	//d.append(key, value);
	//d.remove(key)
		
}

private static final String SUFFIX = "/";
public static void amazonS3(){
//https://javatutorial.net/java-s3-example
	// credentials object identifying user for authentication
			// user must have AWSConnector and AmazonS3FullAccess for 
			// this example to work
			AWSCredentials credentials = new BasicAWSCredentials("AKIARF6YJ2SZTQZLSZNM", "fsuB4JpJzBa5UJBkO9Glb1XVAzJ8L921rhuA/VQE");
			
			// create a client connection based on credentials
			AmazonS3 s3client = new AmazonS3Client(credentials);
			
			// create bucket - name must be unique for all S3 users
			String bucketName = "duster";
			//s3client.createBucket(bucketName);
			
			// list buckets
			for (Bucket bucket : s3client.listBuckets()) {
				System.out.println(" - " + bucket.getName());
			}
			// create folder into bucket
			String folderName = "testfolder";
			createFolder(bucketName, folderName, s3client);
			
			// upload file to folder and set it to public
			String fileName = folderName + SUFFIX + "21616520_2431318393863597_6276379937811525596_n.jpg";
			s3client.putObject(new PutObjectRequest(bucketName, fileName, 
					new File("C:\\Users\\kmandal\\Desktop\\Good photo\\21616520_2431318393863597_6276379937811525596_n.jpg"))
					.withCannedAcl(CannedAccessControlList.PublicRead));
			
			deleteFolder(bucketName, folderName, s3client);
			
			// deletes bucket
			s3client.deleteBucket(bucketName);
		}
		
		public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
			// create meta-data for your folder and set content-length to 0
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(0);
			// create empty content
			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
			// create a PutObjectRequest passing the folder name suffixed by /
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
					folderName + SUFFIX, emptyContent, metadata);
			// send request to S3 to create folder
			client.putObject(putObjectRequest);
		}
		/**
		 * This method first deletes all the files in given folder and than the
		 * folder itself
		 */
		public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
			List<S3ObjectSummary> fileList = 
					client.listObjects(bucketName, folderName).getObjectSummaries();
			for (S3ObjectSummary file : fileList) {
				client.deleteObject(bucketName, file.getKey());
			}
			client.deleteObject(bucketName, folderName);
		}


	public static void main(String[] args) throws Exception {
		amazonS3();
		//doSomthing(549772l,867691l);
		//doSomthingWith(549772l,867691l,769328l);
		//doInsertSomthingWithInster(549772l,867691l,769328l);
		//doUpdateSomthingWith(549772l,867691l,769328l);
		
	}

}
