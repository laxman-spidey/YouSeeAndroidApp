package in.yousee.yousee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends SherlockActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		// setSupportProgressBarVisibility(true);
		// setSupportProgressBarIndeterminate(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initiateExpandableList();
		// sendRequest();
		// sendTestRequest();
	}

	private boolean filterMenuVisibility = false;

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) {
	 * 
	 * switch (item.getItemId()) { case R.id.action_filter:
	 * filterMenuVisibility = !(filterMenuVisibility); //
	 * showFilterMenu(filterMenuVisibility);
	 * 
	 * break;
	 * 
	 * default: break; } return true; }
	 * 
	 * public void showFilterMenu(boolean visibility) { RelativeLayout lay =
	 * (RelativeLayout) findViewById(R.id.filterMenuLayout); if (visibility)
	 * lay.setVisibility(View.VISIBLE); else
	 * lay.setVisibility(View.INVISIBLE); }
	 */

	public void sendRequest()
	{

	}

	private LinkedHashMap<String, HeaderInfo> myDepartments = new LinkedHashMap<String, HeaderInfo>();
	private ArrayList<HeaderInfo> deptList = new ArrayList<HeaderInfo>();

	private MyListAdapter listAdapter;
	private ExpandableListView myList;

	public void initiateExpandableList()
	{
		// get reference to the ExpandableListView
		myList = (ExpandableListView) findViewById(R.id.expandableListView1);
		// create the adapter by passing your ArrayList data
		listAdapter = new MyListAdapter(MainActivity.this, deptList);
		// attach the adapter to the list
		myList.setAdapter(listAdapter);

		Log.i("tag", "before expand all");
		// expand all Groups
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

		// collapse all groups
		//collapseAll();
		// expand the group where item was just added
		//myList.expandGroup(groupPosition);
		// set the current group to be selected so that it becomes
		// visible
		//myList.setSelectedGroup(groupPosition);
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

		addProduct("Apparel", "Activewear");
		addProduct("Apparel", "Jackets");
		addProduct("Apparel", "Shorts");

		addProduct("Beauty", "Fragrances");
		addProduct("Beauty", "Makeup");

	}

	// our child listener
	private OnChildClickListener myListItemClicked = new OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
		{

			// get the group header
			HeaderInfo headerInfo = deptList.get(groupPosition);
			// get the child info
			DetailInfo detailInfo = headerInfo.getProductList().get(childPosition);
			// display it or do something with it
			Toast.makeText(getBaseContext(), "Clicked on Detail " + headerInfo.getName() + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();
			return false;
		}

	};

	// our group listener
	private OnGroupClickListener myListGroupClicked = new OnGroupClickListener() {

		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
		{

			// get the group header
			HeaderInfo headerInfo = deptList.get(groupPosition);
			// display it or do something with it
			Toast.makeText(getBaseContext(), "Child on Header " + headerInfo.getName(), Toast.LENGTH_LONG).show();

			return false;
		}

	};

	// here we maintain our products in various departments
	private int addProduct(String department, String product)
	{

		int groupPosition = 0;

		// check the hash map if the group already exists
		HeaderInfo headerInfo = myDepartments.get(department);
		// add the group if doesn't exists
		if (headerInfo == null)
		{
			headerInfo = new HeaderInfo();
			headerInfo.setName(department);
			myDepartments.put(department, headerInfo);
			deptList.add(headerInfo);
		}

		// get the children for the group
		ArrayList<DetailInfo> productList = headerInfo.getProductList();
		// size of the children list
		int listSize = productList.size();
		// add to the counter
		listSize++;

		// create a new child and add that to the group
		DetailInfo detailInfo = new DetailInfo();
		detailInfo.setSequence(String.valueOf(listSize));
		detailInfo.setName(product);
		productList.add(detailInfo);
		headerInfo.setProductList(productList);

		// find the group position inside the list
		groupPosition = deptList.indexOf(headerInfo);
		return groupPosition;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
