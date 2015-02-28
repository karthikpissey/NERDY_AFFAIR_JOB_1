/**
 * 
 */
package com.infy.hackathon.na.processor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.infy.hackathon.na.constants.AntDirectory;
import com.infy.hackathon.na.constants.MavenDirectory;
import com.infy.hackathon.na.utility.PomGenerator;

/**
 * @author test
 *
 */
public class Convertor {

	/*public static void main(String[] args) throws Exception {
		
		ResourceUtil resourceUtil=new ResourceUtil();
		
		if(resourceUtil.createProjectResource()){
			Convertor convertor = new Convertor();

			convertor.copyFilesToMaven();
		}		

	}*/
	
	public String convertAntToMaven(Map<String, String> antResources){
		
		ResourceUtil resourceUtil=new ResourceUtil();
		String mavenProjectFolder = "";
		try {
			if(resourceUtil.createProjectResource()){
				Convertor convertor = new Convertor();

				mavenProjectFolder = convertor.copyFilesToMaven(antResources);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return mavenProjectFolder;
	}

	public String copyFilesToMaven(Map<String, String> antResources) throws Exception {
		
		String antProjectFolder = antResources.get(AntDirectory.PROJECT_FOLDER.toString());
		ResourceBundle mavenResources = ResourceBundle.getBundle("MavenResources");
		String mavenProjectFolder = mavenResources.getString("APP_NAME_SPACE");
		//Copying the src files
		String antSrcDir = antProjectFolder + "//" + antResources.get(AntDirectory.SRC.toString());
		String mavenSrcDir = mavenProjectFolder+ mavenResources.getString(MavenDirectory.SRC_MAIN_JAVA.toString());
		copyFiles(antSrcDir, mavenSrcDir, ".java");
		
		// Copying the properties referred in Source
		String mavenSrcResDir = mavenProjectFolder+ mavenResources.getString(MavenDirectory.SRC_MAIN_RESOURCES.toString());
		copyFiles(antSrcDir, mavenSrcResDir, ".properties");
		
		//Copying the Test Cases
		String antTestDir = antProjectFolder + "//" + antResources.get(AntDirectory.TESTSRC.toString());
		String mavenTestDir = mavenProjectFolder+ mavenResources.getString(MavenDirectory.SRC_TEST_JAVA.toString());
		copyFiles(antTestDir, mavenTestDir, ".java");
		
		// Copying the properties referred in Test Cases
		String mavenTestResDir = mavenProjectFolder+ mavenResources.getString(MavenDirectory.SRC_TEST_RESOURCES.toString());
		copyFiles(antTestDir, mavenTestResDir, ".properties");
		
		String antLibDir = antProjectFolder + "//" + antResources.get(AntDirectory.LIB.toString());
		new PomGenerator().pomFileGenerator(antLibDir, mavenProjectFolder);
		
		return mavenProjectFolder;
	}

public void copyFiles(String antDir,String mavenDir, String fileExtension) throws Exception{		
			
			File antFile = new File(antDir);
			File mavenFile = new File(mavenDir);
					
			IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(fileExtension);
			 IOFileFilter txtFiles = FileFilterUtils.andFileFilter(FileFileFilter.FILE, txtSuffixFilter);
			  // Create a filter for either directories or ".txt" files
			  FileFilter filter = FileFilterUtils.orFileFilter(DirectoryFileFilter.DIRECTORY, txtFiles);
		
			  // Copy using the filter
			 FileUtils.copyDirectory(antFile, mavenFile, filter);
		
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
