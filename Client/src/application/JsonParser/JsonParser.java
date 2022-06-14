package application.JsonParser;

import com.google.gson.*;

public class JsonParser {
	
	public static JsonTest deserializeJson(String userJson) {
		//String usrJson = "{"+ userJson + "}"; 

		Gson gson = new Gson();
		
		JsonTest json = gson.fromJson(userJson, JsonTest.class);
		
		if (json.matrix == null) {
			System.out.println("Llegó nula");
		}
		
		return json;
		
	}
	
}
