package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.model.ProxyOpportunityItem;
import in.yousee.main.model.RealOpportunityItem;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

public class IndividualOpportunityItemActivity extends YouseeCustomActivity implements OnClickListener, OnResponseRecievedListener
{
	ProxyOpportunityItem proxyOpportunityItem;
	ImageView image;
	TextView titleTextView;
	TextView descriptionTextView;
	ArrayList<View> activityList;
	static boolean selectall = false;
	RealOpportunityItem realItem;
	IndividualOpportunityItemMiddleware builder;

	private static final String LOG_TAG = "tag";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.individual_opportunity_item);
		String jsonString = getIntent().getExtras().getString("result");
		Log.i("tag", "fjedfksdjfljddilfjgldfg " + jsonString);
		proxyOpportunityItem = new ProxyOpportunityItem(jsonString);
		IndividualOpportunityItemMiddleware.requestCode = RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST;
		builder = new IndividualOpportunityItemMiddleware(proxyOpportunityItem, this);
		GcmIntentService.resetNotificationCount(getApplicationContext());
		Log.d("debug_tag", "requestCode = " + IndividualOpportunityItemMiddleware.requestCode);
		Log.d("debug_tag", "requestCode = " + IndividualOpportunityItemMiddleware.requestCode);
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
		// ImageButton deselectAllButton = (ImageButton)
		// findViewById(R.id.deselectAll);static
		selectAllButton.setOnClickListener(this);
		// deselectAllButton.setOnClickListener(this);

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
			if (checkedAtleastOneCard == true)
			{
				Log.i(LOG_TAG, "Committing");
				IndividualOpportunityItemMiddleware.requestCode = RequestCodes.NETWORK_ACTIVITY_COMMIT;
				builder.preCommitExecute(realItem, checkedState);
				super.requestSenderMiddleware = builder;
				super.sendRequest();
			} else
			{
				Toast.makeText(getApplicationContext(), "select atleast one Schedule card to commit", Toast.LENGTH_LONG).show();
			}

		} else
		{
			Log.i(LOG_TAG, "Biscuit");
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
			Log.d("debug_tag", "real item created");
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
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				layout.addView(view);
			}
			setSupportProgressBarIndeterminateVisibility(false);
		} else if (requestCode == RequestCodes.NETWORK_ACTIVITY_COMMIT)
		{
			if ((Boolean) response)
			{
				Log.i("tag", "committed");
				Toast.makeText(getApplicationContext(), "Committed", Toast.LENGTH_SHORT).show();
			} else
			{
				Toast.makeText(getApplicationContext(), "failed to Commit", Toast.LENGTH_SHORT).show();
			}

			IndividualOpportunityItemMiddleware.requestCode = RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST;
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

		String string = "10:20";
		SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
		Date datex = df.parse(string);
		Log.i(LOG_TAG, df.format(datex));

		TextView titleView = (TextView) rowView.findViewById(R.id.title);
		titleView.setText("Schedule #" + i);

		TextView date = (TextView) rowView.findViewById(R.id.date);
		date.setText(schedule.getFromDateString() + " - " + schedule.getToDateString());

		TextView time = (TextView) rowView.findViewById(R.id.time);
		time.setText(schedule.getFromTimeString() + " - " + schedule.getToTimeString());

		TextView area = (TextView) rowView.findViewById(R.id.area);
		area.setText(schedule.getCity() + ", " + schedule.getLocation());

		TextView volReq = (TextView) rowView.findViewById(R.id.volReq);
		volReq.setText("Volunteers required :" + schedule.getVolReq());
		Log.i(LOG_TAG, "isCommitted : " + schedule.isCommitted());

		View commitView = (TextView) rowView.findViewById(R.id.commitTextView);
		commitView.setEnabled(schedule.isCommitted());
		if (commitView.isEnabled())
		{
			// titleView.setBackgroundResource(R.drawable.bottom_disabled_card);
			LinearLayout cardLayout = (LinearLayout) rowView.findViewById(R.id.card_layout);
			// cardLayout.setBackgroundResource(R.drawable.card_disabled);
			commitView.setVisibility(View.VISIBLE);
		}
		final View commitViewReference = commitView;
		rowView.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Log.i(LOG_TAG, "isenabled : " + commitViewReference.isEnabled());
				if (!commitViewReference.isEnabled())
				{
					int x = map.get(v);
					checkedState[x] = !(checkedState[x]);
					setScheduleSelected(v, checkedState[x]);
				}

			}
		});
		return rowView;

	}

	private void refreshActivityScheduleList()
	{
		if (layout != null)
		{
			layout.removeAllViewsInLayout();
		}
		checkList.removeAll(checkList);
		activityList.removeAll(activityList);
		i = 0;
		map.clear();
		IndividualOpportunityItemMiddleware.requestCode = RequestCodes.NETWORK_ACTIVITY_COMMIT;
		requestSenderMiddleware = builder;

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

		} else
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
				Log.i("tag", "sessionID = " + sessionId);
				commit();

			} else
			{
				Log.i("tag", "Entering Login screen");
				showCommitLoginScreen();
			}

			break;
		case R.id.selectAll:
			selectall = !(selectall);
			if (selectall == true)
			{
				Log.i(LOG_TAG, "selectall");
				selectAll();
				v.setBackgroundResource(R.drawable.deselectall);
			} else
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

	public void showCommitLoginScreen()
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
		if (checkedAtleastOneCard == true)
		{
			Intent intent = new Intent();
			Log.i("tag", "ACTIVITY_REQUEST_COMMIT_LOGIN");
			intent.setClass(this, in.yousee.main.LoginActivity.class);
			startActivityForResult(intent, RequestCodes.ACTIVITY_REQUEST_COMMIT_LOGIN);
		} else
		{
			Toast.makeText(getApplicationContext(), "select atleast one Schedule card to commit", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_COMMIT_LOGIN)
		{
			Log.i(LOG_TAG, "ACTIVITY_REQUEST_COMMIT_LOGIN");
			commit();
		}
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_LOGIN)
		{
			if (resultCode == RESULT_OK)
			{

				IndividualOpportunityItemMiddleware.requestCode = RequestCodes.NETWORK_REQUEST_OPPORTUNITY_SCHEDULE_LIST;
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
	public void onLoginSuccess()
	{
		refreshActivityScheduleList();
		super.onLoginSuccess();
	}

	@Override
	public Context getContext()
	{
		return this.getApplicationContext();

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