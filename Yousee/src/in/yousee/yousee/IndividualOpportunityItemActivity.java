package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.ProxyOpportunityItem;
import in.yousee.yousee.model.RealOpportunityItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class IndividualOpportunityItemActivity extends YouseeCustomActivity implements OnClickListener, OnResponseRecievedListener
{
	ProxyOpportunityItem proxyOpportunityItem;
	ImageView image;
	TextView titleTextView;
	TextView descriptionTextView;
	ArrayList<View> activityList;
	static boolean selectall = false;
	RealOpportunityItem realItem;

	private static final String LOG_TAG = "tag";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.individual_opportunity_item);
		String jsonString = getIntent().getExtras().getString("result");

		proxyOpportunityItem = new ProxyOpportunityItem(jsonString);
		IndividualOpportunityItemBuilder builder = new IndividualOpportunityItemBuilder(proxyOpportunityItem, this);
		super.requestSenderChef = builder;

		image = (ImageView) findViewById(R.id.catagoryIcon);
		image.setBackgroundResource(proxyOpportunityItem.getResourceOfCatagoryType());

		titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(proxyOpportunityItem.getTitle());

		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		descriptionTextView.setText(proxyOpportunityItem.getDescription());

		Button applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(this);

		ImageButton selectAllButton = (ImageButton) findViewById(R.id.selectAll);
		// ImageButton deselectAllButton = (ImageButton)
		// findViewById(R.id.deselectAll);
		selectAllButton.setOnClickListener(this);
		// deselectAllButton.setOnClickListener(this);

		activityList = new ArrayList<View>();
		super.sendRequest();

	}

	public void commit()
	{
		Log.i("tag", "committed");
		// showLoginScreen();
	}

	LinearLayout layout;
	int i = 0;
	HashMap<View, Integer> map;
	ArrayList<Boolean> checkList;
	boolean checkedState[];

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onResponseRecieved(Object response, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST)
		{
			if (super.refresh == true)
			{
				refreshActivityScheduleList();
			}
			realItem = new RealOpportunityItem(proxyOpportunityItem, (String) response);
			ArrayList<RealOpportunityItem.OpportunitySchedule> scheduleList = realItem.getActivityScheduleList();
			checkedState = new boolean[scheduleList.size()];
			layout = (LinearLayout) findViewById(R.id.rootLay);
			checkList = new ArrayList<Boolean>();
			map = new HashMap<View, Integer>();
			Iterator<RealOpportunityItem.OpportunitySchedule> iterator = scheduleList.iterator();
			while (iterator.hasNext())
			{
				Log.i(LOG_TAG, "adding.....");
				View view = null;
				try
				{
					view = buildScheduleCard(iterator.next());
				}
				catch (ParseException e)// ImageButton
							// deselectAllButton =
							// (ImageButton)
				// findViewById(R.id.deselectAll); e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				layout.addView(view);
			}
			setSupportProgressBarIndeterminateVisibility(false);
		}
		super.onResponseRecieved(response, requestCode);

	}

	public View buildScheduleCard(RealOpportunityItem.OpportunitySchedule schedule) throws ParseException
	{

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup rowView = (ViewGroup) inflater.inflate(R.layout.schedule_card, layout, false);

		checkedState[i] = false;
		map.put(rowView, i++);
		activityList.add(rowView);

		String string = "10:20";
		SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
		Date datex = df.parse(string);
		Log.i(LOG_TAG, df.format(datex));

		TextView textView = (TextView) rowView.findViewById(R.id.title);
		textView.setText("Schedule #" + i);

		TextView date = (TextView) rowView.findViewById(R.id.date);
		date.setText(schedule.getFromDateString() + " - " + schedule.getToDateString());

		TextView time = (TextView) rowView.findViewById(R.id.time);
		time.setText(schedule.getFromTimeString() + " - " + schedule.getToTimeString());

		TextView area = (TextView) rowView.findViewById(R.id.area);
		area.setText(schedule.getCity() + ", " + schedule.getLocation());

		TextView volReq = (TextView) rowView.findViewById(R.id.volReq);
		volReq.setText("Volunteers required :" + schedule.getVolReq());

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				int x = map.get(v);
				checkedState[x] = !(checkedState[x]);
				setScheduleSelected(v, checkedState[x]);
				Toast.makeText(getApplicationContext(), "clicked" + x, Toast.LENGTH_SHORT).show();

			}
		});
		return rowView;

	}

	private void refreshActivityScheduleList()
	{

		layout.removeAllViewsInLayout();
		checkList.removeAll(checkList);
		activityList.removeAll(activityList);
		i = 0;
		map.clear();

	}

	private void selectAll()
	{
		Iterator<View> it = activityList.iterator();
		int i = 0;
		while (it.hasNext())
		{
			checkedState[i++] = true;
			View view = (View) it.next();
			setScheduleSelected(view, true);

		}
	}

	private void deselectAll()
	{
		Iterator<View> it = activityList.iterator();
		int i = 0;
		while (it.hasNext())
		{
			checkedState[i++] = false;
			View view = (View) it.next();
			setScheduleSelected(view, false);

		}
	}

	private void setScheduleSelected(View v, boolean check)
	{
		ImageView imgView = (ImageView) v.findViewById(R.id.selectView);
		// LinearLayout imgView= (LinearLayout)
		// v.findViewById(R.id.selectViewLayout);
		// LinearLayout card = (LinearLayout) v.findViewById(R.id.card);

		if (check)
		{
			// card.setBackgroundResource(R.drawable.disabled_card);
			imgView.setVisibility(View.VISIBLE);

		}
		else
		{
			// card.setBackgroundResource(R.drawable.border);
			imgView.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.applyButton:
			SessionHandler sessionHandler = new SessionHandler(this);
			String sessionId = null;
			if (sessionHandler.isLoggedIn)
			{
				Log.i("tag", "sessionID = " + sessionId);
				Toast.makeText(getApplicationContext(), sessionId, Toast.LENGTH_LONG).show();

				commit();

			}
			else
			{
				Log.i("tag", "Entering Login screen");
				showLoginScreen();
			}

			break;
		case R.id.selectAll:
			selectall = !(selectall);
			if (selectall == true)
			{
				Log.i(LOG_TAG, "selectall");
				selectAll();
				v.setBackgroundResource(R.drawable.deselectall);
			}
			else
			{
				Log.i(LOG_TAG, "de-selectall");
				deselectAll();
				v.setBackgroundResource(R.drawable.selectall);
			}

			break;

		default:
			break;
		}

	}

	public void showLoginScreen()
	{
		Intent intent = new Intent();
		Log.i("tag", "showing LoginScreen");
		intent.setClass(this, in.yousee.yousee.LoginActivity.class);
		startActivityForResult(intent, RequestCodes.ACTIVITY_REQUEST_COMMIT_LOGIN);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_COMMIT_LOGIN)
		{
			commit();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return true;

	}

	@Override
	public Context getContext()
	{
		return this.getApplicationContext();

	}

}