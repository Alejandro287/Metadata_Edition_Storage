package archivos;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.xmp.XmpDirectory;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;



public class ImagesStorer extends DatabaseInitializer {
	
	public ImagesStorer() {};
		
	/*void withoutUsingGridFS() throws IOException 
    {
        try {
  
        	MongoClient mongo = new MongoClient();
    		MongoDatabase db = mongo.getDatabase("Images");
    		db.drop();
    	    MongoCollection<Document> collection = db.getCollection("dummyColl");
 	       	    
    	    collection.drop();
             
            String filename = "C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba\\IMG1 - copia.jpg";
            String empname ="IMG1 - copia";
             
            *//** Inserts a record with name = empname and photo 
              *  specified by the filepath 
              **//*
            insert(empname,filename,collection);
            
            filename = "C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba\\IMG1 - copia5.jpg";
            empname ="IMG1 - copia5";
            
            insert(empname,filename,collection);
            
            empname ="IMG1 - copia";
             
            String destfilename = "C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba\\IMG1 - copia6.jpg";
            *//** Retrieves record where name = empname, including his photo. 
              * Retrieved photo is stored at location filename 
              **//*
            retrieve(empname, destfilename, collection);
            
            
            String outputFilename = "C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba\\IMG1 - copia6.json";                 // initialize to the path of the file to write to
    		//MongoCollection<Document> collection;  // initialize to the collection from which you want to query

    		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));

    	
    	    for (Document doc : collection.find()) {
    	        writer.write(doc.toJson());
    	        writer.newLine();
    	    }
    	    writer.close();
            
            
            mongo.close();
             
        } catch (MongoException e) {
            e.printStackTrace();
        } 
    }*/
	
	public static Document upload(String filePath)
	{
		File imageFile = new File(filePath);
		return upload(filePath,imageFile.getName().split("\\.")[0]);
	}
	
	public static ObjectId uploadGridFS(String filePath)
	{
		File imageFile = new File(filePath);
		return uploadGridFS(filePath,imageFile.getName().split("\\.")[0]);
	}
     
     
    @SuppressWarnings("unchecked")
	public static Document upload(String filePath,String fileName)
    {
    	Document doc = null;
    	try
        {
        	File imageFile = new File(filePath);
        	if ( imageFile.length() >= 16000000 ) {
        		System.out.println("\nFile size greater than the limit (16MB)\n");
        		System.out.println("\nImage will be uploaded by GridFS\n");
        		uploadGridFS(filePath,fileName);
        	}else{
	        	FileInputStream f = new FileInputStream(imageFile);
	 
	            byte b[] = new byte[f.available()];
	            f.read(b);
	 
	            Binary data = new Binary(b);
	            
	            Document o = new  Document();
	            JSONObject obj = new JSONObject();
	            
	            o = o.append("Name",fileName);
	            
	            Object[] respuestas = new Object [2];
	        	respuestas = getMetadata(imageFile);
	        	obj = (JSONObject) obj.put("Metadata", respuestas[0]);	      
	        	doc = o.append("Metadata", respuestas[1]);
	        	doc.toJson();
	            
	            doc = o.append("Image binary data:",data);
	            /*obj = ((JSONObject) obj.put("Name",fileName));
	            obj = (JSONObject) obj.put("Image binary data:", ":)");*/
	            //System.out.println("Inserted record.");
	            
	            f.close();

	        	//System.out.println(doc.toJson());
	        	
	        	collection.insertOne(doc);
        	}
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return doc;
    }
    
    @SuppressWarnings("unchecked")
	public static ObjectId uploadGridFS(String filePath,String fileName) {
    	  //DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    	  ObjectId fileId = null;
    	  try {
    	  File imageFile = new File(filePath);
    	  InputStream inputStream = new FileInputStream(imageFile);
    	  
    	  Object[] respuestas = new Object [2];
      	  respuestas = getMetadata(imageFile);
    	  
    	   // Create some custom options
    	   //GridFSUploadOptions uploadOptions = new GridFSUploadOptions().chunkSizeBytes(1024).metadata(new Document("Metadata", respuestas[1]));
      	  GridFSUploadOptions uploadOptions = new GridFSUploadOptions().metadata(new Document("Metadata", respuestas[1]));
    	  fileId = gridFSBucket.uploadFromStream(fileName, inputStream, uploadOptions);
    	  
    	  JSONObject obj = new JSONObject();
    	  
      	  obj = (JSONObject) obj.put("Metadata", respuestas[0]);	      
      	      	 
    	  } catch (Exception e) {
    	   e.printStackTrace();
    	  } 
    	  return fileId;
    }
    
	@SuppressWarnings("unchecked")
	public static Object[]  getMetadata(File file) {
		 try
		 {
			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			
			Metadata metadata = ImageMetadataReader.readMetadata(stream);
			
			JSONObject obj1 = new JSONObject();
			
			Document doc1 = new Document();
			

			for (Directory directory : metadata.getDirectories()) {
						
				JSONObject obj2 = new JSONObject();
				Document doc2 = new Document();
				
				if (directory.getName() == "XMP") {
		    		Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);
		    		for (XmpDirectory xmpDirectory : xmpDirectories) {
		    		    XMPMeta xmpMeta = xmpDirectory.getXMPMeta();
		    		    XMPIterator iterator = xmpMeta.iterator();
		    		    while (iterator.hasNext()) {
		    		        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo)iterator.next();
		    		        
		    		        if (xmpPropertyInfo.getValue() != null && xmpPropertyInfo.getValue() != "") {
		    		        
			    		        /*System.out.format("[%s] - %s = %s",
			    			            directory.getName(), xmpPropertyInfo.getPath().split(":")[1].split("\\[1]")[0], xmpPropertyInfo.getValue());
			    			    System.out.print("\n");*/
			    			    
			    			    obj2.put(xmpPropertyInfo.getPath().split(":")[1].split("\\[1]")[0], xmpPropertyInfo.getValue());
						        //doc2.append(xmpPropertyInfo.getPath().split(":")[1].split("\\[1]")[0], xmpPropertyInfo.getValue());
			    			      doc2.append(xmpPropertyInfo.getPath().split(":")[1], xmpPropertyInfo.getValue());
		    		        }
		    		    }
		    		}
		    		obj1.put(directory.getName(), obj2);
				    doc1.append(directory.getName(), doc2);
				    
		    		continue;
		    	}
							
			    for (Tag tag : directory.getTags()) {
			    	
			        obj2.put(tag.getTagName(), tag.getDescription());
			        doc2.append(tag.getTagName(), tag.getDescription());
			        
			    }
			    if (directory.hasErrors()) {
			        for (String error : directory.getErrors()) {
			            System.err.format("ERROR: %s", error);
			        }
			    }
			    
			    obj1.put(directory.getName(), obj2);
			    doc1.append(directory.getName(), doc2);
			    
			}
			
			return new Object[] {obj1, doc1};
			
		}
	   catch( Exception e )
	   {
	       e.printStackTrace();
	   }
		 return new Object[] {null, null};

	}
}
