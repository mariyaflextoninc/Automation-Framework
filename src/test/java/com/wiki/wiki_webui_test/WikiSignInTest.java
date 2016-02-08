package com.wiki.wiki_webui_test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.bcel.generic.LAND;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WikiSignInTest {
	private LoadPropertiesForTest propObject;
	private WebDriver driver = new FirefoxDriver();
	private WebDriverWait wait;
	private String response;
	private Properties prop;
	
	@Test
	public void wikiSignInShouldBeSuccessful() throws InterruptedException{
		propObject = new LoadPropertiesForTest();
		prop = propObject.getPropertiesFromFile();
		
		driver.get(prop.getProperty("url"));
		System.out.println("Successfully opened browser url "+prop.getProperty("url"));
		
		//load url
		String response = driver.getPageSource();
		assertTrue("Validation failed-Text"+prop.getProperty("main_title")+"NOT FOUND",
				response.contains(prop.getProperty("main_title")));
		//Search if the default selected language in drop down is English. 
		defaultLangSelected();
		//search button
     	search();
		// navigation to main page
		navigationToMainPage();
		//login
    	login();
		//logout
	 	logout();
		//add to watch list + delete watchlist
		login();	
		// add page to watchlist
		testWatchList();
	}
	public void defaultLangSelected(){
		
		WebElement selectLangEl = driver.findElement(By.id(prop.getProperty("selectlang_id")));
		Select selectLang = new Select(selectLangEl);
		WebElement option  = selectLang.getFirstSelectedOption();
		String selectedItem = option.getText().trim();
		String language = prop.getProperty("language").trim();
		if(option.getText() != null){
			assertTrue("Validation failed-Text"+language+"NOT FOUND",
					(selectedItem).equals(language));
		}
		
	}
	public void navigationToMainPage(){
		
		WebElement linkEnglish = driver.findElement(By.xpath("//*[@id='www-wikipedia-org']/div[1]/div[2]/a"));
		linkEnglish.click();
		
		String mainPageXpath = "//*[@id='ca-nstab-main']/span/a";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(mainPageXpath)));
		WebElement linkMainPage = driver.findElement(By.xpath(mainPageXpath));
		String mainPageValue = linkMainPage.getText().trim();
		String mainPageText = prop.getProperty("main_page_value").trim();
		assertTrue("Validation failed - Text "+mainPageValue+"NOT FOUND",mainPageValue.equalsIgnoreCase(mainPageText));
			
	}
	public void search(){
		
		WebElement searchText = driver.findElement(By.id("searchInput"));
		searchText.clear();
		searchText.sendKeys(prop.getProperty("home_search_value"));
		WebElement searchButton = driver.findElement(By.xpath("//*[@id='search-container']/form/fieldset/button"));
		searchButton.submit();
		
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstHeading")));
		
		response = driver.getPageSource();
		assertTrue("Validation failed - Text "+prop.getProperty("home_search_value")+"NOT FOUND",response.contains(prop.getProperty("home_search_value")));
		WebElement heading = driver.findElement(By.id("firstHeading"));
		String headingText = heading.getText().trim();
		String searchValue = prop.getProperty("home_search_value").trim();
		
		assertTrue("Validation failed - Text "+searchValue+"NOT FOUND",headingText.equalsIgnoreCase(searchValue));
		System.out.println("heading is "+heading.getText());
		driver.navigate().back();
	}
	public void login(){
		
		String loginXpath = "//*[@id='pt-login']/a";
		WebElement linkLogin = driver.findElement(By.xpath(loginXpath));
		linkLogin.click();
		
		String usernameId = "wpName1";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(usernameId)));
		//username
		WebElement usernameText = driver.findElement(By.id(usernameId));
		usernameText.clear();
		usernameText.sendKeys(prop.getProperty("username"));
		// password
		String passwordId = "wpPassword1";
		WebElement passwordText = driver.findElement(By.id(passwordId));
		passwordText.clear();
		passwordText.sendKeys(prop.getProperty("password"));
		
		String loginButtonId = "wpLoginAttempt";
		WebElement loginButton = driver.findElement(By.id(loginButtonId));
		loginButton.submit();
		System.out.println("Signed in successfully");
		
		//check if signed in
		String userpageXpath = "//*[@id='pt-userpage']/a";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(userpageXpath)));
		WebElement userPageLink = driver.findElement(By.xpath(userpageXpath));	
		String userPageValue = userPageLink.getText().trim();
		String username = prop.getProperty("username").trim();
		assertTrue("Validation failed - Text "+username+" NOT FOUND",userPageValue.equalsIgnoreCase(username));
		System.out.println("Signed in verified");
	}
	public void logout(){
		//signout
		String logoutLinkXpath = "//*[@id='pt-logout']/a";
		WebElement logoutLink = driver.findElement(By.xpath(logoutLinkXpath));
		logoutLink.click();
		
		//check if logged out
		String notloggedInXpath = "//*[@id='pt-anonuserpage']";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(notloggedInXpath)));
		WebElement notloggedInLink = driver.findElement(By.xpath(notloggedInXpath));
		String loggedOutValue = notloggedInLink.getText().trim();
		String loggedOutText = prop.getProperty("logout_message").trim();
		assertTrue("Validation failed - Text "+loggedOutText+" NOT FOUND",loggedOutValue.equalsIgnoreCase(loggedOutText));
		
		//return back to main page
		String returnPageXpath = "//*[@id='mw-returnto']/a";
		WebElement returnPage = driver.findElement(By.xpath(returnPageXpath));
		returnPage.click();
	}
	
	public void testWatchList(){
		
		String wchLstLnkId = "pt-watchlist";
		wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(wchLstLnkId)));
		WebElement wchListLnk = driver.findElement(By.id(wchLstLnkId));
		wchListLnk.click();
		System.out.println("Watch list clicked");

		String pgeCountXpath = "//*[@id='mw-content-text']/p[2]";
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pgeCountXpath)));
		WebElement pageCountValue = driver.findElement(By.xpath(pgeCountXpath)); 
		String pageCountText = pageCountValue.getText();
		int prevPageCount = getPageCount(pageCountText);
		driver.navigate().back();
		
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ca-nstab-main")));
		
		List<WebElement> element1 = driver.findElements(By.id("ca-watch"));
		boolean present = element1.size() > 0;
		if(present){
			element1.get(0).click();
		} else{
			driver.findElement(By.id("ca-unwatch")).click();
		}
		System.out.println("clicked add page to watch list");
		wchListLnk = driver.findElement(By.id("pt-watchlist"));
		wchListLnk.click();
		
		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(pgeCountXpath)));
		pageCountValue = driver.findElement(By.xpath(pgeCountXpath)); 
		pageCountText = pageCountValue.getText();
		int nextPageCount = getPageCount(pageCountText);
		System.out.println("get new page count "+nextPageCount+","+prevPageCount);
		
		if(present){
			
			assertTrue("Validation failed - Text \"Not added in watch list\" NOT FOUND", nextPageCount > prevPageCount );
			System.out.println("Added to watch list");
		}else{
			assertTrue("Validation failed - Text \"Deleted from watch list\" NOT FOUND", nextPageCount < prevPageCount );
			System.out.println("Deleted from watch list");
		}
	}
	public int getPageCount(String pageCounttext){
		
		char[] ch = pageCounttext.toCharArray();
		int i =0;
		for(i = 0;i<ch.length; i++){
			if(Character.isDigit(ch[i])){
				break;
			}
		}
		int num = Character.getNumericValue(ch[i]);
		return num;
	}
	
	@After
	public void tearDown(){
		driver.close();
		System.out.println("Browser closed");
	}
	
	public void validationforLogin(WebDriver driver){
		// with space
		WebElement emailText = driver.findElement(By.id("Email"));
		emailText.submit();
		System.out.println(driver.getCurrentUrl());
	}
}
