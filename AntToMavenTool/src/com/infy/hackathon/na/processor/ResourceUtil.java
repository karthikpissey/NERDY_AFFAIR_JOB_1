package com.infy.hackathon.na.processor;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import com.infy.hackathon.na.constants.MavenDirectory;
import com.infy.hackathon.na.constants.MavenFile;

/**
 * 
 * @author Nerdy_Affair
 * 
 */
public class ResourceUtil {
	
	public boolean createProjectResource() throws IOException{
		System.out.println("Creating the project folder...");
		ResourceBundle bundle = ResourceBundle.getBundle("MavenResources");
		if(createProjectBase(bundle)){
			System.out.println("Project base folder has been created successfully!!!");
		}else{
			System.out.println("Failed to create the project folder.");
			return false;
		}
		System.out.println("Creating the Sub folders...");
		for(MavenDirectory directory : MavenDirectory.values()){
			System.out.println("directory: " + directory);
			createDirectory(directory,bundle);
		}
		System.out.println("Creating Maven related project files...");
		for(MavenFile file : MavenFile.values()){
			createFile(file,bundle);
		}
		System.out.println("Maven project has been created successfully!!!");
		return true;
	}
	
	public boolean createDirectory(MavenDirectory directory,ResourceBundle bundle){	
		System.out.println("Creating the directory: " + directory.name());
		String resourcePath = bundle.getString(directory.name());
		File resourceDir = new File(bundle.getString("APP_NAME_SPACE")+resourcePath);
		if(resourceDir.exists()){
			deleteDirectory(resourceDir);
		}			
		boolean isDirCreated =  resourceDir.mkdirs(); 
		return isDirCreated;
	}
	
	public boolean createFile(MavenFile file, ResourceBundle bundle) throws IOException{
		String resourcePath = bundle.getString(file.name());
		return new File(bundle.getString("APP_NAME_SPACE")+resourcePath).createNewFile(); 		
	}
	
	public boolean createProjectBase(ResourceBundle bundle){		
		String appNameSpace = bundle.getString("APP_NAME_SPACE");
		File appFolder = new File(appNameSpace);
		if(appFolder.exists()){
			deleteDirectory(appFolder);
		}			
		return appFolder.mkdir(); 		
	}
	
	public static void main(String [] args){
		ResourceUtil resourceUtil = new ResourceUtil();
		try {
			resourceUtil.createProjectResource();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
	
}
