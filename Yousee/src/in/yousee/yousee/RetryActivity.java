package in.yousee.yousee;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class RetryActivity extends Activity implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_retry);
		String errorMsg = getIntent().getStringExtra("errorMsg");
		TextView errorMsgTextview = (TextView) findViewById(R.id.errorMsg);
		errorMsgTextview.setText(errorMsg);
		Button retryButton = (Button) findViewById(R.id.retryButton);
		retryButton.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v)
	{
		setResult(RESULT_OK, new Intent("Retry"));
		finish();
	}

}
