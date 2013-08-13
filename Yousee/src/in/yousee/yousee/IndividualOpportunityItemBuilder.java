package in.yousee.yousee;

import in.yousee.yousee.model.ProxyOpportunityItem;

public class IndividualOpportunityItemBuilder implements Chef
{
	IndividualOpportunityItemActivity sourceActivity;
	ProxyOpportunityItem proxy;

	public IndividualOpportunityItemBuilder(ProxyOpportunityItem proxy, IndividualOpportunityItemActivity sourceActivity)
	{
		this.sourceActivity = sourceActivity;
		this.proxy=proxy;

	}

	@Override
	public void assembleRequest()
	{
		
	}

	@Override
	public void cook()
	{

	}

	@Override
	public void serveResponse(String result)
	{

	}

}
