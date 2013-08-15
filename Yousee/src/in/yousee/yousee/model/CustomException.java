package in.yousee.yousee.model;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

public class CustomException extends Exception implements JSONParsable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1234567890;

	public static final int USERNAME_INVALID = 1;
	public static final int PASSWORD_INVALID = 2;
	public static final int NETWORK_NOT_FOUND = 3;
	public static final int NO_INTERNET_CONNECTIVITY = 4;
	public static final int INVALID_URL = 5;
	public static final int LOGIN_ERROR = 6;
	public static final int CUSTOM_ERROR = 7;

	private String errorMsg;
	public int errorCode;

	public CustomException(int errorCode)
	{
		this.errorCode = errorCode;
		setErrorMsg(errorCode);

	}
	public CustomException(String errorMsg)
	{
		setErrorCode(CUSTOM_ERROR);
		setErrorMsg(errorMsg);
	}

	private void setErrorMsg(int errorCode)
	{
		switch (errorCode)
			{
			case USERNAME_INVALID:
				setErrorMsg("Username invalid");
				break;
			case PASSWORD_INVALID:
				setErrorMsg("Password invalid");
				break;
			case NETWORK_NOT_FOUND:
				setErrorMsg("No network found. Please enable wifi or mobile data and try again.");
				break;
			case NO_INTERNET_CONNECTIVITY:
				setErrorMsg("Your device is not connected to internet.");
				break;
			case LOGIN_ERROR:
				setErrorMsg("Details you have entered are incorrect.");
				break;
			default:
				break;
			}

	}

	public String getErrorMsg()
	{
		return errorMsg;
	}

	private void setErrorMsg(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	@Override
	public void parseJSON(JSONObject JSONObject)
	{

	}

	public static void showToastError(Context context, CustomException e)
	{
		Toast.makeText(context, e.getErrorMsg(), Toast.LENGTH_LONG).show();
	}

}
