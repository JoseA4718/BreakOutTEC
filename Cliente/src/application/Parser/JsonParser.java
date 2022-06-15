package application.Parser;

import com.google.gson.Gson;
/**
 * Class that implements the Gson library to parse json files.
 *
 */
public class JsonParser {
	
	public static JsonTestClass deserializeJson(String userJson) {
		//String usrJson = "{"+ userJson + "}"; 

		Gson gson = new Gson();
		
		JsonTestClass json = gson.fromJson(userJson, JsonTestClass.class);
		
		if (json.matrix == null) {
			System.out.println("Lleg� nula");
		}
		
		return json;
		
	}
	
}
