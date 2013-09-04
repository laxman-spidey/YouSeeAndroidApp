package in.yousee.yousee;

import org.apache.http.client.methods.HttpPost;

import in.yousee.yousee.model.CustomException;

public abstract class Chef
{
	public static final int LOGIN_REQUEST_CODE = 100;
	public static final int OPPORTUNITY_LIST_REQUEST_CODE = 101;
	public static final int OPPORTUNITY_SCHEDULE_LIST_REQUEST_CODE = 102;
	public int requestCode;
	
	protected HttpPost postRequest;
	public abstract void assembleRequest();
	public abstract void cook() throws CustomException;
	public abstract void serveResponse(String result);
	public  void resendRequest() throws CustomException
	{
		this.cook();
	}
	protected void cacheRequest(HttpPost postRequest)
	{
		this.postRequest = postRequest;
	}

}
