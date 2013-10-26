package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.model.ProxyOpportunityItem;
import in.yousee.yousee.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends YouseeCustomActivity implements OnItemClickListener
{

	private FrameLayout filterFrame;
	private Button updateButton;
	ListView listview;
	OpportunityListBuilder listBuilder;
	ArrayList<ProxyOpportunityItem> proxyList;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		filterFrame = (FrameLayout) findViewById(R.id.filterFrame);
		updateButton = (Button) findViewById(R.id.updateButton);
		setUpdateButtonOnClickListener();

		buildOpportunityListForTheFirstTime();
		initiateExpandableList();
	}

	private void buildOpportunityListForTheFirstTime()
	{
		Log.i("tag", "building opportunity list");
		if (listBuilder == null)
		{
			listBuilder = new OpportunityListBuilder(this);
		}
		requestSenderMiddleware = listBuilder;
		sendRequest();

	}

	public void buildOpportunityList(ArrayList<ProxyOpportunityItem> proxyList)
	{

		this.proxyList = proxyList;
		listview = (ListView) findViewById(R.id.opportunityListview);

		String[] titles = new String[proxyList.size()];
		int[] types = new int[proxyList.size()];
		int index = 0;
		Iterator<ProxyOpportunityItem> it = proxyList.iterator();
		while (it.hasNext())
		{
			ProxyOpportunityItem item = (ProxyOpportunityItem) it.next();
			titles[index] = item.getTitle();
			types[index] = item.getResourceOfCatagoryType();
			index++;

		}

		OpportunityListAdapter adapter = new OpportunityListAdapter(getApplicationContext(), proxyList);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listview.setOnItemClickListener(this);
		Log.i(LOG_TAG, "progress bar : false");
		setSupportProgressBarIndeterminateVisibility(false);

	}

	private void setUpdateButtonOnClickListener()
	{
		updateButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setSupportProgressBarIndeterminateVisibility(true);
				showFilterMenu(false);
				listBuilder = new OpportunityListBuilder(filterGroupList, MainActivity.this);

				requestSenderMiddleware = listBuilder;
				sendRequest();

			}
		});

	}

	private boolean filterMenuVisibility = false;

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
		case R.id.action_filter:
			filterMenuVisibility = !(filterMenuVisibility);
			showFilterMenu(filterMenuVisibility);

			break;

		default:
			break;
		}
		return true;
	}

	public void showFilterMenu(boolean visibility)
	{
		filterMenuVisibility = visibility;
		if (visibility)
			filterFrame.setVisibility(View.VISIBLE);
		else
			filterFrame.setVisibility(View.INVISIBLE);
	}

	private LinkedHashMap<String, FilterGroupInfo> filterCatagories = new LinkedHashMap<String, FilterGroupInfo>();
	private ArrayList<FilterGroupInfo> filterGroupList = new ArrayList<FilterGroupInfo>();

	private FilterListAdapter listAdapter;
	private ExpandableListView myList;

	public void initiateExpandableList()
	{
		// get reference to the ExpandableListView
		myList = (ExpandableListView) findViewById(R.id.expandableListView1);
		// setPadding();
		// create the adapter by passing your ArrayList data
		listAdapter = new FilterListAdapter(MainActivity.this, filterGroupList);
		// attach the adapter to the list
		myList.setAdapter(listAdapter);

		// expand all Groups
		expandAll();

		// add new item to the List
		// listener for child row click
		// myList.setOnChildClickListener(myListItemClicked);
		// listener for group heading click
		myList.setOnGroupClickListener(myListGroupClicked);

		// add a new item to the list
		loadData();
		// notify the list so that changes can take effect
		listAdapter.notifyDataSetChanged();

	}

	// method to expand all groups
	private void expandAll()
	{
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++)
		{
			myList.expandGroup(i);
		}
	}

	// method to collapse all groups
	private void collapseAll()
	{
		int count = listAdapter.getGroupCount();
		for (int i = 0; i < count; i++)
		{
			myList.collapseGroup(i);
		}
	}

	// load some initial data into out list
	private void loadData()
	{
		addProduct("Area", "Education");
		addProduct("Area", "Environment");
		addProduct("Area", "Health");

		addProduct("Domain", "Documentation");
		addProduct("Domain", "Project Activity");
		addProduct("Domain", "Technology");

		addProduct("City", "Bangalore");
		addProduct("City", "Hyderabad");
		addProduct("City", "Lucknow");

		addProduct("Activity_Type", "Onsite");
		addProduct("Activity_Type", "Offsite");

	}

	// our group listener
	private OnGroupClickListener myListGroupClicked = new OnGroupClickListener()
	{

		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
		{

			// get the group header
			FilterGroupInfo headerInfo = filterGroupList.get(groupPosition);
			ArrayList<FilterChildInfo> detailInfoList = headerInfo.getProductList();
			Iterator<FilterChildInfo> it = detailInfoList.iterator();
			Log.i(LOG_TAG, "group: " + headerInfo.getName());
			while (it.hasNext())
			{
				FilterChildInfo o = it.next();
				Log.i(LOG_TAG, o.getName() + " : " + o.isChecked());
			}

			// display it or do something with it

			return false;
		}

	};

	// here we maintain our products in various departments
	private int addProduct(String department, String product)
	{

		int groupPosition = 0;

		// check the hash map if the group already exists
		FilterGroupInfo headerInfo = filterCatagories.get(department);
		// add the group if doesn't exists
		if (headerInfo == null)
		{
			headerInfo = new FilterGroupInfo();
			headerInfo.setName(department);
			filterCatagories.put(department, headerInfo);
			filterGroupList.add(headerInfo);
		}

		// get the children for the group
		ArrayList<FilterChildInfo> productList = headerInfo.getProductList();
		// size of the children list
		int listSize = productList.size();
		// add to the counter
		listSize++;

		// create a new child and add that to the group
		FilterChildInfo detailInfo = new FilterChildInfo();
		detailInfo.setSequence(String.valueOf(listSize));
		detailInfo.setName(product);
		productList.add(detailInfo);
		headerInfo.setProductList(productList);

		// find the group position inside the list
		groupPosition = filterGroupList.indexOf(headerInfo);
		return groupPosition;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{

		Intent intent = new Intent();
		intent.setClass(this, IndividualOpportunityItemActivity.class);
		intent.putExtra("result", proxyList.get(position).toJsonString());
		startActivity(intent);

	}

	@Override
	public void onResponseRecieved(Object response, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_OPPORTUNITY_LIST)
		{
			TextView infoMsg = (TextView) findViewById(R.id.infoMsg);
			ArrayList<ProxyOpportunityItem> responseObject = (ArrayList<ProxyOpportunityItem>) response;
			if (responseObject.size() > 0)
			{
				infoMsg.setVisibility(View.GONE);
				buildOpportunityList(responseObject);
				listview.setVisibility(View.VISIBLE);

			} else
			{
				Log.i(LOG_TAG, "Showing info msg");
				listview.setVisibility(View.GONE);
				infoMsg.setVisibility(View.VISIBLE);
			}

			setSupportProgressBarIndeterminateVisibility(false);
		}
		super.onResponseRecieved(response, requestCode);
	}

	@Override
	public Context getContext()
	{
		return this.getApplicationContext();

	}

	@Override
	public void reloadActivity()
	{
		requestSenderMiddleware = listBuilder;
		super.reloadActivity();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop()
	{
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}


}
