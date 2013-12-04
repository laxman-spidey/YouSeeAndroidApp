package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.constants.ServerFiles;
import in.yousee.main.model.RegistrationFormObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

public class RegistrationProcessor extends Middleware
{

	public static final String TAG_FIRSTNAME = "firstName";
	public static final String TAG_LASTNAME = "lastName";
	public static final String TAG_EMAIL = "email";
	public static final String TAG_PASSWORD = "password";
	public static final String TAG_DOB = "dob";
	public static final String TAG_PHNO = "phNo";
	public static final String TAG_CITY = "city";

	private OnResponseRecievedListener responseListener;
	private RegistrationFormObject regForm;

	public RegistrationProcessor(OnResponseRecievedListener responseListener, RegistrationFormObject regForm)
	{
		this.responseListener = responseListener;
		this.regForm = regForm;
		assembleRequest();
	}

	@Override
	public void assembleRequest()
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.PROCESS_REGISTRATION);
		nameValuePairs = new ArrayList<NameValuePair>();
		setRequestCode(RequestCodes.NETWORK_REQUEST_REGISTER);
		nameValuePairs.add(new BasicNameValuePair(TAG_FIRSTNAME, regForm.getFirstName()));
		nameValuePairs.add(new BasicNameValuePair(TAG_LASTNAME, regForm.getLastname()));
		nameValuePairs.add(new BasicNameValuePair(TAG_EMAIL, regForm.getEmail()));
		nameValuePairs.add(new BasicNameValuePair(TAG_PASSWORD, regForm.getPassword()));
		nameValuePairs.add(new BasicNameValuePair(TAG_DOB, regForm.getDob()));
		nameValuePairs.add(new BasicNameValuePair(TAG_PHNO, regForm.getPhNo()));
		nameValuePairs.add(new BasicNameValuePair(TAG_CITY, regForm.getCity()));
		encodePostRequest(nameValuePairs);

	}

	@Override
	public void serveResponse(String result, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_REGISTER)
		{

			responseListener.onResponseRecieved(result, requestCode);
		}
		Log.i("tag", "result is = " + result);
	}

	@Override
	public Context getContext()
	{

		return responseListener.getContext();
	}

}
