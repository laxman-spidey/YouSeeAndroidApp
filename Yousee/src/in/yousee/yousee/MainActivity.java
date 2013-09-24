package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.ProxyOpportunityItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends YouseeCustomActivity implements OnItemClickListener, OnResponseRecievedListener
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

		if (SessionHandler.isLoggedIn)
		{
			Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT).show();
		}
		buildOpportunityListForTheFirstTime();
		// sendLoginRequest(false);
		initiateExpandableList();
	}

	private void buildOpportunityListForTheFirstTime()
	{
		Log.i("tag", "building opportunity list");
		if (listBuilder == null)
		{
			listBuilder = new OpportunityListBuilder(this);
		}
		requestSenderChef = listBuilder;
		sendRequest();

	}

	public void buildOpportunityList(ArrayList<ProxyOpportunityItem> proxyList)
	{

		// Log.i("tag", "creating");
		// this.proxyList = proxyList;
		/*
		 * // ------------testing app with no network
		 * connection--------- proxyList = new
		 * ArrayList<ProxyOpportunityItem>(); for (int i = 0; i < 10;
		 * i++) { ProxyOpportunityItem testItem = new
		 * ProxyOpportunityItem(1, "kjfklsdjfkhsdkjfhsd", "Education",
		 * "",
		 * "jfskldjhfksdfkjsydjfknsdjkhfkjsdnfjsdhjkfhksdgkjsdgkjsdnjkshfkjsd"
		 * ); proxyList.add(testItem); } // ------------testing app with
		 * no network connection---------//
		 */

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

		OpportunityListAdapter adapter = new OpportunityListAdapter(getApplicationContext(), titles, types);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listview.setOnItemClickListener(this);
		Log.i(LOG_TAG, "progress bar : false");
		setSupportProgressBarIndeterminateVisibility(false);

	}

	private void setUpdateButtonOnClickListener()
	{
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				setSupportProgressBarIndeterminateVisibility(true);
				showFilterMenu(false);
				listBuilder = new OpportunityListBuilder(filterGroupList, MainActivity.this);

				requestSenderChef = listBuilder;
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

		// expand all Groupsrevenge
		expandAll();

		// add new item to the List
		// listener for child row click
		myList.setOnChildClickListener(myListItemClicked);
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

	// our child listener
	public OnChildClickListener myListItemClicked = new OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
		{

			// get the group header
			FilterGroupInfo headerInfo = filterGroupList.get(groupPosition);
			// get the child info
			FilterChildInfo detailInfo = headerInfo.getProductList().get(childPosition);
			// display it or do something with it

			CheckBox checkBox = detailInfo.getCheckBox();
			checkBox.setChecked(!checkBox.isChecked());
			Toast.makeText(getBaseContext(), "Clicked on Detail " + headerInfo.getName() + "/" + detailInfo.getName() + "  , " + checkBox.isChecked(), Toast.LENGTH_SHORT).show();
			return false;
		}

	};

	// our group listener
	private OnGroupClickListener myListGroupClicked = new OnGroupClickListener() {

		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
		{

			// get the group header
			FilterGroupInfo headerInfo = filterGroupList.get(groupPosition);
			// display it or do something with it
			Toast.makeText(getBaseContext(), "Child on Header " + headerInfo.getName(), Toast.LENGTH_SHORT).show();

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
			ArrayList<ProxyOpportunityItem> responseObject = (ArrayList<ProxyOpportunityItem>) response;
			buildOpportunityList(responseObject);
			setSupportProgressBarIndeterminateVisibility(false);
		}
		super.onResponseRecieved(response, requestCode);
	}

	@Override
	public Context getContext()
	{
		return this.getApplicationContext();

	}

}
