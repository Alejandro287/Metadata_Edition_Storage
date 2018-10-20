package archivos;

import static com.mongodb.client.model.Filters.eq;

import java.io.FileOutputStream;
import java.io.IOException;

import org.bson.Document;
import org.bson.types.Binary;

public class ImageWriter extends DatabaseInitializer {
	
	public static Document retrieve(String fileName, String filePath)
    {
        Binary c;
        byte d[];
        Document obj = null;
        try
        {
        	obj = collection.find(eq("Name", fileName)).first();
        	/*System.out.println(obj.toJson());
            String n = (String)obj.get("Name");
            System.out.println(n);
            System.out.println(fileName);*/
            c = obj.get("Image binary data:", org.bson.types.Binary.class);
            //System.out.println(c);
            FileOutputStream fout = new FileOutputStream(filePath);
            d = c.getData();
            fout.write(d);
            fout.flush();
            System.out.println("\nPhoto of "+fileName+" retrieved and stored at "+filePath);
            fout.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
	
	public static void retrieveGridFS(String fileName, String filePath) throws IOException {
		
		FileOutputStream streamToDownloadTo = new FileOutputStream(filePath);
		gridFSBucket.downloadToStream(fileName, streamToDownloadTo);
		streamToDownloadTo.close();
		System.out.println(streamToDownloadTo.toString());
	}

}
