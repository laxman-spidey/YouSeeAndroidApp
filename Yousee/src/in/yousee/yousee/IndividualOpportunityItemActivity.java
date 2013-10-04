package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.ProxyOpportunityItem;
import in.yousee.yousee.model.RealOpportunityItem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
	IndividualOpportunityItemBuilder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.individual_opportunity_item);
		String jsonString = getIntent().getExtras().getString("result");

		proxyOpportunityItem = new ProxyOpportunityItem(jsonString);
		IndividualOpportunityItemBuilder.requestCode = RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST;
		builder = new IndividualOpportunityItemBuilder(proxyOpportunityItem, this);

		super.requestSenderMiddleware = builder;

		image = (ImageView) findViewById(R.id.catagoryIcon);
		image.setBackgroundResource(proxyOpportunityItem.getResourceOfCatagoryType());

		titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(proxyOpportunityItem.getTitle());

		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		descriptionTextView.setText(proxyOpportunityItem.getDescription());

		Button applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(this);

		ImageButton selectAllButton = (ImageButton) findViewById(R.id.selectAll);
		selectAllButton.setOnClickListener(this);

		activityList = new ArrayList<View>();
		super.sendRequest();

	}

	public void commit()
	{
		if (realItem != null)
		{
			boolean checkedAtleastOneCard = false;
			for (int i = 0; i < checkedState.length; i++)
			{
				if (checkedState[i] == true)
				{
					checkedAtleastOneCard = true;
					break;
				}
			}
			if (checkedAtleastOneCard)
			{
				IndividualOpportunityItemBuilder.requestCode = RequestCodes.NETWORK_ACTIVITY_COMMIT;
				builder.preCommitExecute(realItem, checkedState);
				super.requestSenderMiddleware = builder;
				super.sendRequest();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "select atleast one Schedule card to commit", Toast.LENGTH_LONG).show();
			}

		}
		else
		{
			
		}

	}

	LinearLayout layout;
	int i = 0;
	HashMap<View, Integer> map;
	ArrayList<Boolean> checkList;
	boolean checkedState[];

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
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
				View view = null;
				try
				{
					view = buildScheduleCard(iterator.next());
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				layout.addView(view);
			}
			setSupportProgressBarIndeterminateVisibility(false);
		}
		else if (requestCode == RequestCodes.NETWORK_ACTIVITY_COMMIT)
		{
			if ((Boolean) response)
			{
				Toast.makeText(getApplicationContext(), "Committed", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "failed to Commit", Toast.LENGTH_SHORT).show();
			}

			IndividualOpportunityItemBuilder.requestCode = RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST;
			builder.assembleRequest();
			refreshActivityScheduleList();
			super.reloadActivity();

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

		ImageView commitView = (ImageView) rowView.findViewById(R.id.commitView);
		commitView.setEnabled(schedule.isCommitted());
		if (commitView.isEnabled())
		{
			commitView.setVisibility(View.VISIBLE);
		}
		final ImageView commitViewReference = commitView;
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				if (!commitViewReference.isEnabled())
				{
					int x = map.get(v);
					checkedState[x] = !(checkedState[x]);
					setScheduleSelected(v, checkedState[x]);
					Toast.makeText(getApplicationContext(), "clicked" + x, Toast.LENGTH_SHORT).show();
				}

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
		if (check)
		{
			imgView.setVisibility(View.VISIBLE);

		}
		else
		{
			imgView.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.applyButton:
			String sessionId = null;

			if (SessionHandler.isSessionIdExists(getApplicationContext()))
			{
				sessionId = SessionHandler.getSessionId(getApplicationContext());
				Toast.makeText(getApplicationContext(), sessionId, Toast.LENGTH_LONG).show();

				commit();
			}
			else
			{
				showCommitLoginScreen();
			}

			break;
		case R.id.selectAll:
			selectall = !(selectall);
			if (selectall == true)
			{
				selectAll();
				v.setBackgroundResource(R.drawable.deselectall);
			}
			else
			{
				deselectAll();
				v.setBackgroundResource(R.drawable.selectall);
			}

			break;

		default:
			break;
		}

	}

	public void showCommitLoginScreen()
	{
		Intent intent = new Intent();
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
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_LOGIN)
		{
			if (resultCode == RESULT_OK)
			{

				IndividualOpportunityItemBuilder.requestCode = RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST;
				builder.assembleRequest();
				refreshActivityScheduleList();
			}
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