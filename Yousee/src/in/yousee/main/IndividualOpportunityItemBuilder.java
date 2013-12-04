package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.constants.ServerFiles;
import in.yousee.main.model.CustomException;
import in.yousee.main.model.ProxyOpportunityItem;
import in.yousee.main.model.RealOpportunityItem;

import java.io.IOException;
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

	public static int requestCode;
	ProxyOpportunityItem proxy;
	OnResponseRecievedListener listener;
	RealOpportunityItem realOpportunityItem;
	boolean[] checkedState;

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
		Log.i("tag", "requestCode = " + requestCode);
		if (requestCode == RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST)
		{
			postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.ACTIVITY_SCHEDULE);
			nameValuePairs = new ArrayList<NameValuePair>();
			setRequestCode(RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST);
			addUserIdToPost();
			nameValuePairs.add(new BasicNameValuePair(TAG_ACTIVITY_ID, "" + proxy.getId()));

			encodePostRequest(nameValuePairs);

		}
		else if (requestCode == RequestCodes.NETWORK_ACTIVITY_COMMIT)
		{
			if (realOpportunityItem != null)
			{
				commitOpportunity(this.realOpportunityItem, this.checkedState);
			}
			else
			{
				Log.i("debug_tag", "preCommmitExecute() must be executed before committing");
			}
		}
		else
		{
			Log.i("debug_tag", "set requestCode to  RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST or RequestCodes.NETWORK_ACTIVITY_COMMIT ");
		}
		// cacheRequest(postRequest);
	}

	public void preCommitExecute(RealOpportunityItem realOpportunityItem, boolean checkedState[])
	{
		Log.i("tag", "pre Commit");
		IndividualOpportunityItemBuilder.requestCode = RequestCodes.NETWORK_ACTIVITY_COMMIT;
		this.realOpportunityItem = realOpportunityItem;
		this.checkedState = checkedState;
		assembleRequest();
	}

	private void commitOpportunity(RealOpportunityItem realOpportunityItem, boolean checkedState[])
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.ACTIVITY_COMMIT);
		nameValuePairs = new ArrayList<NameValuePair>();
		setRequestCode(RequestCodes.NETWORK_ACTIVITY_COMMIT);
		addUserIdToPost();
		String value = "";
		for (int i = 0; i < checkedState.length; i++)
		{
			Log.i("tag", " checked state : " + checkedState[i]);
			if (checkedState[i])
			{
				int id = this.realOpportunityItem.getActivityScheduleList().get(i).getOpportunityId();
				Log.i("tag", " checked id: " + id);
				value += id + ",";
			}

		}
		Log.i("tag", "opportunity Id values = " + value);
		value = value.substring(0, value.length() - 1);
		Log.i("tag", "opportunity Id values = " + value);
		nameValuePairs.add(new BasicNameValuePair(TAG_OPPORTUNITY_COMMITTED_ID, "" + value));

		encodePostRequest(nameValuePairs);

	}

	@Override
	public void serveResponse(String result, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST)
		{
			Log.d("tag", "schedule list response recieved");
			listener.onResponseRecieved(result, requestCode);
		}
		else if (requestCode == RequestCodes.NETWORK_ACTIVITY_COMMIT)
		{
			Log.d("tag", "commit response recieved");
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
