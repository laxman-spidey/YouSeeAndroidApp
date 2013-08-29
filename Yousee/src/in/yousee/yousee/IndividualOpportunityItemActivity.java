package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.ProxyOpportunityItem;
import in.yousee.yousee.model.RealOpportunityItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class IndividualOpportunityItemActivity extends SherlockActivity implements OnClickListener, OnResponseRecievedListener
{
	ProxyOpportunityItem proxyOpportunityItem;
	ImageView image;
	TextView titleTextView;
	TextView descriptionTextView;
	ArrayList<View> activityList;
	static boolean selectall = false;

	private static final String LOG_TAG = "tag";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.individual_opportunity_item);
		String jsonString = getIntent().getExtras().getString("result");

		proxyOpportunityItem = new ProxyOpportunityItem(jsonString);
		IndividualOpportunityItemBuilder builder = new IndividualOpportunityItemBuilder(proxyOpportunityItem, this);
		try
		{
			builder.cook();
		} catch (CustomException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
		try
		{
			tokkatest();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
			{
			case R.id.applyButton:
				SessionHandler sessionHandler = new SessionHandler(this);
				String sessionId = null;
				if (sessionHandler.getSessionId(sessionId))
				{
					Log.i("tag", "sessionID = " + sessionId);
					Toast.makeText(getApplicationContext(), sessionId, Toast.LENGTH_LONG).show();

					commit();

				} else
				{
					Log.i("tag", "Entering Login screen");
					showLoginScreen();
				}

				break;
			case R.id.selectAll:
				selectall = !(selectall);
				if (selectall == true)
				{
					Log.i(LOG_TAG,"selectall");
					selectAll();
					v.setBackgroundResource(R.drawable.deselectall);
				}
				else
				{
					Log.i(LOG_TAG,"de-selectall");
					deselectAll();
					v.setBackgroundResource(R.drawable.selectall);
				}
				
				break;

			default:
				break;
			}

		// showLoginScreen();
	}

	public void commit()
	{
		Log.i("tag", "committed");
	}

	public void showLoginScreen()
	{
		Intent intent = new Intent();
		Log.i("tag", "showing LoginScreen");
		intent.setClass(this, in.yousee.yousee.LoginActivity.class);
		startActivity(intent);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
			{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			}
		return super.onOptionsItemSelected(item);
	}

	LinearLayout layout;
	int i = 0;
	HashMap<View, Integer> map;
	ArrayList<Boolean> checkList;
	boolean check[];

	public void tokkatest() throws ParseException
	{
		layout = (LinearLayout) findViewById(R.id.rootLay);
		checkList = new ArrayList<Boolean>();
		map = new HashMap<View, Integer>();
		/*
		 * check = new boolean[7]; layout.addView(add());
		 * layout.addView(add()); layout.addView(add());
		 * layout.addView(add()); layout.addView(add());
		 * layout.addView(add()); layout.addView(add());
		 */
		// String JSONString = new String();
		// String string = "Jan 2, 2013";
		// Date date = new SimpleDateFormat("mmm d, yyyy",
		// Locale.ENGLISH).parse(string);

		// ////////////////////

		String JSONString = new String();
		JSONString = testJSONString();
		
		// /////////////////////
	}

	public View add() throws ParseException
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.schedule_card, layout, false);
		check[i] = false;
		map.put(rowView, i++);
		activityList.add(rowView);
		TextView textView = (TextView) rowView.findViewById(R.id.title);

		String string = "10:20";
		SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
		Date date = df.parse(string);
		Log.i(LOG_TAG, df.format(date));

		// String string = "10:20";
		// SimpleDateFormat df = new SimpleDateFormat("hh:mm",
		// Locale.ENGLISH);
		// Date date = new
		// SimpleDateFormat("hh:mm",Locale.ENGLISH).parse(string);

		TextView todate = (TextView) rowView.findViewById(R.id.date);
		todate.setText(df.format(date));

		textView.setText("Schedule #" + i);
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				int x = map.get(v);
				check[x] = !(check[x]);
				setScheduleSelected(v, check[x]);
				Toast.makeText(getApplicationContext(), "clicked" + x, Toast.LENGTH_SHORT).show();

			}
		});
		return rowView;

	}

	public View buildScheduleCard(RealOpportunityItem.OpportunitySchedule schedule) throws ParseException
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.schedule_card, layout, false);
		check[i] = false;
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
		volReq.setText("Volunteers req" + schedule.getVolReq());

		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				int x = map.get(v);
				check[x] = !(check[x]);
				setScheduleSelected(v, check[x]);
				Toast.makeText(getApplicationContext(), "clicked" + x, Toast.LENGTH_SHORT).show();

			}
		});
		return rowView;

	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{

		super.onSaveInstanceState(outState);
		outState.putBooleanArray("activitySelections", check);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		check = savedInstanceState.getBooleanArray("activitySelections");
		for (int i = 0; i < check.length; i++)
		{
			setScheduleSelected(activityList.get(i), check[i]);
		}

	}

	private void setScheduleSelected(View v, boolean check)
	{
		ImageView imgView = (ImageView) v.findViewById(R.id.selectView);

		if (check)
		{
			imgView.setVisibility(View.VISIBLE);
		} else
		{
			imgView.setVisibility(View.INVISIBLE);
		}

	}

	private void selectAll()
	{
		Iterator<View> it = activityList.iterator();
		int i = 0;
		while (it.hasNext())
		{
			check[i++] = true;
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
			check[i++] = false;
			View view = (View) it.next();
			setScheduleSelected(view, false);

		}
	}

	private String testJSONString()
	{
		InputStream is = getResources().openRawResource(R.raw.samplejsonarray);
		String res = null;
		try
		{
			res = readIt(is);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}

	private String readIt(InputStream stream) throws IOException
	{

		InputStreamReader is = new InputStreamReader(stream);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null)
		{
			// System.out.println(read);
			sb.append(read);
			read = br.readLine();

		}

		return sb.toString();
	}

	@Override
	public void onResponseRecieved(Object response)
	{
		RealOpportunityItem realItem = new RealOpportunityItem(proxyOpportunityItem, (String) response);

		ArrayList<RealOpportunityItem.OpportunitySchedule> scheduleList = realItem.getActivityScheduleList();
		check = new boolean[scheduleList.size()];
		Iterator<RealOpportunityItem.OpportunitySchedule> iterator = scheduleList.iterator();
		while (iterator.hasNext())
		{
			Log.i(LOG_TAG, "adding.....");
			View view = null;
			try
			{
				view = buildScheduleCard(iterator.next());
			} catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			layout.addView(view);
		}

	}

}
