package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.constants.ServerFiles;
import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.ProxyOpportunityItem;
import in.yousee.yousee.model.RealOpportunityItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class IndividualOpportunityItemBuilder extends Middleware
{
	private static final String TAG_ACTIVITY_ID = "activity_id";
	private static final String TAG_OPPORTUNITY_COMMITTED_ID = "opp_id";

	ProxyOpportunityItem proxy;
	OnResponseRecievedListener listener;

	public IndividualOpportunityItemBuilder(ProxyOpportunityItem proxy, OnResponseRecievedListener responseListener)
	{

		listener = responseListener;
		this.proxy = proxy;
		// super.requestCode =
		// Chef.OPPORTUNITY_SCHEDULE_LIST_REQUEST_CODE;
		assembleRequest();

	}

	@Override
	public void assembleRequest()
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.ACTIVITY_SCHEDULE);
		nameValuePairs = new ArrayList<NameValuePair>();
		setRequestCode(RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST);

		nameValuePairs.add(new BasicNameValuePair(TAG_ACTIVITY_ID, "" + proxy.getId()));

		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		// cacheRequest(postRequest);
	}

	public void commitOpportunity(RealOpportunityItem reaOpportunityItem, boolean checkedState[])
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.ACTIVITY_COMMIT);
		nameValuePairs = new ArrayList<NameValuePair>();
		setRequestCode(RequestCodes.NETWORK_ACTIVITY_COMMIT);
		for(int i=0; i<checkedState.length;i++)
		{
			int id = reaOpportunityItem.getActivityScheduleList().get(i).getOpportunityId();
			
			nameValuePairs.add(new BasicNameValuePair(TAG_ACTIVITY_ID, "" + proxy.getId()));
		}
		

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
	public void serveResponse(String result, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST)
		{
			listener.onResponseRecieved(result, requestCode);
		}
		else if (requestCode == RequestCodes.NETWORK_ACTIVITY_COMMIT)
		{
			JSONObject obj;
			Boolean res = false;
			try
			{
				obj = new JSONObject(result);
				res = obj.getBoolean("success");
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			listener.onResponseRecieved(res, requestCode);

		}
	}

	@Override
	public Context getContext()
	{

		return listener.getContext();
	}

}
