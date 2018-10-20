package archivos;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.bson.Document;

public class JsonWriter extends DatabaseInitializer {
	
	public static void toJson (String fileName, String filePath) {
		//String outputFilename = filePath;                 // initialize to the path of the file to write to
		//MongoCollection<Document> collection;  // initialize to the collection from which you want to query
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			
			Document doc = collection.find(eq("Name", fileName)).first();
			
			writer.write(doc.toJson());
	        writer.newLine();

		    writer.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void allToJson (String filePath) {
		//String outputFilename = filePath;                 // initialize to the path of the file to write to
		//MongoCollection<Document> collection;  // initialize to the collection from which you want to query
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
				
		    for (Document doc : collection.find()) {
		        writer.write(doc.toJson());
		        writer.newLine();
		    }
		    writer.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	

}
