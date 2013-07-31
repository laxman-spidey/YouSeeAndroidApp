package in.yousee.yousee.model;

import java.util.ArrayList;
import java.util.Date;

import android.text.format.Time;

public class RealOpportunityItem extends ProxyOpportunityItem
{
	private ArrayList<ActivitySchedule> activityScheduleList;

	public RealOpportunityItem(int volunteerId, String title, String description, int opportunityType, String partner, ArrayList<ActivitySchedule> activityScheduleList)
	{
		super(volunteerId, title, description, opportunityType, partner);
		this.activityScheduleList = activityScheduleList;
	}

	public RealOpportunityItem(int volunteerId, String title, String description, int opportunityType, String partner)
	{
		super(volunteerId, title, description, opportunityType, partner);

	}

	public ArrayList<ActivitySchedule> getActivityScheduleList()
	{
		return activityScheduleList;
	}

	public void setActivityScheduleList(ArrayList<ActivitySchedule> activityScheduleList)
	{
		this.activityScheduleList = activityScheduleList;
	}

	public class ActivitySchedule
	{
		private int activityId;
		private Date fromDate, toDate;
		private Time fromTime, toTime;
		private String location;
		private String city;
		private int volReq;

		public ActivitySchedule(int activityId, Date fromDate, Date toDate, Time fromTime, Time toTime, String location, String city, int volReq)
		{
			super();
			this.activityId = activityId;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.fromTime = fromTime;
			this.toTime = toTime;
			this.location = location;
			this.city = city;
			this.volReq = volReq;
		}

		public int getActivityId()
		{
			return activityId;
		}

		public void setActivityId(int activityId)
		{
			this.activityId = activityId;
		}

		public Date getFromDate()
		{
			return fromDate;
		}

		public void setFromDate(Date fromDate)
		{
			this.fromDate = fromDate;
		}

		public Date getToDate()
		{
			return toDate;
		}

		public void setToDate(Date toDate)
		{
			this.toDate = toDate;
		}

		public Time getFromTime()
		{
			return fromTime;
		}

		public void setFromTime(Time fromTime)
		{
			this.fromTime = fromTime;
		}

		public Time getToTime()
		{
			return toTime;
		}

		public void setToTime(Time toTime)
		{
			this.toTime = toTime;
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

	}
}
