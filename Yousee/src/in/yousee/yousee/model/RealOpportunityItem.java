package in.yousee.yousee.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.text.format.Time;
import android.util.Log;

public class RealOpportunityItem extends ProxyOpportunityItem
{

	private static final String TAG_SCHEDULE_LIST = "activityList";
	private ArrayList<OpportunitySchedule> opportunityScheduleList;

	public RealOpportunityItem(int id, String title, String opportunityType, String partner, String description, ArrayList<OpportunitySchedule> activityScheduleList)
	{
		super(id, title, opportunityType, partner, description);
		this.opportunityScheduleList = activityScheduleList;
	}

	public RealOpportunityItem(ProxyOpportunityItem proxyItem, ArrayList<OpportunitySchedule> activityScheduleList)
	{
		super(proxyItem.getId(), proxyItem.getTitle(), proxyItem.getOpportunityType(), proxyItem.getPartner(), proxyItem.getDescription());
		this.opportunityScheduleList = activityScheduleList;
	}

	public RealOpportunityItem(ProxyOpportunityItem proxyItem, String JSONString)
	{
		super(proxyItem.getId(), proxyItem.getTitle(), proxyItem.getOpportunityType(), proxyItem.getPartner(), proxyItem.getDescription());
		opportunityScheduleList = new ArrayList<RealOpportunityItem.OpportunitySchedule>();
		parseJSON(JSONString);

	}

	public ArrayList<OpportunitySchedule> getActivityScheduleList()
	{
		return opportunityScheduleList;
	}

	public void setActivityScheduleList(ArrayList<OpportunitySchedule> activityScheduleList)
	{
		this.opportunityScheduleList = activityScheduleList;
	}

	@Override
	public void parseJSON(String JSONString)
	{
		JSONArray array;
		try
		{
			JSONObject obj = new JSONObject(JSONString);
			array = obj.getJSONArray(TAG_SCHEDULE_LIST);

			for (int i = 0; i < array.length(); i++)
			{
				Log.i("tag", "" + i);
				JSONObject item = (JSONObject) array.get(i);
				opportunityScheduleList.add(new OpportunitySchedule(item));

			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	public class OpportunitySchedule implements JSONParsable
	{
		private static final String TAG_OPPORTUNITY_ID = "opportunityId";
		private static final String TAG_FROM_DATE = "fromDate";
		private static final String TAG_TO_DATE = "toDate";
		private static final String TAG_FROM_TIME = "fromTime";
		private static final String TAG_TO_TIME = "toTime";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_CITY = "city";
		private static final String TAG_VOL_REQ = "volReq";

		private int opportunityId;
		private Date fromDate, toDate;
		private Date fromTime, toTime;
		private String location;
		private String city;
		private int volReq;

		public OpportunitySchedule(int activityId, Date fromDate, Date toDate, Date fromTime, Date toTime, String location, String city, int volReq)
		{
			super();
			this.opportunityId = activityId;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.fromTime = fromTime;
			this.toTime = toTime;
			this.location = location;
			this.city = city;
			this.volReq = volReq;
		}

		public OpportunitySchedule(JSONObject jsonObject)
		{
			parseJSON(jsonObject);
		}

		public int getOpportunityId()
		{
			return opportunityId;
		}

		public void setOpportunityId(int opportunityId)
		{
			this.opportunityId = opportunityId;
		}

		public Date getFromDate()
		{
			return fromDate;
		}

		public String getFromDateString()
		{
			SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
			return df.format(this.fromDate);
		}

		public void setFromDate(Date fromDate)
		{
			this.fromDate = fromDate;
		}

		public void setFromDate(String fromDate)
		{

			SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
			try
			{
				this.fromDate = df.parse(fromDate);
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Date getToDate()
		{
			return toDate;
		}

		public String getToDateString()
		{
			SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
			return df.format(this.toDate);
		}

		public void setToDate(Date toDate)
		{
			this.toDate = toDate;
		}

		public void setToDate(String fromDate)
		{

			SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
			try
			{
				this.toDate = df.parse(fromDate);
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Date getFromTime()
		{
			return fromTime;
		}

		public String getFromTimeString()
		{
			if (toTime != null)
			{
				SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
				return df.format(this.fromTime);
			}
			else
				return "";

		}

		public void setFromTime(Date fromTime)
		{
			this.fromTime = fromTime;
		}

		public void setFromTime(String fromTime)
		{
			SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
			try
			{

				this.fromTime = df.parse(fromTime);
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Date getToTime()
		{
			return toTime;
		}

		public String getToTimeString()
		{
			if (toTime != null)
			{
				SimpleDateFormat df = new SimpleDateFormat("hh:ss", Locale.ENGLISH);
				return df.format(this.toTime);
			}
			else
				return "";
		}

		public void setToTime(Date toTime)
		{

			this.toTime = toTime;
		}

		public void setToTime(String toTime)
		{

			try
			{
				SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
				this.toTime = df.parse(toTime);
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				this.toTime = null;
				e.printStackTrace();
			}
		}

		public String getLocation()
		{
			return location;
		}

		public void setLocation(String location)
		{
			this.location = location;
		}

		public String getCity()
		{
			return city;
		}

		public void setCity(String city)
		{
			this.city = city;
		}

		public int getVolReq()
		{
			return volReq;
		}

		public void setVolReq(int volReq)
		{
			this.volReq = volReq;
		}

		@Override
		public void parseJSON(JSONObject JSONObject)
		{
			try
			{
				Log.i("tag", JSONObject.toString());
				this.opportunityId = JSONObject.getInt(TAG_OPPORTUNITY_ID);
				setFromDate(JSONObject.getString(TAG_FROM_DATE));
				setToDate(JSONObject.getString(TAG_TO_DATE));
				setFromTime(JSONObject.getString(TAG_FROM_TIME));
				setToTime(JSONObject.getString(TAG_TO_TIME));
				setLocation(JSONObject.getString(TAG_LOCATION));
				setCity(JSONObject.getString(TAG_CITY));
				setVolReq(JSONObject.getInt(TAG_VOL_REQ));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

	}
}
