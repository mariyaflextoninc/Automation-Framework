package com.wiki.wiki_webui_test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadPropertiesForTest {
	private Properties prop;
	
	public Properties getPropertiesFromFile(){
		prop = new Properties();
		InputStream input = null;
		try{
			String filename = "config/pathfile.properties";
			input = new FileInputStream(filename);
    		if( input == null ){
    	            System.out.println("Sorry, unable to find " + filename);
    		    return null;
    		}
			prop.load(input);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(input != null){
				try{
					input.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}
