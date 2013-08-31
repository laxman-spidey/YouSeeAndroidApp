package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.util.Log;

public class RetryableActivity extends SherlockActivity
{
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
		super.onActivityResult(requestCode, resultCode, data);
	}

}
