package gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class GUI_FX extends Application {
	
	private static Stage primaryStage; 
	private AnchorPane mainLayout;
	//private TreeView <String> treeView;

	private TreeView <String> treeView = new TreeView <String>();
	

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.setPrimaryStage(primaryStage);
		this.getPrimaryStage().setTitle("Metadata Manager");
		this.getPrimaryStage().setMaximized(true);
		showMainView();
		//showFileBrowser();
		

	}
	
	private void showMainView() throws IOException {
		/*FXMLLoader loader = new FXMLLoader();
		loader.setLocation(GUI_FX.class.getResource("view/MainView.fxml"));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout); */
		
		Parent root = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
		Scene scene = new Scene(root); 
		
		
		getPrimaryStage().setScene(scene);
		getPrimaryStage().show();
		//this.treeView = (TreeView<String>) scene.lookup("#treeView");
	}
	
	private void showFileBrowser() throws FileNotFoundException {
		
		
		
		
		//create tree pane
       /* VBox treeBox=new VBox();
        treeBox.setPadding(new Insets(10,10,10,10));
        treeBox.setSpacing(10);*/
        //setup the file browser root
		String hostName="computer";
        try{hostName=InetAddress.getLocalHost().getHostName();}catch(UnknownHostException x){}
        
        //InputStream is = GUI_FX.class.getResourceAsStream("view/computer.png");
        InputStream is = new BufferedInputStream(new FileInputStream("icons/computer.png"));
        Image image1 = new Image(is);
        ImageView imageView = new ImageView(image1);
        TreeItem<String> rootNode=new TreeItem<>(hostName,imageView);
        //this.treeView.setRoot(rootNode);
        Iterable<Path> rootDirectories=FileSystems.getDefault().getRootDirectories();        
        
        for(Path name:rootDirectories){
        	System.out.println(name.toString());
        	FilePathTreeItem treeNode=new FilePathTreeItem(name);
        	rootNode.getChildren().add(treeNode);
        	//tree(name, rootNode);
        }
        
        
        rootNode.setExpanded(true);
        //create the tree view
        //this.treeView.setRoot(rootNode);
        
        
        //add everything to the tree pane
        /*treeBox.getChildren().addAll(new Label("File browser"),treeView);
        VBox.setVgrow(treeView,Priority.ALWAYS);
       
        //setup and show the window
        primaryStage.setTitle("JavaFX File Browse Demo");
        StackPane root=new StackPane();
        root.getChildren().addAll(treeBox);
        primaryStage.setScene(new Scene(root,400,300));
        primaryStage.show();*/
	}
	
	/*public void mouseClick (MouseEvent mouseEvent) {
		//TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
		//System.out.println(item.getValue());
	}*/
	
	/*
	public void tree(Path path, TreeItem<String> rootNode) throws FileNotFoundException {
		
		File root = new File( path.toString() );
        File[] list = root.listFiles();
        
        //System.out.println(list[0].getPath());

        if (list == null) return;
        

        for ( File f : list ) {
        	Path name2 = f.toPath();
        	FilePathTreeItem treeNode=new FilePathTreeItem(name2);
        	rootNode.getChildren().add(treeNode);
        	
            if ( f.isDirectory() ) {
            	tree( name2, rootNode );
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
                //System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
		
	}
	
	public void walk( String path, TreeItem<String> rootNode ) throws FileNotFoundException {

        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;
        
        Path name = root.toPath();
        FilePathTreeItem treeNode=new FilePathTreeItem(name);
    	rootNode.getChildren().add(treeNode);

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath(), rootNode );
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
                //System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
    }*/

	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
