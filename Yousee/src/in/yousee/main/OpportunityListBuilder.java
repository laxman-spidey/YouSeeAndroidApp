package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.constants.ServerFiles;
import in.yousee.main.model.CustomException;
import in.yousee.main.model.ProxyOpportunityItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OpportunityListBuilder extends Middleware
{
	private String TAG_FIRSTTIME = "firstTime";
	private String TAG_UPDATE = "update";
	private boolean getNextelements;
	private boolean isFilterEnabled;

	private OnResponseRecievedListener listener;

	public OpportunityListBuilder(ArrayList<FilterGroupInfo> filterGroupList, OnResponseRecievedListener listener)
	{
		this.listener = listener;

		// super.requestCode = Chef.OPPORTUNITY_LIST_REQUEST_CODE;
		assembleRequest(filterGroupList);
	}

	public OpportunityListBuilder(OnResponseRecievedListener listener)
	{
		this.listener = listener;

		assembleRequest();

	}

	@Override
	public void assembleRequest()
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.VOLUNTEERING_OPPORTUNITIES);

		nameValuePairs = new ArrayList<NameValuePair>(2);
		setRequestCode(RequestCodes.NETWORK_REQUEST_OPPORTUNITY_LIST);
		nameValuePairs.add(new BasicNameValuePair(TAG_FIRSTTIME, "true"));

		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

	}

	public void getDataOnScroll(boolean isScrolled,  boolean isFilterEnabled)
	{
		this.getNextelements = isScrolled;
		this.isFilterEnabled = isFilterEnabled;
	}
	
	protected void assembleRequest(ArrayList<FilterGroupInfo> filterGroupList)
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + ServerFiles.VOLUNTEERING_OPPORTUNITIES);
		nameValuePairs = new ArrayList<NameValuePair>(2);
		super.setRequestCode(RequestCodes.NETWORK_REQUEST_OPPORTUNITY_LIST);
		boolean noItemChecked = true;
		nameValuePairs.add(new BasicNameValuePair(TAG_UPDATE, "true"));
		Log.i("tag", "+hfksjdhfldhfjkghdfjkgdkjfhgjkdhfjgkhdfjkghjkdfhgkjdshfg");
		Iterator<FilterGroupInfo> it = filterGroupList.iterator();
		while (it.hasNext())
		{
			FilterGroupInfo group = it.next();

			// nameValuePairs.add(new
			// BasicNameValuePair(group.getName(), "true"));
			Iterator<FilterChildInfo> childIterator = group.getProductList().iterator();
			boolean isOneOfChildChecked = false;
			String string = "";
			while (childIterator.hasNext())
			{
				FilterChildInfo child = childIterator.next();

				if (child.isChecked())
				{
					isOneOfChildChecked = true;
					string += child.getName() + ",";
					// Log.i("tag", " " + group.getName() +
					// ": " + child.getName());
				}

			}
			if (isOneOfChildChecked)
			{
				noItemChecked = false;
				string = string.substring(0, string.length() - 1);

				nameValuePairs.add(new BasicNameValuePair(group.getName(), "" + string));
			}

		}
		Log.i("tag", "name value pairs " + nameValuePairs.toString());

		try
		{
			if(noItemChecked)
			{
				assembleRequest();
			}
			else
			{
				postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void serveResponse(String result, int requestCode)
	{

		// if (requestCode ==
		// RequestCodes.NETWORK_REQUEST_OPPORTUNITY_LIST)
		{
			JSONObject json;

			ArrayList<ProxyOpportunityItem> proxyItemList = new ArrayList<ProxyOpportunityItem>();
			try
			{

				json = new JSONObject(result);
				int resultCount = json.getInt("resultCount");
				String totalCount = json.getString("totalCount");

				JSONArray list = json.getJSONArray("list");

				for (int i = 0; i < list.length(); i++)
				{
					proxyItemList.add(new ProxyOpportunityItem(list.getJSONObject(i)));
				}

			}
			catch (JSONException e)
			{
				Log.i("tag", "exception caught");
				
				e.printStackTrace();
			}
			Log.i("tag", "item list length = " + proxyItemList.size());
			listener.onResponseRecieved(proxyItemList, requestCode);
		}
		// else
		{
			// listener.onResponseRecieved(result, requestCode);
		}
	}

	@Override
	public Context getContext()
	{
		return listener.getContext();
	}

}
