package in.yousee.yousee;

import org.apache.http.client.methods.HttpPost;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.CustomException;

public abstract class Chef
{
	
	public static final String TAG_NETWORK_REQUEST_CODE = "requestCode";
	
	protected void setRequestCode(int requestCode)
	{
		postRequest.setHeader(Chef.TAG_NETWORK_REQUEST_CODE, ""+requestCode);
	}
	protected HttpPost postRequest;
	public abstract void assembleRequest();
	public abstract void cook() throws CustomException;
	public abstract void serveResponse(String result, int requestCode);
	
	

}
