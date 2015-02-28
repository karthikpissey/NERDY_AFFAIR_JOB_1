package com.infy.hackathon.na.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

public class FileUtility {
	Properties props = new Properties();
	
	
	public static void main(String[] args) {
		try {
			extractUploadedZipProject("C:\\antToMaven_Space\\uploaded_Content", "C:\\antToMaven_Space\\unZippedProjects\\");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearDirectory(String path) {
		File file = new File(path);
		try {
			FileUtils.forceDeleteOnExit(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void extractUploadedZipProject(String sourceDir, String targetDir) throws IOException{
		File file = new File(targetDir);
		if(!file.exists()){
			FileUtils.forceMkdir(file);
		}else
			FileUtils.cleanDirectory(file);
		
		File uploadedContent = new File(sourceDir);
		String fileName;
		if(uploadedContent.isDirectory()){
			File uploadedFile []= uploadedContent.listFiles();
			File uploadedZipFile = uploadedFile[0];
			fileName = uploadedZipFile.getPath();
			
		}else{
			fileName = uploadedContent.getPath();
		}
		FileUtility.extractZipFiles(fileName, targetDir);
		
		
	}
	
	public static void extractZipFiles(String sourceDir, String targetDir) throws IOException{
		byte[] buff = new byte[1024];
		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(sourceDir));
		ZipEntry entry = inputStream.getNextEntry();
		
		while(entry !=null){
			String entryName = entry.getName();
			int n;
			FileOutputStream outPutStream;
			File newFile = new File(entryName);
			String dir = newFile.getParent();
			if(dir==null){
				if(newFile.isDirectory())
					break;
			}else{
				new File(targetDir+dir).mkdirs();
			}
			
			if(!entry.isDirectory()){
				outPutStream = new FileOutputStream(targetDir+entryName);
				while((n=inputStream.read(buff, 0, 1024))>-1){
					outPutStream.write(buff, 0, n);
				}
				outPutStream.close();
				
			}
			entry.clone();
			entry = inputStream.getNextEntry();
		}
		inputStream.close();
		
	}

}
