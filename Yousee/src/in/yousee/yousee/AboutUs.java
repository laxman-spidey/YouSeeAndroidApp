package in.yousee.yousee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class AboutUs extends SherlockActivity implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		TextView emailView = (TextView) findViewById(R.id.emailIntentTextView);
		emailView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
		{ getResources().getString(R.string.yousee_email_id) });
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

}
