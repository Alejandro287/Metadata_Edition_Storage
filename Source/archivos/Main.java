package archivos;

import java.io.File;
import java.io.IOException;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import com.adobe.xmp.XMPException;
import com.drew.imaging.ImageProcessingException;

public class Main {

	public static void main(String[] args) throws ImageProcessingException, IOException, XMPException, ImageWriteException, ImageReadException {
		
		
		//new DatabaseInitializer("test", "file1");
		
		String filePathTIFF = "C:/Users/Alejandro/OneDrive - Universidad Nacional de Colombia/Documents/Universidad - Mecatrónica/Semestre 10/Proyecto Sofrony/Imagenes Pruebas/Prueba/IMG_170428_205701_0027_GRE - copia.TIF";
		String filePathJPG = "C:/Users/Alejandro/OneDrive - Universidad Nacional de Colombia/Documents/Universidad - Mecatrónica/Semestre 10/Proyecto Sofrony/Imagenes Pruebas/Prueba/IMG_170428_205701_0027_RGB - copia.JPG";
		/*MetadataManager.readMetadata(filePathTIFF);
		MetadataManager.readMetadata(filePathJPG);
		ImagesStorer.upload(filePathTIFF);
		ImagesStorer.upload(filePathJPG);*/
		MetadataManager.edit(filePathJPG, "Alejo", "perdedor", "claro que NO");
		MetadataManager.edit(filePathJPG, "Alejo", "perdedor", "claro que NO");
		MetadataManager.edit(filePathJPG, "Alejo", "perdedor", "Negativo");
		MetadataManager.edit(filePathJPG, "Alejo", "Ganador", "claro que si");
		MetadataManager.edit(filePathJPG, "Alejandrino", "jefe", "SII");
		MetadataManager.readMetadata(filePathJPG);
		/*MetadataManager.edit(filePathJPG, "Exif SubIFD", "Focal Length", "200");
		MetadataManager.edit(filePathTIFF, "Alejandro", "Ganador", "SI");
		MetadataManager.edit(filePathTIFF, "Exif SubIFD", "Focal Length", "200");
		File imageFileTIFF = new File(filePathTIFF);
		File imageFileJPG = new File(filePathJPG);
		String fileNameTIFF = imageFileTIFF.getName().split("\\.")[0];
		String fileNameJPG = imageFileJPG.getName().split("\\.")[0];
		filePathTIFF = "C:/Users/Alejandro/OneDrive - Universidad Nacional de Colombia/Documents/Universidad - Mecatrónica/Semestre 10/Proyecto Sofrony/Imagenes Pruebas/Prueba/Palma TIFF - copia.json";
		filePathJPG = "C:/Users/Alejandro/OneDrive - Universidad Nacional de Colombia/Documents/Universidad - Mecatrónica/Semestre 10/Proyecto Sofrony/Imagenes Pruebas/Prueba/Palma jpg - copia.json";
		JsonWriter.toJson(fileNameTIFF, filePathTIFF);
		JsonWriter.toJson(fileNameJPG, filePathJPG);
		filePathTIFF = "C:/Users/Alejandro/OneDrive - Universidad Nacional de Colombia/Documents/Universidad - Mecatrónica/Semestre 10/Proyecto Sofrony/Imagenes Pruebas/Prueba/IMG_170428_205701_0027_GRE - copia-1.TIF";
		filePathJPG = "C:/Users/Alejandro/OneDrive - Universidad Nacional de Colombia/Documents/Universidad - Mecatrónica/Semestre 10/Proyecto Sofrony/Imagenes Pruebas/Prueba/IMG_170428_205701_0027_GRE - copia-1.jpg";
		ImageWriter.retrieve(fileNameTIFF,filePathTIFF);
		ImageWriter.retrieve(fileNameJPG,filePathJPG);*/
	}

}
