package archivos;

import java.util.Set;

import javax.swing.JOptionPane;

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
		
		boolean DBexist = false;
		boolean collectionExist = false;
		
		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while(dbsCursor.hasNext()) {
			if (dbsCursor.next().equals(nameDB)) {
				DBexist = true;
				break;
			}
		}
		
		if (!DBexist) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to create a new database called "+nameDB+"?","Warning",dialogButton);
			if(dialogResult == JOptionPane.YES_OPTION){
				DatabaseInitializer.db = mongoClient.getDatabase(nameDB);
				//db.drop();
			}
		}else {
			DatabaseInitializer.db = mongoClient.getDatabase(nameDB);
			//db.drop();
		}
		
		MongoCursor<String> collectionNames = db.listCollectionNames().iterator();
		while(collectionNames.hasNext()) {
			if (collectionNames.next().equals(nameCollection)) {
				collectionExist = true;
				break;
			}
		}
		
		if (!collectionExist) {
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to create a new collection called "+nameCollection+"?","Warning",dialogButton);
			if(dialogResult == JOptionPane.YES_OPTION){
				DatabaseInitializer.collection = db.getCollection(nameCollection);
			    //collection.drop();
			}
		}else {
			DatabaseInitializer.collection = db.getCollection(nameCollection);
		    //collection.drop();
		}
		
	    DatabaseInitializer.gridFSBucket = GridFSBuckets.create(db, nameCollection+"GridFS");
	    //gridFSBucket.drop();
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
