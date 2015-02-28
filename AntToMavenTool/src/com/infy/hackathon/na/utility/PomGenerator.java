package com.infy.hackathon.na.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infy.hackathon.na.constants.MavenArtifactBean;

public class PomGenerator {
	private static final String MAVEN_KEY_SEARCH_URL="http://search.maven.org/solrsearch/select?q=";
	private static final String KEY_SEARCH_PARAMS="&rows=20&wt=json";
	private static final String MAVEN_CHECKSUM_URL="http://search.maven.org/solrsearch/select?q=1:%22";
	private static final String CHECKSUM_QUERY_PARAMS="%22&rows=20&wt=json";
	private static final String CHECKSUM_SEARCH="CHECKSUM";
	private static final String JARNAME_SEARCH="JARNAME";
	
	public static void main(String[] args) {
		
	
			String libDir="C:\\Users\\kalluri\\git\\hackathon\\NERDY_AFFAIR_JOB_1\\SampleWebAppAnt\\WebContent\\WEB-INF\\lib"; 
			String targetMavenDir="C:\\SampleMavenApp"; 
			try{
			new PomGenerator().pomFileGenerator(libDir, targetMavenDir);
			}catch(Exception e){
				
			}
			
			/*System.out.println("Begining to unzip the uploaded file....");
			//FileUtility.extractUploadedZipProject(bundle.getString(AntToMavenConstants.UPLOADED_ZIP_DIR), bundle.getString(AntToMavenConstants.UNZIPPED_PROJECT_DIR));
			*/
			
		
		
		
	}
		
	public String pomFileGenerator(String libDir, String targetMavenDir){
		
			List<MavenArtifactBean> pomDependencies = PomGenerator
					.getPomDependencies(libDir,targetMavenDir);
			String pomFilePath = targetMavenDir+"\\pom.xml";
			File file = new File(pomFilePath);
			String result="failure";
			try{
				if(!file.exists())
				 file.createNewFile();
				else{
				 file.delete();
				 file.createNewFile();
				}
				Writer w = new FileWriter(file);
				Model model = new Model();
				model.setGroupId( "CUSTOM-APPNAME" );
				model.setArtifactId("CUSTOM-APPNAME");
				model.setVersion("0.0.1-SNAPSHOT");
				model.setPackaging("war");
				model.setModelVersion("4.0.0");
				List<Dependency> dependencyList = new ArrayList<Dependency>();
				for(MavenArtifactBean bean: pomDependencies){
					Dependency dependency = new Dependency();
					dependency.setArtifactId(bean.getA());
					dependency.setGroupId(bean.getG());
					dependency.setVersion(bean.getLatestVersion());
					if(bean.getScope()!=null && bean.getScope().equals("system")){
						dependency.setScope(bean.getScope());
						dependency.setSystemPath(bean.getSystemPath());
					}
					dependencyList.add(dependency);
				}
				
				model.setDependencies(dependencyList);
				new MavenXpp3Writer().write(w, model);
				result = "success";
			}catch(Exception e){
				e.printStackTrace();
			}
			return result;
		
	}
	
	
	private static MavenArtifactBean getArtifactId(String jarFilePath, String targetMavenDir) throws NoSuchAlgorithmException, IOException{
		
		String jarName = jarFilePath.substring(jarFilePath.lastIndexOf("\\")+1,jarFilePath.indexOf("jar")-1);
		
		String version = Character.isDigit(jarName.substring(jarName.lastIndexOf("-")+1).charAt(0))?jarName.substring(jarName.lastIndexOf("-")+1):"";
		String splitNames[]=jarName.split("-");
		jarName =  Character.isDigit(splitNames[splitNames.length-1].charAt(0))? jarName.substring(0, jarName.lastIndexOf("-")):jarName;
		String checkSum = ChecksumUtility.getCheckSum(jarFilePath);
		System.out.println("JarName::"+jarName);
		JsonObject obj;
		obj = PomGenerator.invokeMavenRepoService(CHECKSUM_SEARCH, checkSum);
		List<MavenArtifactBean> mavenArtifacts = new ArrayList<MavenArtifactBean>();
		mavenArtifacts.addAll(PomGenerator.getAllMavenArtifactBeans(obj, jarName));
		MavenArtifactBean returnMavenArtifactBean = null;
		if (mavenArtifacts != null) {
			Collections.sort(mavenArtifacts,
					new MavenArtifactBean().new SortBasedOnRank());
			returnMavenArtifactBean = (mavenArtifacts == null || mavenArtifacts
					.size() == 0) ? null : mavenArtifacts.get(0);
		}
		if (mavenArtifacts == null || mavenArtifacts.size() == 0) {
			obj = PomGenerator.invokeMavenRepoService(JARNAME_SEARCH, jarName);
			mavenArtifacts.addAll(getAllMavenArtifactBeans(obj, jarName));
			Collections.sort(mavenArtifacts,
					new MavenArtifactBean().new SortBasedOnRank());
			returnMavenArtifactBean = (mavenArtifacts == null || mavenArtifacts
					.size() == 0) ? null : mavenArtifacts.get(0);
			if (returnMavenArtifactBean != null)
				returnMavenArtifactBean.setLatestVersion(version);

		}
		if (mavenArtifacts == null || mavenArtifacts.size() == 0) {
			String targetFolder = targetMavenDir+"\\"+"lib";
			File file = new File(targetFolder);
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
			returnMavenArtifactBean.setA(jarName==null?"<ChangeIt>":jarName);
			returnMavenArtifactBean.setG(jarName==null?"<ChangeIt>":jarName);
			returnMavenArtifactBean.setLatestVersion((version==null||version.length()==0)?"1.0":version);
			returnMavenArtifactBean.setScope("system");
			String targetDir = targetFolder+"\\"+jarFilePath.substring(jarFilePath.lastIndexOf("\\")+ 1) ;
			targetDir = "${basedir}\\"+targetDir.substring(targetDir.lastIndexOf("lib"));
			targetDir = targetDir.replace("\\", "/");
			returnMavenArtifactBean.setSystemPath(targetDir);
			returnMavenArtifactBean.setId(jarName==null?"<ChangeIt>":jarName);
		}
		return returnMavenArtifactBean;
		
	}
	
	private static List<MavenArtifactBean> getAllMavenArtifactBeans(JsonObject obj,String searchkey){
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
	
	private static JsonObject invokeMavenRepoService(String searchType, String searchPhrase){
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
	
	
	private static List<MavenArtifactBean>  getPomDependencies(String libDir, String targetMavenDir){
		System.out.println("Started bui");
		File jarHeadDir = new File(libDir);
		File[] jarDir = jarHeadDir.listFiles();
		List<MavenArtifactBean> dependencyBeans = null; 
		if (jarDir != null) {
			dependencyBeans = new ArrayList<MavenArtifactBean>();
			for (File jarFile : jarDir) {
				String jarFilePath = jarFile.toString();
				/*
				 * String jarName =
				 * jarFilePath.substring(jarFilePath.lastIndexOf
				 * ("\\")+1,jarFilePath.indexOf("jar")-1); String version =
				 * jarName.substring(jarName.lastIndexOf("-")+1); jarName =
				 * jarName.substring(0, jarName.lastIndexOf("-"));
				 */
				if (jarFilePath.endsWith(".jar")) {
					try {
						MavenArtifactBean artifactId = PomGenerator
								.getArtifactId(jarFilePath,targetMavenDir);
						if (artifactId != null)
							dependencyBeans.add(artifactId);
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					continue;
			}
		}
		return dependencyBeans;
		
	}

}
