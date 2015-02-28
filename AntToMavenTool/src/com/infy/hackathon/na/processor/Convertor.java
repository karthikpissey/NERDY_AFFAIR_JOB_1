/**
 * 
 */
package com.infy.hackathon.na.processor;

import java.io.File;
import java.io.FileFilter;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.infy.hackathon.na.constants.AntDirectory;
import com.infy.hackathon.na.constants.MavenDirectory;

/**
 * @author test
 *
 */
public class Convertor {

	public static void main(String[] args) throws Exception {
		
		ResourceUtil resourceUtil=new ResourceUtil();
		
		if(resourceUtil.createProjectResource()){
			Convertor convertor = new Convertor();

			convertor.copyFilesToMaven();
		}

		

	}

	public void copyFilesToMaven() throws Exception {
		

		//Copying the src files
		copyFiles(AntDirectory.SRC, MavenDirectory.SRC_MAIN_JAVA);
		
		//Copying the webcontent files
		//copyFiles(AntDirectory.WEBAPP,MavenDirectory.SRC_MAIN_WEBAPP);
		
		//Copying the Test Files
		//copyFiles(AntDirectory.TESTSRC,MavenDirectory.SRC_TEST_JAVA);
		

	}

	// Copies the src files from
	public void copySrcFiles(ResourceBundle antBundle,
			ResourceBundle mavenBundle) throws Exception {

		String antSrcPath = antBundle.getString(AntDirectory.SRC.toString());
		String mavenJavaPath = mavenBundle.getString("APP_NAME_SPACE")
				+ mavenBundle.getString("SRC_MAIN_JAVA");

		System.out.println("Ant src path " + antSrcPath);
		System.out.println("Maven src path " + mavenJavaPath);

		File antSrcFile = new File(antSrcPath);
		File mavenSrcFile = new File(mavenJavaPath);

		FileUtils.copyDirectory(antSrcFile, mavenSrcFile);
		
		

	}
	
	public void copyFiles(AntDirectory ant,MavenDirectory maven) throws Exception{
		
		ResourceBundle antBundle = ResourceBundle.getBundle("AntResources");
		ResourceBundle mavenBundle = ResourceBundle.getBundle("MavenResources");
		
		String antPath = null;
		try{		
		antPath = antBundle.getString(ant.toString());	
		System.out.println("Ant ");
		}catch(Exception e){
			System.out.println("Ant path not defined for "+ant.toString());
		}
		
		String mavenPath =null;
		
		try{		
			mavenPath =mavenBundle.getString("APP_NAME_SPACE")+ mavenBundle.getString(maven.toString());		
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		
		if(antPath!=null && mavenPath!=null){
			
			File antFile = new File(antPath);
			File mavenFile = new File(mavenPath);
			
			//antFile.get
			
			//FileUtils.copyDirectory(antFile, mavenFile);
			
			IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".java");
			 IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
			  // Create a filter for either directories or ".txt" files
			  FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
		
			  // Copy using the filter
			 FileUtils.copyDirectory(antFile, mavenFile, filter);
			
			System.out.println("Copied the files from "+antPath+" to "+ mavenPath);
			
			
			
		}else{
			System.out.println("One of the maven or ant path is missing");
		}
		
		
	}

}
