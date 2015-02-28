package com.infy.hackathon.na.utility;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.infy.hackathon.na.constants.AntToMavenConstants;

public class ConvertionEngine {
	
	public static void main(String[] args) {
		
		
		
		
		
	}
	
	public void getPomDependencies(){
		File jarHeadDir = new File(AntToMavenConstants.PROCESSING_PROJECT_DIR);
		File[] projectDir = jarHeadDir.listFiles();
		File jarDir=null;
		if(projectDir[0].isDirectory()){
			jarDir = new File(projectDir[0].getPath()+AntToMavenConstants.LIB_DIR);
		}
		if(jarDir.isFile() || FileUtils.sizeOfDirectory(jarDir)==0){
			//To do :: return empty dependecny object
		}
		
	}

}
