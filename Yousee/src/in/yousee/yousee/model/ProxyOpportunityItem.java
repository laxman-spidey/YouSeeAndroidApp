package in.yousee.yousee.model;

import in.yousee.yousee.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ProxyOpportunityItem implements JSONParsable
{
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_TYPE = "type";
	private static final String TAG_PARTNER = "partner";
	private static final String TAG_DESCRIPTION = "description";

	private static final String TAG_TYPE_EDUCATION = "Education";
	private static final String TAG_TYPE_ENVIRONMENT = "Environment";
	private static final String TAG_TYPE_HEALTH = "Health";

	private int id;
	private String title;
	private String opportunityType;
	private String partner;
	private String description;

	public ProxyOpportunityItem(int id, String title, String opportunityType, String partner, String description)
	{
		super();
		this.id = id;
		this.title = title;
		this.opportunityType = opportunityType;
		this.partner = partner;
		this.description = description;
	}

	public ProxyOpportunityItem(JSONObject jsonObject)
	{
		parseJSON(jsonObject);
	}

	public ProxyOpportunityItem(String jsonString)
	{
		parseJSON(jsonString);
	}

	public String getPartner()
	{
		return partner;
	}

	public void setPartner(String partner)
	{
		this.partner = partner;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getOpportunityType()
	{
		return opportunityType;
	}

	public void setOpportunityType(String opportunityType)
	{
		this.opportunityType = opportunityType;
	}

	public void parseJSON(String JSONString)
	{
		JSONObject json;
		try
		{
			json = new JSONObject(JSONString);
			parseJSON(json);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void parseJSON(JSONObject JSONObject)
	{

		try
		{
			this.id = JSONObject.getInt(TAG_ID);
			this.title = JSONObject.getString(TAG_TITLE);
			this.opportunityType = JSONObject.getString(TAG_TYPE);
			// this.partner = JSONObject.getString(TAG_PARTNER);
			// this.description =
			this.description = JSONObject.getString(TAG_DESCRIPTION);
			Log.i("tag", this.title);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String toJsonString()
	{
		String jsonString = new String();
		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject.put(TAG_ID, id);
			jsonObject.put(TAG_TITLE, title);
			jsonObject.put(TAG_TYPE, opportunityType);
			jsonObject.put(TAG_DESCRIPTION, description);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonString = jsonObject.toString();

		return jsonString;
	}

	public int getResourceOfCatagoryType()
	{
		if(opportunityType.equalsIgnoreCase(TAG_TYPE_EDUCATION))
			return R.drawable.ic_education;
		else if(opportunityType.equalsIgnoreCase(TAG_TYPE_ENVIRONMENT))
			return R.drawable.ic_environment;
		else if(opportunityType.equalsIgnoreCase(TAG_TYPE_HEALTH))
			return R.drawable.ic_health;
		else
			return R.drawable.uc_logo;
	}
}
