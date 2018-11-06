package gui.view;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.sun.media.jfxmedia.logging.Logger;

import gui.FilePathTreeItem;
import gui.GUI_FX;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.util.ArrayList;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;

import javafx.scene.control.Label;

import javafx.scene.control.TextArea;

import javafx.scene.control.TreeItem;

import javafx.scene.control.TreeItem.TreeModificationEvent;

import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javafx.event.EventHandler;

public class FXMLDocumnetController implements Initializable{
	
	@FXML
	private TreeView <String> treeView;
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private AnchorPane anchorImageView;
	
	private File fileSelected; 
	
	private boolean initial = false;  
	private File defaultDirectory;
	
	
	public static Image folderCollapseImage;
    public static Image folderExpandImage;
    public static Image fileImage;
    public static Image imageFileImage;
	
    @FXML
	private void handleFileChooser (ActionEvent event) throws IOException {
    	
    	FileChooser fc = new FileChooser ();
    	fc.setTitle("Metadata Manager");
    	fc.getExtensionFilters().add(new ExtensionFilter ("Image Files", "*.TIF","*.jpg"));
    	if (!this.initial){
    		defaultDirectory = new File("c:/");
    		this.initial=true;
    	}
    	if (!defaultDirectory.isDirectory()) {
    		defaultDirectory = defaultDirectory.getParentFile();
    	}
    	fc.setInitialDirectory(defaultDirectory);
    	File f = fc.showOpenDialog(GUI_FX.getPrimaryStage());
    	this.fileSelected = f;
    	if (f != null && !(f.isDirectory())) {
    		try {
                BufferedImage bufferedImage = ImageIO.read(f);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                this.imageView.setImage(image);
            } catch (IOException ex) {}
    	}else if (f.isDirectory()) {
    		BufferedImage bufferedImage = ImageIO.read(new File ("folder-icon.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            this.imageView.setImage(image);
    	}
    	
    	defaultDirectory = f;
    }
    
    @FXML
	private void handleDirectoryChooser (ActionEvent event) throws IOException {
    	
    	DirectoryChooser chooser = new DirectoryChooser();
    	chooser.setTitle("Metadata Manager");
    	//chooser.getExtensionFilters().add(new ExtensionFilter ("Image Files", "*.TIF","*.jpg"));
    	if (!this.initial){
    		defaultDirectory = new File("c:/");
    		this.initial=true;
    	}
    	
    	if (!defaultDirectory.isDirectory()) {
    		defaultDirectory = defaultDirectory.getParentFile();
    	}
    	
    	chooser.setInitialDirectory(defaultDirectory);
    	File f = chooser.showDialog(GUI_FX.getPrimaryStage());
    	this.fileSelected = f;
    	if (f != null && !(f.isDirectory())) {
    		try {
                BufferedImage bufferedImage = ImageIO.read(f);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                this.imageView.setImage(image);
            } catch (IOException ex) {}
    	}else if (f.isDirectory()) {
    		BufferedImage bufferedImage = ImageIO.read(new File ("folder-icon.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            this.imageView.setImage(image);
    	}
    	
    	defaultDirectory = f;
    }
    
    
	@FXML
	private void handleButtonAction (ActionEvent event) throws FileNotFoundException {
		System.out.println("You clicked me!");

		

	    /*this.addEventHandler(TreeItem.branchCollapsedEvent(),new EventHandler(){
	        @Override
	        public void handle(Event e){
	            FilePathTreeItem source=(FilePathTreeItem)e.getSource();
	            if(source.isDirectory()&&!source.isExpanded()){
	                ImageView iv=(ImageView)source.getGraphic();
	                iv.setImage(folderCollapseImage);
	            }
	        }
	    });*/
		
	}
	
	
	/*@FXML
	private void addEventHandler(TreeItem.branchExpandedEvent(),new EventHandler()){
        @Override
        public void handle(Event e){
        	System.out.println("Hola");
            FilePathTreeItem source=(FilePathTreeItem)e.getSource();
            if(source.isDirectory()&&source.isExpanded()){
                ImageView iv=(ImageView)source.getGraphic();
                iv.setImage(folderExpandImage);
                System.out.println("Hola");
            }
            try{
                if(source.getChildren().isEmpty()){
                	System.out.println("Hola2");
                    Path path=Paths.get(source.getFullPath());
                    BasicFileAttributes attribs=Files.readAttributes(path,BasicFileAttributes.class);
                    if(attribs.isDirectory()){
                    	System.out.println("Hola3");
                        DirectoryStream<Path> dir=Files.newDirectoryStream(path);
                        for(Path file:dir){
                        	System.out.println(file.toString());
                            FilePathTreeItem treeNode=new FilePathTreeItem(file);
                            source.getChildren().add(treeNode);
                        }
                    }
                }else{
                    //if you want to implement rescanning a directory for changes this would be the place to do it
                }
            }catch(IOException x){
                x.printStackTrace();
            }
        }   
    });
	*/
	
	

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		String hostName="computer";
        try{hostName=InetAddress.getLocalHost().getHostName();}catch(UnknownHostException x){}
        
        //InputStream is = GUI_FX.class.getResourceAsStream("view/computer.png");

		try {
			 InputStream is = new BufferedInputStream(new FileInputStream("icons/computer.png"));
			 Image image1 = new Image(is);
	         ImageView imageView = new ImageView(image1);
	         TreeItem<String> rootNode=new TreeItem<>(hostName,imageView);
	         this.treeView.setRoot(rootNode);
	         Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();
	         for(Path name:rootDirectories){
	         	System.out.println(name.toString());
	         	FilePathTreeItem treeNode = new FilePathTreeItem(name);
	         	rootNode.getChildren().add(treeNode);
	         	//tree(name, rootNode);
	         }
	         rootNode.setExpanded(true);
	         
	         
	        this.folderCollapseImage=new Image(new BufferedInputStream(new FileInputStream("icons/folder.png")));
	 	    this.folderExpandImage=new Image(new BufferedInputStream(new FileInputStream("icons/folder-open.png")));
	 	    this.fileImage=new Image(new BufferedInputStream(new FileInputStream("icons/text-x-generic.png")));
	 	    this.imageFileImage=new Image(new BufferedInputStream(new FileInputStream("icons/image-x-generic.png")));
	 	    
	 	    
	 	   BufferedImage bufferedImage = ImageIO.read(new File ("Default-image.jpg"));
           Image image = SwingFXUtils.toFXImage(bufferedImage, null);
           this.imageView.setImage(image);
           
           this.imageView.setPreserveRatio(false);
           this.imageView.autosize();
           this.imageView.fitWidthProperty().bind(anchorImageView.widthProperty());
           this.imageView.fitHeightProperty().bind(anchorImageView.heightProperty());
	 	    
	 	    // Set tree modification related event handlers (branchExpandedEvent)

	        /*rootNode.addEventHandler(TreeItem.branchExpandedEvent(),new  EventHandler()
	        {
				@Override
				public void handle(Event e) {
					// TODO Auto-generated method stub
					
					TreeItem<String> item = (TreeItem<String>)e.getSource();
					String path = item.getValue().toString();
					System.out.println(path);
					File fileIn = new File (path);
	                //FilePathTreeItem source=(FilePathTreeItem)e.getSource();
					FilePathTreeItem source;
					if (item instanceof FilePathTreeItem) {
						source = (FilePathTreeItem) item;
					}else {
						source = null;
					}
					
	                if(source.isDirectory()&&source.isExpanded()){
	                    ImageView iv=(ImageView)source.getGraphic();
	                    iv.setImage(folderExpandImage);
	                    System.out.println("Hola");
	                }
	                try{
	                    if(source.getChildren().isEmpty()){
	                    	System.out.println("Hola2");
	                        Path path=Paths.get(source.getFullPath());
	                        BasicFileAttributes attribs=Files.readAttributes(path,BasicFileAttributes.class);
	                        if(attribs.isDirectory()){
	                        	System.out.println("Hola3");
	                            DirectoryStream<Path> dir=Files.newDirectoryStream(path);
	                            for(Path file:dir){
	                            	System.out.println(file.toString());
	                                FilePathTreeItem treeNode=new FilePathTreeItem(file);
	                                source.getChildren().add(treeNode);
	                            }
	                        }
	                    }else{
	                        //if you want to implement rescanning a directory for changes this would be the place to do it
	                    }
	                }catch(IOException x){
	                    x.printStackTrace();
	                }
				}
	        });

	        // Set tree modification related event handlers (branchCollapsedEvent)
	        rootNode.addEventHandler(TreeItem.branchCollapsedEvent(),new EventHandler()
	        {
				@Override
				public void handle(Event e) {
					// TODO Auto-generated method stub
					TreeItem<String> item = (TreeItem<String>)e.getSource();
					String path = item.getValue().toString();
					System.out.println(path);
					File file = new File (path);
					//file.toPath();
					//FilePathTreeItem source=(FilePathTreeItem)e.getSource();
	                if(file.isDirectory()&&!item.isExpanded()){
	                    ImageView iv=(ImageView)item.getGraphic();
	                    iv.setImage(folderCollapseImage);
	                }
				}
	        });*/

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
