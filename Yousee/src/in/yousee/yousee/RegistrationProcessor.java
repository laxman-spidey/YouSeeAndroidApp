package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.constants.ServerFiles;
import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.RegistrationFormObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.GpsStatus.Listener;
import android.util.Log;
import android.widget.Toast;

public class RegistrationProcessor extends Chef
{

	public static final String TAG_FIRSTNAME = "firstName";
	public static final String TAG_LASTNAME = "lastName";
	public static final String TAG_EMAIL = "email";
	public static final String TAG_PASSWORD = "password";
	public static final String TAG_DOB = "dob";

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
		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void cook() throws CustomException
	{
		NetworkConnectionHandler connectionHandler = new NetworkConnectionHandler(responseListener.getContext());
		connectionHandler.sendRequest(postRequest, this);

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

}
