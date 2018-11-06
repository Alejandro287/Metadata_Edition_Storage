package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;



import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import com.adobe.xmp.XMPException;
import com.drew.imaging.ImageProcessingException;

import archivos.DatabaseInitializer;
import archivos.ImagesStorer;
import archivos.JsonWriter;
import archivos.MetadataManager;
import archivos.ImageWriter;

import java.awt.Font;








public class GUI extends JFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2259140747428786757L;
	JButton button ;
    JLabel label;
    private JButton btnReadMetadata;
    private JButton btnEditMetadata;
    private JTextField newDirectory;
    private JLabel lblDirectory;
    private JTextField newTag;
    private JLabel lblTag;
    private JTextField newDescription;
    private JLabel lblDescription;
    private JLabel lblName;
    private JLabel label_1;
    private JTextField databaseName;
    private JTextField collectionName;
    private JButton btnUploadImage;
    private JButton btnRetrieveImageFrom;
    private JTextField retrieveFileName;
    private JLabel lblFileName;
    private JLabel label_2;
    public static String pathFile = null;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JScrollPane scrollPane_1;
    private JTextArea collectionTextArea;
    private JButton btnDatabaseInizialiter;
    private DatabaseInitializer DB;
    private JTextField toJasonName;
    
    public GUI(){
    super("Metadata Manager");
    button = new JButton("Browse Image");
    button.setBounds(10,330,500,40);
    label = new JLabel();
    label.setBackground(Color.BLACK);
    label.setBounds(10,10,500,309);
    getContentPane().add(button);
    getContentPane().add(label);
    
    button.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        
          JFileChooser file = new JFileChooser();
          file.setCurrentDirectory(new File(System.getProperty("user.home")));
          //file.setCurrentDirectory(new File("C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba"));
         
          //filter the files
          FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png","TIF");
          file.addChoosableFileFilter(filter);
          int result = file.showSaveDialog(null);
           //if the user click on save in Jfilechooser
          if(result == JFileChooser.APPROVE_OPTION){
              File selectedFile = file.getSelectedFile();
              String path = selectedFile.getAbsolutePath();
              GUI.pathFile = path; 
              String extension = getFileExtension(selectedFile);
              if (extension.equals("TIF")) {
		    	  try {
		              final BufferedImage tif = ImageIO.read(selectedFile);
		              File minImage = new File ("minImage.png"); 
		              ImageIO.write(tif, "png", minImage);
		  			  path = minImage.getAbsolutePath();
		  			  label.setIcon(ResizeImage(path));
		              new File (path).delete();
				  } catch (IOException e1) {
					  e1.printStackTrace();
				  }
              }else {
            	  label.setIcon(ResizeImage(path));
              }
            
              
              
          }
           //if the user click on save in Jfilechooser


          else if(result == JFileChooser.CANCEL_OPTION){
              System.out.println("No File Select");
          }
        }
    });
    
    getContentPane().setLayout(null);
    
    btnReadMetadata = new JButton("Read Metadata");
    btnReadMetadata.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		try {
				String metadataStr = MetadataManager.readMetadata(GUI.pathFile);
				textArea.setText("");
				textArea.append(metadataStr);
			} catch (ImageProcessingException | IOException | XMPException e1) {
				e1.printStackTrace();
			}
    	}
    });
    btnReadMetadata.setBounds(10, 392, 500, 40);
    getContentPane().add(btnReadMetadata);
    
    btnEditMetadata = new JButton("Edit Metadata");
    btnEditMetadata.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		try {
				MetadataManager.edit(GUI.pathFile, newDirectory.getText(), newTag.getText(), newDescription.getText());
			} catch (ImageProcessingException | ImageWriteException | ImageReadException | IOException
					| XMPException e1) {
				e1.printStackTrace();
			}
    	}
    });
    btnEditMetadata.setBounds(10, 452, 117, 40);
    getContentPane().add(btnEditMetadata);
    
    newDirectory = new JTextField();
    newDirectory.setBounds(137, 462, 99, 30);
    getContentPane().add(newDirectory);
    newDirectory.setColumns(10);
    
    lblDirectory = new JLabel("Directory");
    lblDirectory.setHorizontalAlignment(SwingConstants.CENTER);
    lblDirectory.setBounds(155, 443, 63, 14);
    getContentPane().add(lblDirectory);
    
    newTag = new JTextField();
    newTag.setColumns(10);
    newTag.setBounds(246, 462, 99, 30);
    getContentPane().add(newTag);
    
    lblTag = new JLabel("Tag");
    lblTag.setHorizontalAlignment(SwingConstants.CENTER);
    lblTag.setBounds(272, 443, 46, 14);
    getContentPane().add(lblTag);
    
    newDescription = new JTextField();
    newDescription.setColumns(10);
    newDescription.setBounds(355, 462, 155, 30);
    getContentPane().add(newDescription);
    
    lblDescription = new JLabel("Description");
    lblDescription.setHorizontalAlignment(SwingConstants.CENTER);
    lblDescription.setBounds(390, 443, 84, 14);
    getContentPane().add(lblDescription);
    
    lblName = new JLabel("Database Name ");
    lblName.setHorizontalAlignment(SwingConstants.CENTER);
    lblName.setBounds(597, 82, 99, 21);
    getContentPane().add(lblName);
    
    label_1 = new JLabel("Collection Name ");
    label_1.setHorizontalAlignment(SwingConstants.CENTER);
    label_1.setBounds(762, 82, 99, 21);
    getContentPane().add(label_1);
    
    databaseName = new JTextField();
    databaseName.setBounds(585, 104, 122, 21);
    getContentPane().add(databaseName);
    databaseName.setColumns(10);
    
    collectionName = new JTextField();
    collectionName.setColumns(10);
    collectionName.setBounds(750, 104, 122, 21);
    getContentPane().add(collectionName);
    
    btnUploadImage = new JButton("Upload image");
    btnUploadImage.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		ImagesStorer.upload(GUI.pathFile);
    		String collectionText = DB.showCollection();
			collectionTextArea.setText("");
			collectionTextArea.append(collectionText);
    	}
    });
    btnUploadImage.setBounds(585, 150, 287, 40);
    getContentPane().add(btnUploadImage);
    
    btnRetrieveImageFrom = new JButton("Retrieve Image from DB");
    btnRetrieveImageFrom.addActionListener(new ActionListener() {
    	
    	public void actionPerformed(ActionEvent e) {
    			
    		JFileChooser file = new JFileChooser ();
    		
    		file.setCurrentDirectory(new File(System.getProperty("user.home")));
    		//file.setCurrentDirectory(new File("C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba"));
    		//file.showSaveDialog(null);
	         
	          //filter the files
	          FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png","TIF");
	          file.addChoosableFileFilter(filter);
	          int result = file.showSaveDialog(null);
	           //if the user click on save in Jfilechooser
	          if(result == JFileChooser.APPROVE_OPTION){
	              File selectedFile = file.getSelectedFile();
	              String path = selectedFile.getAbsolutePath();
	              
	              //Path path_Path = Paths.get(fullPath);
	              //String path = path_Path.getParent().toString();
	              
	              ImageWriter.retrieve(retrieveFileName.getText(), path);
		      	
	              GUI.pathFile = path; 
	              String extension = getFileExtension(selectedFile);
	              if (extension.equals("TIF")) {
	  		    	  try {
	  		              final BufferedImage tif = ImageIO.read(selectedFile);
	  		              File minImage = new File ("minImage.png"); 
	  		              ImageIO.write(tif, "png", minImage);
	  		  			  path = minImage.getAbsolutePath();
	  		  			  label.setIcon(ResizeImage(path));
	  		              new File (path).delete();
	  				  } catch (IOException e1) {
	  					  e1.printStackTrace();
	  				  }
	              }else {
	            	  label.setIcon(ResizeImage(path));
	              }
	              
	              
	              /**ImageWriter.retrieve(retrieveFileName.getText(),retrieveFilePath.getText());
	      		
	      		  File selectedFile = new File (retrieveFilePath.getText());
	              String path = selectedFile.getAbsolutePath();
	              GUI.pathFile = retrieveFilePath.getText(); 
	              String extension = getFileExtension(selectedFile);
	              if (extension.equals("TIF")) {
	  		    	  try {
	  		              final BufferedImage tif = ImageIO.read(selectedFile);
	  		              File minImage = new File ("minImage.png"); 
	  		              ImageIO.write(tif, "png", minImage);
	  		  			  path = minImage.getAbsolutePath();
	  		  			  label.setIcon(ResizeImage(path));
	  		              new File (path).delete();
	  				  } catch (IOException e1) {
	  					  e1.printStackTrace();
	  				  }
	              }else {
	            	  label.setIcon(ResizeImage(path));
	              }*/
 
	          }
	           //if the user click on save in Jfilechooser
	          else if(result == JFileChooser.CANCEL_OPTION){
	              System.out.println("No File Select");
	          }
		
    	}
    });
    btnRetrieveImageFrom.setBounds(10, 564, 160, 40);
    getContentPane().add(btnRetrieveImageFrom);
    
    retrieveFileName = new JTextField();
    retrieveFileName.setBounds(180, 574, 134, 30);
    getContentPane().add(retrieveFileName);
    retrieveFileName.setColumns(10);
    
    lblFileName = new JLabel("File Name to search");
    lblFileName.setHorizontalAlignment(SwingConstants.CENTER);
    lblFileName.setBounds(195, 549, 105, 14);
    getContentPane().add(lblFileName);
    
    label_2 = new JLabel("METADATA");
    label_2.setHorizontalAlignment(SwingConstants.CENTER);
    label_2.setFont(new Font("Tahoma", Font.BOLD, 15));
    label_2.setBounds(1063, 24, 180, 30);
    getContentPane().add(label_2);
    
    scrollPane = new JScrollPane();
    scrollPane.setBounds(946, 74, 414, 596);
    getContentPane().add(scrollPane);
    
    textArea = new JTextArea();
    scrollPane.setViewportView(textArea);
    
    scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(520, 245, 416, 425);
    getContentPane().add(scrollPane_1);
    
    collectionTextArea = new JTextArea();
    scrollPane_1.setViewportView(collectionTextArea);
    
    btnDatabaseInizialiter = new JButton("MONGO DATABASE INITIALIZER");
    btnDatabaseInizialiter.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		DB = new DatabaseInitializer(databaseName.getText(), collectionName.getText());
    		String collectionText = DB.showCollection();
			collectionTextArea.setText("");
			collectionTextArea.append(collectionText);
    	}
    });
    btnDatabaseInizialiter.setFont(new Font("Tahoma", Font.BOLD, 15));
    btnDatabaseInizialiter.setBounds(585, 19, 287, 44);
    getContentPane().add(btnDatabaseInizialiter);
    
    JLabel lblDatabasejsonFormat = new JLabel("DATABASE (JSON Format)");
    lblDatabasejsonFormat.setHorizontalAlignment(SwingConstants.CENTER);
    lblDatabasejsonFormat.setFont(new Font("Tahoma", Font.BOLD, 15));
    lblDatabasejsonFormat.setBounds(613, 215, 231, 30);
    getContentPane().add(lblDatabasejsonFormat);
    
    JButton btnItemFromDb = new JButton("DB item to JSON File");
    btnItemFromDb.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		
    		JFileChooser file = new JFileChooser ();
    		
    		file.setCurrentDirectory(new File(System.getProperty("user.home")));
    		//file.setCurrentDirectory(new File("C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba"));
    		//file.showSaveDialog(null);
	         
	          //filter the files
	          FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png","TIF");
	          file.addChoosableFileFilter(filter);
	          int result = file.showSaveDialog(null);
	           //if the user click on save in Jfilechooser
	          if(result == JFileChooser.APPROVE_OPTION){
	              File selectedFile = file.getSelectedFile();
	              String path = selectedFile.getAbsolutePath();
	              
	              //Path path_Path = Paths.get(fullPath);
	              //String path = path_Path.getParent().toString();
	              
	              JsonWriter.toJson(toJasonName.getText(),path);

	          }
	           //if the user click on save in Jfilechooser
	          else if(result == JFileChooser.CANCEL_OPTION){
	              System.out.println("No File Select");
	          }

    	}
    });
    btnItemFromDb.setBounds(10, 630, 160, 40);
    getContentPane().add(btnItemFromDb);
    
    toJasonName = new JTextField();
    toJasonName.setColumns(10);
    toJasonName.setBounds(180, 640, 134, 30);
    getContentPane().add(toJasonName);
    
    JLabel lblItemName = new JLabel("Item Name to search");
    lblItemName.setHorizontalAlignment(SwingConstants.CENTER);
    lblItemName.setBounds(195, 615, 105, 14);
    getContentPane().add(lblItemName);
    
    JButton btnDbToJson = new JButton("Entire DB to JSON File");
    btnDbToJson.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		
    		
    		
    		JFileChooser file = new JFileChooser ();
    		
    		file.setCurrentDirectory(new File(System.getProperty("user.home")));
    		//file.setCurrentDirectory(new File("C:\\Users\\Alejandro\\OneDrive - Universidad Nacional de Colombia\\Documents\\Universidad - Mecatrónica\\Semestre 10\\Proyecto Sofrony\\Imagenes Pruebas\\Prueba"));
    		//file.showSaveDialog(null);
	         
	          //filter the files
	          FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png","TIF");
	          file.addChoosableFileFilter(filter);
	          int result = file.showSaveDialog(null);
	           //if the user click on save in Jfilechooser
	          if(result == JFileChooser.APPROVE_OPTION){
	              File selectedFile = file.getSelectedFile();
	              String path = selectedFile.getAbsolutePath();
	              
	              //Path path_Path = Paths.get(fullPath);
	              //String path = path_Path.getParent().toString();
	              
	              JsonWriter.allToJson(path);
	          }
	           //if the user click on save in Jfilechooser
	          else if(result == JFileChooser.CANCEL_OPTION){
	              System.out.println("No File Select");
	          }

    	}
    });
    btnDbToJson.setBounds(324, 635, 186, 35);
    getContentPane().add(btnDbToJson);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    this.setLocation(0, 0);
    setSize(1500,720);
    setVisible(true);
    }
     
     // Methode to resize imageIcon with the same size of a Jlabel
    public ImageIcon ResizeImage(String ImagePath)
    {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
    
    public static void main(String[] args){
        new GUI();
    }
   }


///////////////END


/////OUTPUT:
