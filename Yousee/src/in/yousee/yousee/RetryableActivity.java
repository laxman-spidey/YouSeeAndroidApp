package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.CustomException;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RetryableActivity extends SherlockFragmentActivity implements UsesLoginFeature
{

	MenuItem loginMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// setSupportProgressBarIndeterminate(true);
		setSupportProgressBarIndeterminateVisibility(false);

		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.default_menu, menu);
		loginMenuItem = (MenuItem) menu.findItem(R.id.action_login);
		if (SessionHandler.isLoggedIn)
			loginMenuItem.setTitle("Logout");
		else
			loginMenuItem.setTitle("Login");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean refresh = false;

	public static final String LOG_TAG = "tag";
	protected Chef requestSenderChef;

	public void promptRetry(String msg)
	{
		Intent intent = new Intent();
		intent.setClass(this, RetryActivity.class);
		intent.putExtra("errorMsg", msg);
		startActivityForResult(intent, RequestCodes.ACTIVITY_REQUEST_RETRY);
	}

	public void sendRequest()
	{
		setSupportProgressBarIndeterminateVisibility(true);
		try
		{
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
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_RETRY)
		{
			if (resultCode == RESULT_OK)
			{
				reloadActivity();
			}
		}
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_LOGIN)
		{

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
			reloadActivity();
			break;
		case R.id.action_login:
			if (!(SessionHandler.isLoggedIn))
			{
				if (sendLoginRequest())
					setloginMenuItem(true);
			}
			else
			{
				logout();
			}
			break;
		case R.id.action_register:
			showRegistrationForm();
			break;
		default:
			break;
		}
		return true;
	}

	private void setloginMenuItem(boolean loggedIn)
	{

		if (loggedIn)
			loginMenuItem.setTitle("Logout");
		else
			loginMenuItem.setTitle("Login");
	}

	public boolean sendLoginRequest()
	{
		try
		{

			SessionHandler sessionHandler = new SessionHandler(getApplicationContext());
			if (sessionHandler.isSessionIdExists() == false)
			{
				Log.i(LOG_TAG, "sessionId doesn't exist");
				if (sessionHandler.isLoginCredentialsExists() == true)
				{
					Log.i(LOG_TAG, "login data doesn't exist");
					Toast.makeText(getApplicationContext(), "Logging in..", Toast.LENGTH_SHORT).show();
					sessionHandler.loginExec();
					SessionHandler.isLoggedIn = true;
					return true;
				}
				else
				{
					showLoginScreen();
					Toast.makeText(getApplicationContext(), "new user", Toast.LENGTH_SHORT).show();
					return false;
				}

			}
			else
			{

				return false;
			}
		}
		catch (CustomException e)
		{

			Toast.makeText(getApplicationContext(), "Login Error occured.", Toast.LENGTH_SHORT).show();
			return false;

		}

	}

	private boolean logout()
	{
		try
		{

			SessionHandler sessionHandler = new SessionHandler(getApplicationContext());
			sessionHandler.logout();
			SessionHandler.isLoggedIn = false;
			setloginMenuItem(false);
			return true;
		}
		catch (CustomException e)
		{

			Toast.makeText(getApplicationContext(), "Error occured.", Toast.LENGTH_SHORT).show();
			return false;

		}

	}

	private void showRegistrationForm()
	{

		Intent intent = new Intent();
		Log.i("tag", "showing Registration Activity");
		intent.setClass(this, in.yousee.yousee.RegistrationActivity.class);
		startActivity(intent);
	}

	private void reloadActivity()
	{
		sendRequest();
	}

	@Override
	public void onLoginFailed()
	{
		;
	}

	@Override
	public void onLoginSuccess()
	{
		SessionHandler.isLoggedIn = true;
		reloadActivity();

	}

	public void showLoginScreen()
	{
		Intent intent = new Intent();
		Log.i("tag", "showing LoginScreen");
		intent.setClass(this, in.yousee.yousee.LoginActivity.class);
		startActivity(intent);

	}

}
