package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RetryableActivity extends SherlockActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminate(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.default_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean refresh = false;
	public static final int REQUEST_RETRY = 1001;
	public static final String LOG_TAG = "tag";
	protected Chef requestSenderChef;

	public void promptRetry(String msg)
	{
		Intent intent = new Intent();
		intent.setClass(this, RetryActivity.class);
		intent.putExtra("errorMsg", msg);
		startActivityForResult(intent, REQUEST_RETRY);
	}

	public void sendRequest()
	{
		setSupportProgressBarIndeterminateVisibility(true);
		try
		{
			
			Log.i(LOG_TAG, "cooking");
			requestSenderChef.cook();
		}
		catch (CustomException e)
		{
			
			promptRetry(e.getErrorMsg());
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// Log.i(LOG_TAG, "retrying");
		// requestCode = RESULT_OK;
		if (requestCode == REQUEST_RETRY)
		{
			if (resultCode == RESULT_OK)
			{
				sendRequest();
			}
		}
		setSupportProgressBarIndeterminateVisibility(false);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_refresh:
			refresh = true;
			Log.i(LOG_TAG, "refreshing.............................................................");
			sendRequest();
			break;

		default:
			break;
		}
		return true;
	}

}
