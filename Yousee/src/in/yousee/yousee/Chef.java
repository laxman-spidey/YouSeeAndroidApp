package in.yousee.yousee;

import java.util.List;

import in.yousee.yousee.model.CustomException;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public abstract class Chef
{

	public static final String TAG_NETWORK_REQUEST_CODE = "requestCode";
	public static final String TAG_NETWORK_RESULT_CODE = "resultCode";
	protected List<NameValuePair> nameValuePairs;
	protected HttpPost postRequest;

	protected void setRequestCode(int requestCode)
	{
		nameValuePairs.add(new BasicNameValuePair(TAG_NETWORK_REQUEST_CODE, "" + requestCode));
		
			
	}

	public abstract void assembleRequest();

	public abstract void cook() throws CustomException;

	public abstract void serveResponse(String result, int requestCode);
}
