package in.yousee.yousee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class MainActivity extends SherlockActivity
{

	private FrameLayout filterFrame;
	private Button updateButton;
	ListView listview;
 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminate(false);
		// setSupportProgressBarIndeterminate(true);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		// setSupportProgressBarVisibility(true);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_main);
		filterFrame = (FrameLayout) findViewById(R.id.filterFrame);
		updateButton = (Button) findViewById(R.id.updateButton);
		setUpdateButtonOnClickListener();

		initiateExpandableList();
		createOpportunityListView();

		// sendRequest();
		// sendTestRequest();
	}

	@Override
	protected void onResume()
	{
		Log.i("tag", "onResume - progress bar has to be disappear");
		super.onResume();
	}

	private void createOpportunityListView()
	{

		listview = (ListView) findViewById(R.id.opportunityListview);

		Log.i("tag", "before getting attributes");

		Log.i("tag", "after getting attributes");
		String[] titles = new String[7];
		titles[0] = "Support Innovation through documentation - Documentation of UC projects reports";
		titles[1] = "title 1.. a longer one. This title is just a longer and longer one. Text in this title has no meaning. Don't read it.. Thanks for reading.kj sdfjshdl fsdkfhsjkdh fudgjdfguid fgdf gdfg iudfgd g";
		titles[2] = "It's a short title.";
		titles[6] = "Organise Monthly Donation camp for Reusable and Recyclable items at your Office or Residential Community.";
		titles[3] = "It's a longer title. But not as long as first title. Like first one, this title also doesn't mean anything";
		titles[4] = "bla. bla. bla. bla. bla. bla. bla. bla. bla. bla. ";
		titles[5] = "no more titles.. don't swipe down";
		OpportunityListAdapter adapter = new OpportunityListAdapter(getApplicationContext(), titles);
		listview.setAdapter(adapter);

	}

	private void setUpdateButtonOnClickListener()
	{
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				Iterator<FilterGroupInfo> it = deptList.iterator();
				while (it.hasNext())
				{
					FilterGroupInfo group = it.next();
					Log.i("tag", "+" + group.getName());
					Iterator<FilterChildInfo> childIterator = group.getProductList().iterator();
					while (childIterator.hasNext())
					{
						FilterChildInfo child = childIterator.next();
						if (child.isChecked())
						{
							Log.i("tag", "	" + child.getName());
						}
					}

				}
				showFilterMenu(false);
			}
		});

	}

	private boolean filterMenuVisibility = false;

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

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

	public void sendRequest()
	{

	}

	private LinkedHashMap<String, FilterGroupInfo> myDepartments = new LinkedHashMap<String, FilterGroupInfo>();
	private ArrayList<FilterGroupInfo> deptList = new ArrayList<FilterGroupInfo>();

	private FilterListAdapter listAdapter;
	private ExpandableListView myList;

	public void initiateExpandableList()
	{
		// get reference to the ExpandableListView
		myList = (ExpandableListView) findViewById(R.id.expandableListView1);
		// setPadding();
		// create the adapter by passing your ArrayList data
		listAdapter = new FilterListAdapter(MainActivity.this, deptList);
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
		// collapseAll();
		// expand the group where item was just added
		// myList.expandGroup(groupPosition);
		// set the current group to be selected so that it becomes
		// visible
		// myList.setSelectedGroup(groupPosition);
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

		addProduct("Acivity Type", "Onsite");
		addProduct("Acivity Type", "Offsite");

	}

	// our child listener
	public OnChildClickListener myListItemClicked = new OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
		{

			Log.d("tag", "child is selected");
			// get the group header
			FilterGroupInfo headerInfo = deptList.get(groupPosition);
			// get the child info
			FilterChildInfo detailInfo = headerInfo.getProductList().get(childPosition);
			// display it or do something with it
			Toast.makeText(getBaseContext(), "Clicked on Detail " + headerInfo.getName() + "/" + detailInfo.getName(), Toast.LENGTH_SHORT).show();
			CheckBox checkBox = detailInfo.getCheckBox();
			checkBox.setChecked(!checkBox.isChecked());
			return false;
		}

	};

	// our group listener
	private OnGroupClickListener myListGroupClicked = new OnGroupClickListener() {

		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
		{

			// get the group header
			FilterGroupInfo headerInfo = deptList.get(groupPosition);
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
		FilterGroupInfo headerInfo = myDepartments.get(department);
		// add the group if doesn't exists
		if (headerInfo == null)
		{
			headerInfo = new FilterGroupInfo();
			headerInfo.setName(department);
			myDepartments.put(department, headerInfo);
			deptList.add(headerInfo);
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
		groupPosition = deptList.indexOf(headerInfo);
		return groupPosition;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void fancyThat(View v)
	{
		v.getBackground().setAlpha(50);
	}
}
