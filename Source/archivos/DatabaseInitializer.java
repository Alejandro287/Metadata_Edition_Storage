package archivos;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

public class DatabaseInitializer {
	
	private static MongoClient mongoClient;
	private static MongoDatabase db;
	protected static MongoCollection<Document> collection;
	protected static GridFSBucket gridFSBucket;
	
	public DatabaseInitializer() {};
	
	public DatabaseInitializer(String nameDB, String nameCollection) {
		DatabaseInitializer.mongoClient = new MongoClient();
		DatabaseInitializer.db = mongoClient.getDatabase(nameDB);
		db.drop();
		DatabaseInitializer.collection = db.getCollection(nameCollection);
	    collection.drop();
	    DatabaseInitializer.gridFSBucket = GridFSBuckets.create(db, nameCollection+"GridFS");
	    gridFSBucket.drop();
	}
	
	
	public String showCollection() {
		String collectionStr = "";  
		MongoCursor<Document> cursor = collection.find().iterator();
		try {
		    while (cursor.hasNext()) {
		    	
		    	String item = "\n "+cursor.next().toJson();
		    	String spliter = "\"Image binary data:\" :";
		    	String [] parts = item.split(spliter);
		    	String part2 = parts[1].substring(parts[1].indexOf("}"));
		    	item = parts[0]+spliter+" \"...\" "+part2;
		    	item = item.replaceAll("\\{", "\n\\{\n\r");
		    	item = item.replaceAll("\",", "\",\n\r");
		    	item = item.replaceAll("\\},", "\n\\},\n\r");
		    	item = item.replaceAll("\\}\n,", "\n\\}\n\r");
		    	item = item.replaceAll("\\} ", "\n\\}\n\r");
		    	parts = item.split("\n");
		    	int tabCounter = 0; 
		    	item = "";
		    	System.out.println("parts.length : " + parts.length);
		    	for(int i = 0 ; i < parts.length ; i++) {
		    		
		    		if (parts[i].equals("}") || parts[i].equals("},")) {
		    			tabCounter--; 
		    			System.out.println("tabCounter-- : " + tabCounter);
		    		}
		    		
		    		String tab = ""; 
		    		for(int j = 1 ; j <= tabCounter ; j++) {
		    			tab += "\t";
		    		}
		    		
		    		if (parts[i].equals("{")) {
		    			tabCounter++; 
		    			System.out.println("tabCounter++ : " + tabCounter);
		    		}
		    		
		    		item += tab+parts[i]+"\n";
		    	}
		        System.out.println(item);
		        collectionStr += item;
		    }
		} finally {
		    cursor.close();
		}
		return collectionStr;
	}
	
	
	public static MongoClient getMongoClient() {
		return mongoClient;
	}
	public static void setMongoClient(MongoClient mongoClient) {
		DatabaseInitializer.mongoClient = mongoClient;
	}
	public static MongoDatabase getDb() {
		return db;
	}
	public static void setDb(MongoDatabase db) {
		DatabaseInitializer.db = db;
	}
	public static MongoCollection<Document> getCollection() {
		return collection;
	}
	public static void setCollection(MongoCollection<Document> collection) {
		DatabaseInitializer.collection = collection;
	}
	

}
