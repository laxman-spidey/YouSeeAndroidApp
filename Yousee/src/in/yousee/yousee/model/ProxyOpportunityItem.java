package in.yousee.yousee.model;

public class ProxyOpportunityItem
{

	private int volunteerId;
	private String title;
	private String description;
	private int opportunityType;
	private String partner;

	public ProxyOpportunityItem(int volunteerId, String title, String description, int opportunityType, String partner)
	{
		super();
		this.volunteerId = volunteerId;
		this.title = title;
		this.description = description;
		this.opportunityType = opportunityType;
		this.partner = partner;
	}

	public int getVolunteerId()
	{
		return volunteerId;
	}

	public void setVolunteerId(int volunteerId)
	{
		this.volunteerId = volunteerId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getOpportunityType()
	{
		return opportunityType;
	}

	public void setOpportunityType(int opportunityType)
	{
		this.opportunityType = opportunityType;
	}

	public String getPartner()
	{
		return partner;
	}

	public void setPartner(String partner)
	{
		this.partner = partner;
	}

}
