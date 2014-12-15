package com.mcigroup.eventmanager.front.helper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Tools {
	
	
	public static Gson gson = new Gson();
	
	public static Gson gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	public static <T> T toObject(String json, Class<T> classToReturn){ return gson.fromJson(json, classToReturn); }
	
	public static String loadResource(String resoucePath){
		String toReturn = "";
		
		if(resoucePath != null && !resoucePath.isEmpty()){
			try {
				InputStream resource = Tools.class.getResourceAsStream(resoucePath);
				toReturn = IOUtils.toString(resource, "UTF-8");
			} catch (IOException e) {
				toReturn = "";
				System.err.println(ExceptionUtils.getStackTrace(e));
			}
		}
		
		return toReturn;
	}
	
	public static boolean isJSONValid(String jsonString) {
	      try {
	          gson.fromJson(jsonString, Object.class);
	          return true;
	      } catch(com.google.gson.JsonSyntaxException ex) { 
	          return false;
	      }
	}
	

}