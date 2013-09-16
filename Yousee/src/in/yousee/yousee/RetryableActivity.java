package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.CustomException;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RetryableActivity extends SherlockFragmentActivity implements UsesLoginFeature, OnResponseRecievedListener
{

	MenuItem loginMenuItem;
	Menu menu;

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
		this.menu = menu;
		loginMenuItem = (MenuItem) menu.findItem(R.id.action_login);
		if (SessionHandler.isLoggedIn)
		{
			loginMenuItem.setTitle("Logout");
			menu.removeItem(R.id.action_register);
		}
		else
		{
			loginMenuItem.setTitle("Login");
		}
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

			Log.i(LOG_TAG, "network not connected exception found");
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
				if (sendLoginRequest(true))
					setMenuState(true);
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

	private void setMenuState(boolean loggedIn)
	{

		MenuItem loginMenu = menu.getItem(R.id.action_login);
		if (loggedIn)
		{
			loginMenu.setTitle("Logout");
			menu.removeItem(R.id.action_register);
		}
		else
		{
			loginMenu.setTitle("Login");

		}

	}

	public boolean sendLoginRequest(boolean loginMenuClicked)
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
					if (loginMenuClicked)
					{
						showLoginScreen();
					}
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
			sessionHandler.logout(this);

			setMenuState(false);
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
		Toast.makeText(getContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoginSuccess()
	{
		SessionHandler.isLoggedIn = true;
		Toast.makeText(getContext(), "logged in successfully.", Toast.LENGTH_SHORT).show();
		reloadActivity();

	}

	public void showLoginScreen()
	{
		Intent intent = new Intent();
		Log.i("tag", "showing LoginScreen");
		intent.setClass(this, in.yousee.yousee.LoginActivity.class);
		startActivity(intent);

	}

	@Override
	public void onResponseRecieved(Object response, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_LOGOUT)
		{
			SessionHandler.isLoggedIn = false;
			Toast.makeText(getApplicationContext(), "Successfully Logged out.", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public Context getContext()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
