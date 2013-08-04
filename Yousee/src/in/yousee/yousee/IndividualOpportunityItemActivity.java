package in.yousee.yousee;

import in.yousee.yousee.model.ProxyOpportunityItem;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class IndividualOpportunityItemActivity extends SherlockActivity
{
	ProxyOpportunityItem proxyOpportunityItem;
	ImageView image;
	TextView titleTextView;
	TextView descriptionTextView; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individual_opportunity_item);  
		String jsonString = getIntent().getExtras().getString("result");
		proxyOpportunityItem = new ProxyOpportunityItem(jsonString);
		image = (ImageView) findViewById(R.id.opportunityCatagoryIcon);
		titleTextView = (TextView) findViewById(R.id.opportunityTitle);
		descriptionTextView =(TextView) findViewById(R.id.descriptionTextView);
		titleTextView.setText(proxyOpportunityItem.getTitle());
		descriptionTextView.setText(proxyOpportunityItem.getDescription());

	}

}
