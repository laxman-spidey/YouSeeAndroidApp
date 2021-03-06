package in.yousee.main;

import java.io.UnsupportedEncodingException;
import java.util.List;

import in.yousee.main.model.CustomException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public abstract class Middleware
{
	public static final String TAG_NETWORK_REQUEST_CODE = "requestCode";
	public static final String TAG_NETWORK_RESULT_CODE = "resultCode";
	public static final String TAG_USER_ID = "userId";
	protected List<NameValuePair> nameValuePairs;
	protected HttpPost postRequest;

	protected void setRequestCode(int requestCode)
	{
		nameValuePairs.add(new BasicNameValuePair(TAG_NETWORK_REQUEST_CODE, "" + requestCode));
	}

	protected void addUserIdToPost()
	{
		if (SessionHandler.isSessionIdExists(getContext()))
		{
			nameValuePairs.add(new BasicNameValuePair(TAG_USER_ID, "" + SessionHandler.getUserId(getContext())));
		}
	}

	public abstract void assembleRequest();

	public void sendRequest() throws CustomException
	{
		NetworkConnectionHandler connectionHandler = new NetworkConnectionHandler(getContext(), this);
		if (NetworkConnectionHandler.isNetworkConnected(getContext()))
		{
			connectionHandler.execute(postRequest);
		}
	}

	protected void encodePostRequest(List<NameValuePair> nameValuePairs)
	{
		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public abstract void serveResponse(String result, int requestCode);

	public abstract Context getContext();
}
