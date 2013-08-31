package in.yousee.yousee.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SessionData implements JSONParsable
{
	private final String TAG_SESSION_ID = "sessionId";
	private final String TAG_USER_ID = "userId";
	private final String TAG_USER_TYPE_ID = "userTypeId";
	private final String TAG_SUCCESS = "successFlag";

	private boolean success;
	private String sessionId;
	private String userId;
	private String userType;

	public SessionData(JSONObject JsonObject)
	{
		parseJSON(JsonObject);
	}

	public SessionData(String JSONString)
	{
		try
		{
			Log.i("tag", JSONString);
			parseJSON(new JSONObject(JSONString));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserType()
	{
		return userType;
	}

	public void setUserType(String userType)
	{
		this.userType = userType;
	}

	@Override
	public void parseJSON(JSONObject JSONObject)
	{
		try
		{
			setSessionId(JSONObject.getString(TAG_SESSION_ID));
			setSuccess(JSONObject.getBoolean(TAG_SUCCESS));
			setUserId(JSONObject.getString(TAG_USER_ID));
			setUserType(JSONObject.getString(TAG_USER_TYPE_ID));

		} catch (JSONException e)
		{
			e.printStackTrace();
		}

	}

}
