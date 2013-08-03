package in.yousee.yousee.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ProxyOpportunityItem implements JSONParsable
{
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "name";
	private static final String TAG_TYPE = "type";
	private static final String TAG_PARTNER = "partner";
	private static final String TAG_DESCRIPTION = "description";

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

	public ProxyOpportunityItem(String JSONString)
	{
		parseJSON(JSONString);
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
			this.partner = JSONObject.getString(TAG_PARTNER);
			this.description = JSONObject.getString(TAG_DESCRIPTION);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
