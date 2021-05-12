package weatherAPI;
import org.junit.Before;
import org.testng.annotations.Test;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.*;
import io.restassured.config.RedirectConfig;
import static org.hamcrest.CoreMatchers.*;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.module.mockmvc.RestAssuredMockMvc.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class rhrread {
	
	RedirectConfig config = new RedirectConfig();
	Response response;
	String resultFileName;
	
	@Given("I go to {string} with {string}")
	public void requestApi(String api, String parameterSet) throws Exception {
		//get url
		String url = getUrl(api);
		//get parameters
		Map<String, String> parameters = getPara(parameterSet);
		//request
		response = RestAssured.given()
				.params(parameters).
		when().
		    get(url);
	}
	
	@Given("I go to {string} with {string} without redirect")
	public void requestApiWithoutRedirect(String api, String parameterSet) throws Exception {
		//get url
		String url = getUrl(api);
		//get parameters
		Map<String, String> parameters = getPara(parameterSet);
		//request
		response = RestAssured.given()
				.params(parameters)
				.config(RestAssured.config().redirect(config.followRedirects(false))).
		when().
		    get(url);
	}
	
	@Given("I go to {string} with {string} with redirect")
	public void requestApiWithRedirect(String api, String parameterSet) throws Exception {
		//get url
		String url = getUrl(api);
		//get parameters
		Map<String, String> parameters = getPara(parameterSet);
		//request
		response = RestAssured.given()
				.params(parameters)
				.config(RestAssured.config().redirect(config.followRedirects(true))).
		when().
		    get(url);
	}
	
	@And("I go to {string} with {string} with long url")
	public void requestApiLongUrl(String api, String parameterSet) throws Exception {
		//get url
		String url = getUrl(api);
		//get parameters
		Map<String, String> parameters = getPara(parameterSet);
		//produce long url
		while(url.length()<10000) {
			url = url+url;
		}
		response = RestAssured.given()
				.params(parameters).
		when().
		    get(url);
	} 
	
	@And("I check status code is {int}")
	public void checkStatusCode(int statusCode) {
		assertEquals(response.statusCode(), statusCode);
	}
	
	@And("I check json file is in the correct format")
	public void formatChecker() {
		String json = response.getBody().asPrettyString();
		assertThat(json, matchesJsonSchemaInClasspath("rhrread-schema.json"));
	}
	
	@And("I check status code is {int} or {int}")
	public void acceptCodes(int statCode1, int statCode2){
		response.andReturn().then().assertThat().statusCode(anyOf(is(301),is(307)));
	}
	
	@And("I measure response time within {int} milliseconds")
	public void responseTime(int time) throws Exception {
		if(response.getTime() > time)
			throw new Exception("Response time is too long: " + response.getTime() + " milliseconds");
	}
	
	@And("I check {string} is shown")
	public void checkPageContent(String content) throws Exception {
		String reponseHtml = response.getBody().asPrettyString();
		switch(content) {
		case "Moved Permanently/Temporary Redirect":
			if(reponseHtml.indexOf("Moved Permanently") + reponseHtml.indexOf("Temporary Redirect") 
					< 0)
				throw new Exception(content + "is not found on the page");
			break;
		case "Please include valid parameters in API request":
			if(reponseHtml.indexOf(content)<0)
				throw new Exception(content + "is not found on the page");
			break;
		case "Request-URI Too Long":
			if(reponseHtml.indexOf(content)<0)
				throw new Exception(content + "is not found on the page");
			break;
		}
	}
	
	@And("I check response type is {string}")
	public void responseType(String type) throws Exception{
		switch(type) {
		case "json":
			assertEquals(response.contentType(),"application/json; charset=utf-8");
			break;
		case "html":
			if(!response.contentType().startsWith("text/html"))
				throw new Exception("I check response type is " + type +" failed.");
			break;
		}
	}
	
	@Given("I carry out performance test")
	public void performanceTest() throws Exception {
		//Jmeter file location
		String JmeterBinLocation = System.getProperty("user.dir") + "\\src\\test\\resources\\apache-jmeter-5.4.1\\bin\\";
		//output file location
		File theDir = new File(JmeterBinLocation + "\\output\\" + new SimpleDateFormat("yyyyMMdd").format(new Date()));
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		//output file name
	    resultFileName = theDir.getPath() +"\\" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) +"_weathersult.jtl";
		//run Jmeter
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(JmeterBinLocation + "jmeter.bat -n -t " 
		+ JmeterBinLocation + "weather.jmx -l " 
				+ resultFileName);
	}
	
	@Then("I verify result file is generated")
	public void verifyJmeterResultCreated() throws Exception {
		File result = new File(resultFileName);
		if (!result.exists() && result.isDirectory()){
			throw new Exception("Result file is not generated");
		}
	}
	
	public Map<String, String> getPara(String parameterSet) throws IOException {
		File file = new File(Const.dataFile);   //creating a new file instance  
		FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file  
		//creating Workbook instance that refers to .xlsx file  
		XSSFWorkbook wb = new XSSFWorkbook(fis);   
		XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object  
		DataFormatter df = new DataFormatter();
		
		//paramMap is each parameter sets
	    Map<String, String> paramMap = new LinkedHashMap<>();
	    //allParamMap contains all the parameters set and key is the parameter set name
	    Map<String,Map<String,String>> allParamMap = new LinkedHashMap<>();
	    Row row = sheet.getRow(0);

	    ArrayList<String> headersName = new ArrayList<String>();

	    //get parameter name i.e. dataType & lang
	    for (int j = 0; j <= row.getPhysicalNumberOfCells(); j++) {
	        row.getCell(j);
	        if ((df.formatCellValue(row.getCell(j)).isEmpty())) {
	            continue;
	        } else {
	            headersName.add(df.formatCellValue(row.getCell(j)));
	        }
	    }
	    //loop to get parameter sets
	    OUTER: for (Row myrow : sheet) {
	    	String paramName="";
	        for (int i = 0; i < myrow.getLastCellNum(); i++) {
	        	//skip first row, only contain column name
	            if (myrow.getRowNum() == 0) {
	                continue OUTER;
	            }  
	            String value = df.formatCellValue(myrow.getCell(i));
	            //continue loop when the data = paramName
	            if (headersName.get(i).equalsIgnoreCase("paramName")) {
	            	paramName = value;
	                continue;
	            }  
	            paramMap.put(headersName.get(i), value);
	        }
	        allParamMap.put(paramName, new LinkedHashMap<>(paramMap));
	    }
	    //return parameters
	    return allParamMap.get(parameterSet);
	}
	
	public String getUrl(String api) {
		String url="";
		//check api type, if end with http then url start with http://
		if(api.endsWith("http")) {
			url = "http://";
			api = api.replace("_http", "");
		}
		else {
			url = "https://";
		}
		//get url
		switch(api) {
		case "rhrread":
			url = url + Const.rhrreadUrl;
			break;
		}
		return url;
	}
}
