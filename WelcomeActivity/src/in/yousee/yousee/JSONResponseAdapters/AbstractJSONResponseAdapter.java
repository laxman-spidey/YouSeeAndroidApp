package in.yousee.yousee.JSONResponseAdapters;

import in.yousee.yousee.model.SessionData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public abstract class AbstractJSONResponseAdapter {
	
	public SessionData DecodeJson(JSONObject jsonObject) throws JSONException {
		
		String JSONStringData = "{\"successFlag\":\"daskhdjas\" \"sessionId\":\"fgjkasgkjdas\" \"userId\":\"fdjkshfjk\" \"userTypeId\":\"oiiywejfnsdnfjsdgf\" }";
		
		JSONObject MetaData = new JSONObject(JSONStringData);
		SessionData userData = new SessionData();
		userData.setSessionId(jsonObject.getString("sessionId"));
		userData.setUserId(jsonObject.getString("userId"));
		userData.setUserType(jsonObject.getString("userType"));
		userData.setUsername(jsonObject.getString("username"));
		userData.setSuccesFlag(jsonObject.getString("succesFlag"));

		return userData;

	}

}
