package archivos;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRationals;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShortOrLong;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShorts;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoUnknowns;
import org.apache.commons.imaging.formats.tiff.write.TiffImageWriterLossless;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.XMPSchemaRegistryImpl;
import com.adobe.xmp.options.SerializeOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.Tag;
import com.drew.metadata.xmp.XmpDirectory;

public class MetadataManager {
	
	public static String readMetadata(String filePath) throws ImageProcessingException, IOException, XMPException {
		
		File file = new File(filePath);
		
		DataInputStream stream = new DataInputStream(new FileInputStream(file));
		Metadata metadata = ImageMetadataReader.readMetadata(stream);
		
		stream.close();
		
		String metadataStr = "";
		
		for (Directory directory : metadata.getDirectories()) {
					
					if (directory.getName() == "XMP") {
			    		Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);
			    		for (XmpDirectory xmpDirectory : xmpDirectories) {
			    		    XMPMeta xmpMeta = xmpDirectory.getXMPMeta();
			    		    XMPIterator iterator = xmpMeta.iterator();
			    		    while (iterator.hasNext()) {
			    		        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo)iterator.next();
			    		        
			    		        if (xmpPropertyInfo.getValue() != null && xmpPropertyInfo.getValue() != "") {
			    		        
				    		        System.out.format("[%s] - %s = %s",
				    			            directory.getName(), xmpPropertyInfo.getPath(), xmpPropertyInfo.getValue());
				    			    System.out.print("\n");
				    			    
				    			    metadataStr += "\n["+ directory.getName()+"] - "+ xmpPropertyInfo.getPath()+" = "+xmpPropertyInfo.getValue();
			    		        }
			    		    }
			    		}
			    		
			    		continue;
			    	}
					
				    for (Tag tag : directory.getTags()) {
				    	
				    	metadataStr += "\n["+ directory.getName()+"] - "+ tag.getTagName()+" = "+tag.getDescription();
				        System.out.format("[%s] - %s = %s",
				            directory.getName(), tag.getTagName(), tag.getDescription());
				        System.out.print("\n");
				    }
			}
		
		return metadataStr;
	}
	
	
	
	public static void edit(String filePath, String newDirectory, String newTag, String newDescription) throws ImageProcessingException, IOException, ImageWriteException, ImageReadException, XMPException {
		
		File file = new File(filePath);
		InputStream inputstream = new FileInputStream(file);
		DataInputStream stream = new DataInputStream(inputstream);
		
		Metadata metadata = ImageMetadataReader.readMetadata(stream);
		
		
		System.out.println("System.out.println(metadata.toString());");
		System.out.println(metadata.toString());

        String imageType = null; 
        stream.close();
        
        for (Directory directory : metadata.getDirectories()) {
        	if(directory.getName().equals("File Type")) {
        		for (Tag tag : directory.getTags()) {
        			if (tag.getTagName().equals("Detected File Type Name")) {
        				imageType = tag.getDescription();
        				System.out.println("Image Type:");
        				System.out.println(imageType);
        				
        				break;
        			}
        		}
        		break;
        	}
        	
        }
		
		boolean containsDirectory = false;  
		boolean containsTag = false; 
		
		for (Directory directory : metadata.getDirectories()) {			
			if (directory.getName().equals(newDirectory)) {
				System.out.println("directory.getName()");
				System.out.println(directory.getName());
				
				for (Tag tag : directory.getTags()) {				
					if (tag.getTagName().equals(newTag)) {
						System.out.println("tag.getTagName()");
						System.out.println(tag.getTagName());
						
						if (directory.getName().equals("GPS")) {
							for (TagInfo tagInfo : GpsTagConstants.ALL_GPS_TAGS) {
								if(tag.getTagType() == tagInfo.tag) {
									System.out.println("tagInfo.tag");
									System.out.println(tagInfo.tag);
									
									change(file, imageType, tagInfo.tag, newDescription, tagInfo);
									break;
								}
							}
						}else if(directory.getName().equals("Exif SubIFD")) {
							for (TagInfo tagInfo : ExifTagConstants.ALL_EXIF_TAGS) {
								if(tag.getTagType() == tagInfo.tag) {
									System.out.println("tagInfo.tag");
									System.out.println(tagInfo.tag);
									
									change(file, imageType, tagInfo.tag, newDescription, tagInfo);
									break;
								}else if (tag.getTagType() == 37393){
									
									System.out.println("tagInfo.tag");
									System.out.println(tagInfo.tag);
									
									//tagInfo.tag = 41489 
									change(file, imageType, 41489, newDescription, ExifTagConstants.EXIF_TAG_IMAGE_NUMBER);
									break;
								}
							}
						}else if(directory.getName().equals("Exif IFD0")) {
							for (TagInfo tagInfo : TiffTagConstants.ALL_TIFF_TAGS) {
								if(tag.getTagType() == tagInfo.tag) {
									System.out.println("tagInfo.tag");
									System.out.println(tagInfo.tag);
									
									change(file, imageType, tagInfo.tag, newDescription, tagInfo);
									break;
								}
							}
						}
						
						containsTag = true; 
						break;
					}
					
					if (!containsTag) {
						// Pendiente
					}
				}
				containsDirectory = true; 
				break;
			}
			
			
		}
		
		System.out.println("containsDirectory");
		System.out.println(containsDirectory);
		
		System.out.println("containsTag");
		System.out.println(containsTag);
		
		/*if (!containsDirectory) {
			
			String xmpXml = Imaging.getXmpXml(file);
			
			System.out.println("xmpXml");
			System.out.println(xmpXml);

			
			for (Directory directory : metadata.getDirectories()) {
				
				if (directory.getName() == "XMP") {
		    		Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);
		    		for (XmpDirectory xmpDirectory : xmpDirectories) {
		    		    XMPMeta xmpMeta = xmpDirectory.getXMPMeta();
		    		    XMPIterator iterator = xmpMeta.iterator();
		    		    while (iterator.hasNext()) {
		    		        XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo)iterator.next();

		    		        if (xmpPropertyInfo.getValue() != null && xmpPropertyInfo.getValue() != "") {
			    		        
			    		        if (xmpPropertyInfo.getPath().equals(newDirectory+":"+newTag)) {
			    		        	if(!xmpPropertyInfo.getValue().equals(newDescription)) {
			    		        		
			    		        		String spliter = newDirectory+":"+newTag+"\""+xmpPropertyInfo.getValue()+"\""; 
			    			            String[] parts = xmpXml.split(spliter);
			    			            String union = parts[0]+newDirectory+":"+newTag+"\""+newDescription+"\"\n"+parts[1];
			    			            
			    			            TiffOutputSet outputSet = outputSetType (file,imageType);
			    			    		save(outputSet, file, imageType, union);
			    		        	}
			    		        	containsDirectory = true; 
			    		        	break;
			    		        }
		    		        }
		    		    }
		    		 }
		    		
		    		 break;
		    	 }
		     }
	    }*/
		
		if (!containsDirectory) {
			
			String xmpXml = Imaging.getXmpXml(file);
			
			
			//XMPMeta newXMP = XMPMetaFactory.create();
			
			//XMPMeta oldXMP = XMPMetaFactory.parse(inputstream);
			XMPMeta oldXMP2 = XMPMetaFactory.parseFromString(xmpXml);
			System.out.println(XMPMetaFactory.serializeToString(oldXMP2 , new SerializeOptions()));
			//new XMPSchemaRegistryImpl().registerNamespace("http://ns.myname.com/Alejo/1.0/", "Alejo");
			//newXMP.setProperty("http://ns.myname.com/Alejo/1.0/", "Alejo", "Claro que SI");
			
			
			String spliter = "</rdf:Description>"; 
			//String spliter = "\n >\n"; 
            String[] parts = xmpXml.split(spliter);
            //String union = parts[0]+"\n "+newDirectory+":"+newTag+"=\""+newDescription+"\""+spliter+parts[1];
            String union = parts[0]+spliter+"\n<rdf:Description>\n"+"<"+newDirectory+":"+newTag+">"+newDescription+"</"+newDirectory+":"+newTag+">\n"+spliter+parts[1];
 
            String newXMPStr = "<?xpacket begin=\"ï»¿\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\r\n" + 
            "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"XMP Core 4.4.0\">\r\n" + 
            "   <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\r\n" + 
            "      <rdf:Description rdf:about=\"\"\r\n" + 
            "            xmlns:"+newDirectory+"=\"http://ns.myname.com/"+newDirectory+"/1.0/\">\r\n" + 
            "         <"+newDirectory+":"+newTag+">"+newDescription+"</"+newDirectory+":"+newTag+">\r\n" + 
            "      </rdf:Description>\r\n"+
            "   </rdf:RDF>\r\n" + 
            "</x:xmpmeta>\r\n"+
            "<?xpacket end=\"w\"?>";
            
            
            String adicion = "\n      <rdf:Description rdf:about=\"\"\r\n" + 
            "            xmlns:Alejo=\"http://ns.myname.com/Alejo/1.0/\">\r\n" + 
            "         <Alejo:Ganador>SIII</Alejo:Ganador>\r\n" + 
            "      </rdf:Description>";
            
            //union = parts[parts.length-2]+spliter+adicion+parts[parts.length-1];
            
           
            
            XMPMeta newXMP = XMPMetaFactory.parseFromString(newXMPStr);
            
            //System.out.println(XMPMetaFactory.serializeToString(newXMP , new SerializeOptions()));
            
            XMPUtils.appendProperties(oldXMP2,newXMP, true, true, true);
			
			
			union = XMPMetaFactory.serializeToString(newXMP , new SerializeOptions());
			
			 System.out.println("Union:");
	         System.out.println(union);
            
            TiffOutputSet outputSet = outputSetType (file,imageType);
    		save(outputSet, file, imageType, union);

		}
		
	}
	
	
	public static void change(File file, String imageType, int tagNum, String description, TagInfo tagInfo) throws ImageReadException, IOException, ImageWriteException {
		
		TiffOutputSet outputSet = outputSetType (file,imageType);
		
		TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
        TiffOutputDirectory GPSDirectory = outputSet.getOrCreateGPSDirectory();
        TiffOutputDirectory RootDirectory = outputSet.getOrCreateRootDirectory();
		Object newDescription = new Object();
		
		switch (tagNum) {
			case 0:
				newDescription = String2IntList2BytesList(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoBytes) tagInfo, (byte[]) newDescription);
				break;
			case 1:
				newDescription = description;
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 2:
				newDescription = StringList2rationalNumberList(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoRationals) tagInfo, (RationalNumber[]) newDescription);
				break;
			case 3:
				newDescription = description;
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 4:
				newDescription = StringList2rationalNumberList(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoRationals) tagInfo, (RationalNumber[]) newDescription);
				break;
			case 5:
				newDescription = String2Int2Byte(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoByte) tagInfo, (Byte) newDescription);
				break;
			case 6:
				newDescription = String2rationalNumber(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoRationals) tagInfo, (RationalNumber) newDescription);
				break;
			case 7:
				newDescription = StringList2rationalNumberList(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoRationals) tagInfo, (RationalNumber[]) newDescription);
				break;
			case 9:
				newDescription = description;
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 12:
				newDescription = description;
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 13:
				newDescription = String2rationalNumber(description);
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoRationals) tagInfo, (RationalNumber) newDescription);
				break;
			case 18:
				newDescription = description;
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 29:
				newDescription = description;
				GPSDirectory.removeField(tagInfo);
				GPSDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 256:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 257:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 258:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 259:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 262:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 270:
				newDescription = description;
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 271:
				newDescription = description;
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 272:
				newDescription = description;
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 273:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 274:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 277:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 279:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShortOrLong) tagInfo, (Short) newDescription);
				break;
			case 280:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShorts) tagInfo, (Short) newDescription);
				break;
			case 284:
				newDescription = String2Short(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 305:
				newDescription = description;
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 306:
				newDescription = description;
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 700:
				newDescription = String2IntList2BytesList(description);
				RootDirectory.removeField(tagInfo);
				RootDirectory.add((TagInfoBytes) tagInfo, (byte[]) newDescription);
				break;
			case 50713:
				break;
			case 50714:
				break;
			case 50735:
				break;
			case 50827:
				break;
			case 33434:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRationals) tagInfo, (RationalNumber) newDescription);
				break;
			case 33437:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRationals) tagInfo, (RationalNumber) newDescription);
				break;
			case 34852:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 34855:
				newDescription = String2Short(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoShorts) tagInfo, (Short) newDescription);
				break;
			case 36867:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 36868:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 37377:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRational) tagInfo, (RationalNumber) newDescription);
				break;
			case 37378:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRational) tagInfo, (RationalNumber) newDescription);
				break;
			case 37381:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRational) tagInfo, (RationalNumber) newDescription);
				break;
			case 37386:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRationals) tagInfo, (RationalNumber) newDescription);
				break;
			case 41489:
				newDescription = String2Int2Byte(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoUnknowns) tagInfo, (Byte) newDescription);
				break;
			case 37520:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 37521:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 37522:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 40962:
				newDescription = String2Short(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 40963:
				newDescription = String2Short(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 41486:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRational) tagInfo, (RationalNumber) newDescription);
				break;
			case 41487:
				newDescription = String2rationalNumber(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoRational) tagInfo, (RationalNumber) newDescription);
				break;
			case 41488:
				newDescription = String2Short(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 41986:
				newDescription = String2Short(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 41989:
				newDescription = String2Short(description);
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoShort) tagInfo, (Short) newDescription);
				break;
			case 42016:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
			case 42033:
				newDescription = description;
				exifDirectory.removeField(tagInfo);
				exifDirectory.add((TagInfoAscii) tagInfo, (String) newDescription);
				break;
		}
		
		save(outputSet, file, imageType);
	}
	
	public static Short String2Short(String description) {
		return Short.parseShort(description);
	}
	
	public static RationalNumber String2rationalNumber(String description) {
		double value = Double.parseDouble(description);
		return RationalNumber.valueOf(value);
	}
	
	public static Byte String2Int2Byte(String description) {
		Integer value = Integer.parseInt(description);
		return value.byteValue();
	}
	
	public static Byte[] String2IntList2BytesList(String description) {
		String[] stringList = description.split(",");
		Byte[] byteList = new Byte[stringList.length];
		for(int i = 0; i<stringList.length ; i++) {
			byteList[i] = String2Int2Byte(stringList[i]);
		}
		return byteList;
	}
	
	public static RationalNumber[] StringList2rationalNumberList(String description) {
		description = description.replaceAll("\\s","");
		String[] stringList = description.split(",");
		RationalNumber[] rationalList = new RationalNumber[stringList.length];
		for(int i = 0; i<stringList.length ; i++) {
			rationalList[i] = String2rationalNumber(stringList[i]);
		}
		return rationalList;
	}
	
	public static void save(TiffOutputSet outputSet, File file, String imageType) throws ImageReadException, ImageWriteException, IOException {
		save(outputSet, file, imageType, null);
	}
	
	public static void save(TiffOutputSet outputSet, File file, String imageType, String union) throws IOException, ImageReadException, ImageWriteException {
		
		String path = file.getCanonicalPath();
		
		String fileName = file.getName();
		
		
		System.out.println("outputSet.toString()");
		System.out.println(outputSet.toString());
		
		if (imageType.equals("TIFF") || imageType.equals("ARW")) {
			
			System.out.println("imageType");
			System.out.println(imageType);
			

			String tempPath = path.split(fileName)[0]+"temp.TIF";
			File tempFile = new File(tempPath);
			
			System.out.println("tempPath");
			System.out.println(tempPath);
			
			final BufferedImage image = Imaging.getBufferedImage(file);
			
			Map<String, Object> params = new HashMap<>();

            if (null != union) {
            	System.out.println("UNION NO NULL");
            	TiffOutputDirectory RootDirectory = outputSet.getOrCreateRootDirectory();
                final byte[] xmpXmlBytes = union.getBytes("utf-8");
                RootDirectory.removeField(TiffTagConstants.TIFF_TAG_XMP);
                RootDirectory.add(TiffTagConstants.TIFF_TAG_XMP, xmpXmlBytes);
                params.put(ImagingConstants.PARAM_KEY_XMP_XML, union);
            }
            
            System.out.println("outputSet.toString()");
            System.out.println(outputSet.toString());

            params.put(ImagingConstants.PARAM_KEY_EXIF, outputSet);
            params.put(ImagingConstants.PARAM_KEY_COMPRESSION, TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED_1);
            params.put(ImagingConstants.PARAM_KEY_FORMAT, ImageFormats.TIFF);

            //Imaging.writeImage(image, tempFile, ImageFormats.TIFF, params);

    
			byte[] imageBytes = Imaging.writeImageToBytes(image, ImageFormats.TIFF, params);

            try(FileOutputStream fos = new FileOutputStream(tempFile);
                OutputStream os = new BufferedOutputStream(fos)) {
                new TiffImageWriterLossless(imageBytes).write(os, outputSet);
            }
            
            
            System.out.println("file.delete()");
            System.out.println("tempFile.renameTo(new File(path))");
	   		
	   		System.out.println(file.delete());
            System.out.println(tempFile.renameTo(new File(path)));
			
		}else if (imageType.equals("JPEG")) {
			
			System.out.println("imageType");
			System.out.println(imageType);
			
			String tempPath = path.split(fileName)[0]+"temp.jpg";
			File tempFile = new File(tempPath);
			
			System.out.println("tempPath");
			System.out.println(tempPath);
			
			FileOutputStream fos = new FileOutputStream(tempFile);
            OutputStream os = new BufferedOutputStream(fos);
	   		
	   		new ExifRewriter().updateExifMetadataLossless(file, os, outputSet);
	   		
	   		fos.close();
	   		os.close();
	   		
	   		if (null != union) {
	   			System.out.println("outputSet.toString()");
	   			System.out.println(outputSet.toString());
	   			
		   		FileOutputStream foss = new FileOutputStream(tempFile);
	            OutputStream oss = new BufferedOutputStream(foss);
		   		new JpegXmpRewriter().updateXmpXml(file, oss, union);
		   		foss.close();
		   		oss.close();
	   		}
	   		
	   		System.out.println("file.delete()");
            System.out.println("tempFile.renameTo(new File(path))");
	   		
	   		System.out.println(file.delete());
            System.out.println(tempFile.renameTo(new File(path)));
		}

	}
	
	public static TiffOutputSet outputSetType (File file, String imageType) throws ImageWriteException, ImageReadException, IOException {
		ImageMetadata metadataWrite = Imaging.getMetadata(file);
		TiffOutputSet outputSet = new TiffOutputSet();
		
		if (imageType.equals("TIFF")) {
			TiffImageMetadata TiffMetadata = (TiffImageMetadata) metadataWrite;
	        outputSet = TiffMetadata.getOutputSet();
	        System.out.println("outputSet.toString()");
	        System.out.println(outputSet.toString());
	        outputSet = getMetadata(file, outputSet);
		}else if (imageType.equals("JPEG")) {
	   		JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadataWrite;
	   		TiffImageMetadata exif = jpegMetadata.getExif();
	   		outputSet = exif.getOutputSet();
		}else if (imageType.equals("ARW")) {
			TiffImageMetadata TiffMetadata = (TiffImageMetadata) metadataWrite;
	        outputSet = TiffMetadata.getOutputSet();
	        System.out.println("outputSet.toString()");
	        System.out.println(outputSet.toString());
		}
		return outputSet;
	}

   public static TiffOutputSet getMetadata(File file, TiffOutputSet outputSet) {
		 try
		 {
			
		    TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            TiffOutputDirectory GPSDirectory = outputSet.getOrCreateGPSDirectory();
            TiffOutputDirectory RootDirectory = outputSet.getOrCreateRootDirectory();
			
			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			
			Metadata metadata = ImageMetadataReader.readMetadata(stream);
			
			stream.close();
			
			for (Directory directory : metadata.getDirectories()) {
				System.out.println(directory.getName());
				System.out.print("\n");
				if (directory.getName().equals("GPS")) {
			
				    for (Tag tag : directory.getTags()) {
				    	
				    	/*System.out.format("[%s] - %s = %s",
					            directory.getName(), tag.getTagName(), tag.getDescription());
				    	    System.out.print("\n");
				    	    System.out.println(directory.getObject(tag.getTagType()).getClass());
					        System.out.print("\n");*/

				    	switch (tag.getTagName()) {
				    	
				    		case "GPS Version ID":
				    			short [] nums = ((short [])directory.getObject(tag.getTagType()));
				    			byte[] partsByte = new byte[nums.length];
				    			for (int i=0;i<nums.length;i++) {
				    				Short short1=new Short((short) nums[i]);
				    				partsByte[i]=short1.byteValue();
				    			}
				    			
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_VERSION_ID);				    			
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_VERSION_ID, partsByte);
				    			break;
				    		case "GPS Latitude Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Latitude":
				    			Rational[] rationalLatitude = (Rational[]) directory.getObject(tag.getTagType());
				    			RationalNumber[] rationalNumberLatitude = new RationalNumber [rationalLatitude.length];
				    			for (int i=0;i<rationalLatitude.length;i++) {
				    				rationalLatitude[i] = rationalLatitude[i].getSimplifiedInstance();
				    				rationalNumberLatitude [i] = new RationalNumber((int) rationalLatitude[i].getNumerator(),(int) rationalLatitude[i].getDenominator());
				    				
				    				//System.out.println("\n" + rationalNumberLatitude[i].toString());
				    				//System.out.println("\n" + rationalLatitude[i].toString());
				    			}
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LATITUDE);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_LATITUDE, rationalNumberLatitude);
				    			break;
				    		case "GPS Longitude Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Longitude":
				    			Rational[] rationalLongitude = (Rational[]) directory.getObject(tag.getTagType());
				    			RationalNumber[] rationalNumberLongitude = new RationalNumber [rationalLongitude.length];
				    			for (int i=0;i<rationalLongitude.length;i++) {
				    				rationalLongitude[i] = rationalLongitude[i].getSimplifiedInstance();
				    				rationalNumberLongitude [i] = new RationalNumber((int) rationalLongitude[i].getNumerator(),(int) rationalLongitude[i].getDenominator());
				    				
				    				//System.out.println("\n" + rationalNumberLongitude[i].toString());
				    				//System.out.println("\n" + rationalLongitude[i].toString());
				    			}
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_LONGITUDE, rationalNumberLongitude);
				    			break;
				    		case "GPS Altitude Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE_REF, ((Integer) directory.getObject(tag.getTagType())).byteValue());
				    			break;
				    		case "GPS Altitude":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_ALTITUDE);
				    			Rational rationalAltitude = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_ALTITUDE,new RationalNumber((int) rationalAltitude.getNumerator(),(int) rationalAltitude.getDenominator()));
				    			break;
				    		case "GPS Time-Stamp":
				    			Rational[] rationalTimeStamp = (Rational[]) directory.getObject(tag.getTagType());
				    			RationalNumber[] rationalNumberTimeStamp = new RationalNumber [rationalTimeStamp.length];
				    			for (int i=0;i<rationalTimeStamp.length;i++) {
				    				rationalTimeStamp[i] = rationalTimeStamp[i].getSimplifiedInstance();
				    				rationalNumberTimeStamp [i] = new RationalNumber((int) rationalTimeStamp[i].getNumerator(),(int) rationalTimeStamp[i].getDenominator());
				    				
				    				//System.out.println("\n" + rationalNumberTimeStamp[i].toString());
				    				//System.out.println("\n" + rationalTimeStamp[i].toString());
				    			}
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_TIME_STAMP);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_TIME_STAMP, rationalNumberTimeStamp);
				    			break;
				    		case "GPS Satellites":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_SATELLITES);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_SATELLITES, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Status":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_STATUS);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_STATUS, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Measure Mode":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_MEASURE_MODE);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_MEASURE_MODE, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS DOP":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DOP);
				    			Rational rationalDOP = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DOP,new RationalNumber((int) rationalDOP.getNumerator(),(int) rationalDOP.getDenominator()));
				    			break;
				    		case "GPS Speed Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_SPEED_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_SPEED_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Speed":
				    			Rational rationalSpeed = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_SPEED);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_SPEED,new RationalNumber((int) rationalSpeed.getNumerator(),(int) rationalSpeed.getDenominator()));
				    			break;
				    		case "GPS Track Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_TRACK_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_TRACK_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Track":
				    			Rational rationalTrack = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_TRACK);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_TRACK,new RationalNumber((int) rationalTrack.getNumerator(),(int) rationalTrack.getDenominator()));
				    			break;
				    		case "GPS Img Direction Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Img Direction":
				    			Rational rationalImgDirection = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_IMG_DIRECTION);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_IMG_DIRECTION,new RationalNumber((int) rationalImgDirection.getNumerator(),(int) rationalImgDirection.getDenominator()));
				    			break;
				    		case "GPS Map Datum":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_MAP_DATUM);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_MAP_DATUM, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Dest Latitude Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_LATITUDE_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_LATITUDE_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Dest Latitude":
				    			Rational[] rationalDestLatitude = (Rational[]) directory.getObject(tag.getTagType());
				    			RationalNumber[] rationalNumberDestLatitude = new RationalNumber [rationalDestLatitude.length];
				    			for (int i=0;i<rationalDestLatitude.length;i++) {
				    				rationalDestLatitude[i] = rationalDestLatitude[i].getSimplifiedInstance();
				    				rationalNumberDestLatitude [i] = new RationalNumber((int) rationalDestLatitude[i].getNumerator(),(int) rationalDestLatitude[i].getDenominator());
				    				
				    				//System.out.println("\n" + rationalNumberDestLatitude[i].toString());
				    				//System.out.println("\n" + rationalDestLatitude[i].toString());
				    			}
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_LATITUDE);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_LATITUDE, rationalNumberDestLatitude);
				    			break;
				    		case "GPS Dest Longitude Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_LONGITUDE_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_LONGITUDE_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Dest Longitude":
				    			Rational[] rationalDestLongitude = (Rational[]) directory.getObject(tag.getTagType());
				    			RationalNumber[] rationalNumberDestLongitude = new RationalNumber [rationalDestLongitude.length];
				    			for (int i=0;i<rationalDestLongitude.length;i++) {
				    				rationalDestLongitude[i] = rationalDestLongitude[i].getSimplifiedInstance();
				    				rationalNumberDestLongitude [i] = new RationalNumber((int) rationalDestLongitude[i].getNumerator(),(int) rationalDestLongitude[i].getDenominator());
				    				
				    				//System.out.println("\n" + rationalNumberDestLongitude[i].toString());
				    				//System.out.println("\n" + rationalDestLongitude[i].toString());
				    			}
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_LONGITUDE);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_LONGITUDE, rationalNumberDestLongitude);
				    			break;
				    		case "GPS Dest Bearing Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_BEARING_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_BEARING_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Dest Bearing":
				    			Rational rationalDestBearing = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_BEARING);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_BEARING,new RationalNumber((int) rationalDestBearing.getNumerator(),(int) rationalDestBearing.getDenominator()));
				    			break;
				    		case "GPS Dest Distance Ref":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_DISTANCE_REF);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_DISTANCE_REF, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Dest Distance":
				    			Rational rationalDestDistance = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DEST_DISTANCE);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DEST_DISTANCE,new RationalNumber((int) rationalDestDistance.getNumerator(),(int) rationalDestDistance.getDenominator()));
				    			break;
				    		case "GPS Date Stamp":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DATE_STAMP);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DATE_STAMP, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
				    		case "GPS Differential":
				    			GPSDirectory.removeField(GpsTagConstants.GPS_TAG_GPS_DIFFERENTIAL);
				    			GPSDirectory.add(GpsTagConstants.GPS_TAG_GPS_DIFFERENTIAL, ((Short) directory.getObject(tag.getTagType())));
				    			break;
				    		//faltaron los de clase TagInfoGpsText
				    	
				    	}

				    }
				}else if (directory.getName().equals("Exif SubIFD")) {
					for (Tag tag : directory.getTags()) {
						
						/*System.out.format("[%s] - %s = %s",
			            directory.getName(), tag.getTagName(), tag.getDescription());
			    	    System.out.print("\n");
			    	    System.out.println(directory.getObject(tag.getTagType()).getClass());
				        System.out.print("\n");
				   
				        System.out.println(tag.getTagTypeHex());
				        System.out.print("\n");
							*/
						switch (tag.getTagTypeHex()) {
						
							case "0x0001":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_INTEROPERABILITY_INDEX);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_INTEROPERABILITY_INDEX, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
							case "0x0002":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_INTEROPERABILITY_VERSION);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_INTEROPERABILITY_VERSION, ((Short) directory.getObject(tag.getTagType())).byteValue());
				    			break;
							case "0x000b":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_PROCESSING_SOFTWARE);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_PROCESSING_SOFTWARE, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
							case "0x0131":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SOFTWARE);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_SOFTWARE, ((StringValue) directory.getObject(tag.getTagType())).toString());
				    			break;
							case "0x829a":
				    			Rational rationalExposureTime = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_EXPOSURE_TIME, new RationalNumber((int) rationalExposureTime.getNumerator(),(int) rationalExposureTime.getDenominator()));
								break;
							case "0x829d":
				    			Rational rationalFNumber = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FNUMBER);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_FNUMBER, new RationalNumber((int) rationalFNumber.getNumerator(),(int) rationalFNumber.getDenominator()));
								break;
							case "0x8824":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SPECTRAL_SENSITIVITY);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_SPECTRAL_SENSITIVITY, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x8827":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_ISO);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_ISO, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x9003":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x9004":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x9201":
				    			Rational rationalShutter = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE, new RationalNumber((int) rationalShutter.getNumerator(),(int) rationalShutter.getDenominator()));
								break;
							case "0x9202":
				    			Rational rationalAperture = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE, new RationalNumber((int) rationalAperture.getNumerator(),(int) rationalAperture.getDenominator()));
								break;
							case "0x9205":
				    			Rational rationalApertureMax = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_MAX_APERTURE_VALUE);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_MAX_APERTURE_VALUE, new RationalNumber((int) rationalApertureMax.getNumerator(),(int) rationalApertureMax.getDenominator()));
								break;
							case "0x920a":
				    			Rational rationalFocalLength = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH, new RationalNumber((int) rationalFocalLength.getNumerator(),(int) rationalFocalLength.getDenominator()));
								break;
							case "0x9211":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_IMAGE_NUMBER);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_IMAGE_NUMBER, ((Long) directory.getObject(tag.getTagType())).byteValue());
								break;
							case "0x9290":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SUB_SEC_TIME);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_SUB_SEC_TIME, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x9291":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_ORIGINAL);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_ORIGINAL, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x9292":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_DIGITIZED);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_DIGITIZED, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0xa002":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, ((Integer) directory.getObject(tag.getTagType())).shortValue());
								break;
							case "0xa003":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, ((Integer) directory.getObject(tag.getTagType())).shortValue());
								break;
							case "0xa20e":
				    			Rational rationalFocalPlaneXResolution = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FOCAL_PLANE_XRESOLUTION_EXIF_IFD);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_FOCAL_PLANE_XRESOLUTION_EXIF_IFD, new RationalNumber((int) rationalFocalPlaneXResolution.getNumerator(),(int) rationalFocalPlaneXResolution.getDenominator()));
								break;
							case "0xa20f":
				    			Rational rationalFocalPlaneYResolution = ((Rational) directory.getObject(tag.getTagType())).getSimplifiedInstance();
				    			exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FOCAL_PLANE_YRESOLUTION_EXIF_IFD);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_FOCAL_PLANE_YRESOLUTION_EXIF_IFD, new RationalNumber((int) rationalFocalPlaneYResolution.getNumerator(),(int) rationalFocalPlaneYResolution.getDenominator()));
								break;
							case "0xa210":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FOCAL_PLANE_RESOLUTION_UNIT_EXIF_IFD);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_FOCAL_PLANE_RESOLUTION_UNIT_EXIF_IFD, ((Integer) directory.getObject(tag.getTagType())).shortValue());
								break;
							case "0xa402":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_EXPOSURE_MODE);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_EXPOSURE_MODE, ((Integer) directory.getObject(tag.getTagType())).shortValue());
								break;
							case "0xa405":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH_IN_35MM_FORMAT);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_FOCAL_LENGTH_IN_35MM_FORMAT, ((Integer) directory.getObject(tag.getTagType())).shortValue());
								break;
							case "0xa420":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_IMAGE_UNIQUE_ID);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_IMAGE_UNIQUE_ID, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0xa431":
								exifDirectory.removeField(ExifTagConstants.EXIF_TAG_BODY_SERIAL_NUMBER);
								exifDirectory.add(ExifTagConstants.EXIF_TAG_BODY_SERIAL_NUMBER, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
						}
						
					/*System.out.format("[%s] - %s = %s",
			            directory.getName(), tag.getTagName(), tag.getDescription());
			        System.out.print("\n");*/
						
					}
				}else if (directory.getName().equals("Exif IFD0")) {
					for (Tag tag : directory.getTags()) {
						
						/*System.out.format("[%s] - %s = %s",
			            directory.getName(), tag.getTagName(), tag.getDescription());
			    	    System.out.print("\n");
			    	    System.out.println(directory.getObject(tag.getTagType()).getClass());
				        System.out.print("\n");
				   
				        System.out.println(tag.getTagTypeHex());
				        System.out.print("\n");
							*/
						switch (tag.getTagTypeHex()) {
						
							case "0x0100":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_IMAGE_WIDTH, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0101":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_IMAGE_LENGTH, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0102":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_BITS_PER_SAMPLE, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0103":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_COMPRESSION);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_COMPRESSION, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0106":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_PHOTOMETRIC_INTERPRETATION);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_PHOTOMETRIC_INTERPRETATION, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x010e":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_IMAGE_DESCRIPTION, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x010f":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_MAKE);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_MAKE, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x0110":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_MODEL);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_MODEL, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x0111":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_STRIP_OFFSETS);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_STRIP_OFFSETS, ((Long) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0112":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_ORIENTATION);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_ORIENTATION, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0115":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_SAMPLES_PER_PIXEL);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_SAMPLES_PER_PIXEL, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0117":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_STRIP_BYTE_COUNTS);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_STRIP_BYTE_COUNTS, ((Long) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0118":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_MIN_SAMPLE_VALUE);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_MIN_SAMPLE_VALUE, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x011c":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_PLANAR_CONFIGURATION);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_PLANAR_CONFIGURATION, ((Integer) directory.getObject(tag.getTagType())).shortValue());
				    			break;
							case "0x0131":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_SOFTWARE);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_SOFTWARE, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
							case "0x0132":
								RootDirectory.removeField(TiffTagConstants.TIFF_TAG_DATE_TIME);
								RootDirectory.add(TiffTagConstants.TIFF_TAG_DATE_TIME, ((StringValue) directory.getObject(tag.getTagType())).toString());
								break;
						}
					}
				}
			    
	    	
			    if (directory.hasErrors()) {
			        for (String error : directory.getErrors()) {
			            System.err.format("ERROR: %s", error);
			        }
			    }
			    
			}
			
						
			return outputSet;
			
		}
		 
		 
		 
	  catch( Exception e )
	  {
	      e.printStackTrace();
	  }
		 return null;

	}

}
