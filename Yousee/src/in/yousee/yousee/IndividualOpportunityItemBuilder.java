package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.ProxyOpportunityItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class IndividualOpportunityItemBuilder extends Chef
{
	private static final String TAG_ACTIVITY_ID = "activity_id";
	IndividualOpportunityItemActivity sourceActivity;
	ProxyOpportunityItem proxy;
	HttpPost postRequest;
	OnResponseRecievedListener listener;
	

	public IndividualOpportunityItemBuilder(ProxyOpportunityItem proxy, IndividualOpportunityItemActivity sourceActivity)
	{
		this.sourceActivity = sourceActivity;
		listener = sourceActivity;
		this.proxy=proxy;
		assembleRequest();

	}

	@Override
	public void assembleRequest()
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.ACTIVITY_SCHEDULE);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(TAG_ACTIVITY_ID, ""+proxy.getId()));
		
		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		cacheRequest(postRequest);
	}

	@Override
	public void cook() throws CustomException
	{
		NetworkConnectionHandler networkHandler = new NetworkConnectionHandler(sourceActivity);
		networkHandler.sendRequest(postRequest, this);

	}

	@Override
	public void serveResponse(String result)
	{
		Log.i("tag", "fahjsdhfsgdfjhsdjkfgsd");
		listener.onResponseRecieved(result);
	}

}
