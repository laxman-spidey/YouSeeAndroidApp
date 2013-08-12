package in.yousee.yousee;

import in.yousee.yousee.model.ProxyOpportunityItem;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class IndividualOpportunityItemActivity extends SherlockActivity implements OnClickListener
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

		image = (ImageView) findViewById(R.id.catagoryIcon);
		image.setBackgroundResource(proxyOpportunityItem.getResourceOfCatagoryType());

		titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(proxyOpportunityItem.getTitle());

		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		descriptionTextView.setText(proxyOpportunityItem.getDescription());

		Button applyButton = (Button) findViewById(R.id.applyButton);
		applyButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		SessionHandler sessionHandler = new SessionHandler(this);
		String sessionId = null;
		if (sessionHandler.getSessionId(sessionId))
		{
			Log.i("tag", "sessionID = " +sessionId);
			commit();
			
		} else
		{
			showLoginScreen();
		}

	}

	public void commit()
	{
		Log.i("tag", "committed");
	}

	public void showLoginScreen()
	{

		Intent intent = new Intent();
		Log.i("tag", "showing LoginScreen");
		intent.setClass(this, in.yousee.yousee.LoginFragment.class);
		startActivity(intent);
	}

}
