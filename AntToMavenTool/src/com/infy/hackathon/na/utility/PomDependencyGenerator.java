package com.infy.hackathon.na.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infy.hackathon.na.constants.AntToMavenConstants;
import com.infy.hackathon.na.constants.MavenArtifactBean;

public class PomDependencyGenerator {
	private static final String MAVEN_KEY_SEARCH_URL="http://search.maven.org/solrsearch/select?q=";
	private static final String KEY_SEARCH_PARAMS="&rows=20&wt=json";
	private static final String MAVEN_CHECKSUM_URL="http://search.maven.org/solrsearch/select?q=1:%22";
	private static final String CHECKSUM_QUERY_PARAMS="%22&rows=20&wt=json";
	private static final String CHECKSUM_SEARCH="CHECKSUM";
	private static final String JARNAME_SEARCH="JARNAME";
	public static void main(String[] args) {
		
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("MavenResources");
			System.out.println("Begining to unzip the uploaded file....");
			FileUtility.extractUploadedZipProject(bundle.getString(AntToMavenConstants.UPLOADED_ZIP_DIR), bundle.getString(AntToMavenConstants.UNZIPPED_PROJECT_DIR));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static void getArtifactId(String searchkey, String jarFilePath) throws NoSuchAlgorithmException, IOException{
		String jarName = jarFilePath.substring(jarFilePath.lastIndexOf("\\")+1,jarFilePath.indexOf("jar")-1);
		String version = jarName.substring(jarName.lastIndexOf("-")+1);
		String checkSum = ChecksumUtility.getCheckSum(jarFilePath);
		JsonObject obj;
		obj = PomDependencyGenerator.invokeMavenRepoService(CHECKSUM_SEARCH, checkSum);
		//
		List<MavenArtifactBean> mavenArtifacts = new ArrayList<MavenArtifactBean>();
		mavenArtifacts.addAll(PomDependencyGenerator.getAllMavenArtifactBeans(obj, searchkey));
		MavenArtifactBean returnMavenArtifactBean = null;
		if (mavenArtifacts != null) {
			Collections.sort(mavenArtifacts,
					new MavenArtifactBean().new SortBasedOnRank());
			returnMavenArtifactBean = (mavenArtifacts == null || mavenArtifacts
					.size() == 0) ? null : mavenArtifacts.get(0);
		}
		if (mavenArtifacts == null || mavenArtifacts.size() == 0) {
			obj = PomDependencyGenerator.invokeMavenRepoService(JARNAME_SEARCH, searchkey);
			mavenArtifacts.addAll(getAllMavenArtifactBeans(obj, searchkey));
			Collections.sort(mavenArtifacts,
					new MavenArtifactBean().new SortBasedOnRank());
			returnMavenArtifactBean = (mavenArtifacts == null || mavenArtifacts
					.size() == 0) ? null : mavenArtifacts.get(0);
			if (returnMavenArtifactBean != null)
				returnMavenArtifactBean.setLatestVersion(version);

		}
		if (mavenArtifacts == null || mavenArtifacts.size() == 0) {
			String maventempDir = AntToMavenConstants.JAR_PROCESSING_DIR + "lib";
			File file = new File(maventempDir);
			if (!file.exists()) {
				FileUtils.forceMkdir(file);
			}
			FileUtils.copyFileToDirectory(new File(jarFilePath), file);
			/*
			 * <dependency> <groupId>getContractBPInfo</groupId>
			 * <artifactId>getContractBPInfo.jar</artifactId>
			 * <version>1.0</version> <scope>system</scope>
			 * <systemPath>${project.libdir}/getContractBPInfo.jar</systemPath>
			 * </dependency>
			 */
			
			returnMavenArtifactBean = new MavenArtifactBean();
			returnMavenArtifactBean.setA(jarName);
			returnMavenArtifactBean.setG(jarName);
			returnMavenArtifactBean.setLatestVersion(version);
			returnMavenArtifactBean.setScope("system");
			returnMavenArtifactBean.setSystemPath(maventempDir
					+ jarFilePath.substring(jarFilePath.lastIndexOf("\\") + 1));
		}
		
	}
	
	public static List<MavenArtifactBean> getAllMavenArtifactBeans(JsonObject obj,String searchkey){
		JsonObject responseObj = obj.getAsJsonObject("response");
		JsonArray mavenRepoSearchResults = responseObj.getAsJsonArray("docs");
		List<MavenArtifactBean> mavenArtifacts = new ArrayList<MavenArtifactBean>();
		for(int i=0;i<mavenRepoSearchResults.size();i++){
			JsonObject mavenRepoFound = mavenRepoSearchResults.get(i).getAsJsonObject();
			
			MavenArtifactBean bean = new MavenArtifactBean();
			bean.setA(mavenRepoFound.get("a").getAsString());
			bean.setG(mavenRepoFound.get("g").getAsString());
			bean.setId(mavenRepoFound.get("id").getAsString());
			if(mavenRepoFound.has("latestVersion"))
			bean.setLatestVersion(mavenRepoFound.get("latestVersion").getAsString());
			if(mavenRepoFound.has("v"))
				bean.setLatestVersion(mavenRepoFound.get("v").getAsString());
				
			bean.setSearchHit(LevenshteinDistance.computeLevenshteinDistance(searchkey, bean.getA()));
			mavenArtifacts.add(bean);
			
		}
		return mavenArtifacts;
	}
	
	public static JsonObject invokeMavenRepoService(String searchType, String searchPhrase){
		searchPhrase=searchPhrase.replace(" ", "%20");
		String mavenRestURL = "";
		JsonObject returnObject = null;
		if(searchType.equals(JARNAME_SEARCH)){
			mavenRestURL=MAVEN_KEY_SEARCH_URL+searchPhrase+KEY_SEARCH_PARAMS;
		}else{
			mavenRestURL=MAVEN_CHECKSUM_URL+searchPhrase+CHECKSUM_QUERY_PARAMS;
		}
		System.out.println("Maven repo URL being invoked:: "+mavenRestURL);
		try{
			URL url = new URL(mavenRestURL);
			HttpURLConnection mavenConnection = (HttpURLConnection) url.openConnection();
			mavenConnection.setRequestMethod("GET");
			mavenConnection.setRequestProperty("Accept", "application/json");
			if(mavenConnection.getResponseCode()!=200){
				throw new RuntimeException("Failed to invoke Maven API:: with response code- "
						+mavenConnection.getResponseCode()+":: "+mavenConnection.getResponseMessage());
				
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(mavenConnection.getInputStream()));
			String output;
			while ((output = reader.readLine()) != null) {
				JsonParser parser = new JsonParser();
				returnObject=(JsonObject)parser.parse(output);
			}
			reader.close();
			
		}catch(Exception e){
			String errMsg = "{error:'"+e.getMessage()+"'}";
			e.printStackTrace();
			return new JsonParser().parse(errMsg).getAsJsonObject();
		}
		return returnObject;
		
	}
	
	
	public void getPomDependencies(){
		System.out.println("Started bui");
		ResourceBundle bundle = ResourceBundle.getBundle("MavenResources");
		File jarHeadDir = new File(bundle.getString(AntToMavenConstants.LIB_DIR));
		File[] jarDir = jarHeadDir.listFiles();

		for(File jarFile : jarDir){
			String jarFilePath = jarFile.toString();
			String jarName = jarFilePath.substring(jarFilePath.lastIndexOf("\\")+1,jarFilePath.indexOf("jar")-1);
			String version = jarName.substring(jarName.lastIndexOf("-")+1);
			jarName = jarName.substring(0, jarName.lastIndexOf("-"));
			if(jarFilePath.endsWith(".jar")){
				
			}else
				continue;
			
		}
		
	}

}
