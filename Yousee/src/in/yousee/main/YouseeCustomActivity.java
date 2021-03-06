package in.yousee.main;

import in.yousee.main.constants.RequestCodes;
import in.yousee.main.model.CustomException;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class YouseeCustomActivity extends SherlockFragmentActivity implements UsesLoginFeature, OnResponseRecievedListener
{

	MenuItem loginMenuItem;
	Menu menu;
	boolean loginMenuClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setWindowProgressBar();
		// getOverflowMenu();
		super.onCreate(savedInstanceState);
	}

	protected void setWindowProgressBar()
	{
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminate(true);
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		/*
		 * getSupportMenuInflater().inflate(R.menu.default_menu, menu);
		 * this.menu = menu;
		 * setMenuState(SessionHandler.isSessionIdExists
		 * (getApplicationContext()));
		 */
		getSupportMenuInflater().inflate(R.menu.default_menu, menu);
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		Log.i(LOG_TAG, "onprepare optionsmenu");

		setMenuState(SessionHandler.isSessionIdExists(getApplicationContext()));

		return super.onPrepareOptionsMenu(menu);
	}

	private void getOverflowMenu()
	{

		try
		{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null)
			{
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean refresh = false;

	public static final String LOG_TAG = "tag";
	protected Middleware requestSenderMiddleware;

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
		
		if (NetworkConnectionHandler.isExecuting == false)
		{
			try
			{
				requestSenderMiddleware.sendRequest();
			}
			catch (CustomException e)
			{

				Log.i(LOG_TAG, "network not connected exception found");
				promptRetry(e.getErrorMsg());
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Please wait.. ", Toast.LENGTH_SHORT).show();
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
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_LOGIN || requestCode == RequestCodes.ACTIVITY_REQUEST_REGISTRATION )
		{
			if (resultCode == RESULT_OK)
			{
				onLoginSuccess();
			}
			else if(resultCode == Activity.RESULT_FIRST_USER)
			{
				showRegistrationForm();
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
			reloadActivity();
			break;
		case R.id.action_login:
			sendLoginRequest(true);

			break;
		case R.id.action_logout:
			logout();
			break;
		case R.id.action_register:
			showRegistrationForm();
			break;
		case R.id.action_about_us:
			showAboutUsActivity();
			break;
		case R.id.action_settings:
			showSettingsActivity();
			break;
		default:
			break;
		}
		return true;
	}

	private void setMenuState(boolean loggedIn)
	{
		Log.i(LOG_TAG, "login status " + loggedIn);
		MenuItem loginMenu = menu.findItem(R.id.action_login);
		MenuItem registerMenu = menu.findItem(R.id.action_register);
		MenuItem logoutMenu = menu.findItem(R.id.action_logout);
		if (loggedIn)
		{
			loginMenu.setVisible(false);
			registerMenu.setVisible(false);
			logoutMenu.setVisible(true);

		}
		else
		{
			loginMenu.setVisible(true);
			registerMenu.setVisible(true);
			logoutMenu.setVisible(false);
		}

	}

	public boolean sendLoginRequest(boolean loginMenuClicked)
	{
		this.loginMenuClicked = loginMenuClicked;

		if (SessionHandler.isSessionIdExists(getApplicationContext()) == false)
		{

			showLoginScreen();
			return true;

		}
		else
		{

			return false;
		}

	}

	private boolean logout()
	{
		try
		{

			SessionHandler sessionHandler = new SessionHandler(getApplicationContext());
			sessionHandler.logout(this);
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
		intent.setClass(this, in.yousee.main.RegistrationActivity.class);
		startActivityForResult(intent, RequestCodes.ACTIVITY_REQUEST_REGISTRATION);
	}

	private void showAboutUsActivity()
	{

		Intent intent = new Intent();
		Log.i("tag", "showing About us Activity");
		intent.setClass(this, in.yousee.main.AboutUs.class);
		startActivity(intent);
	}
	private void showSettingsActivity()
	{
		Intent intent = new Intent();
		Log.i("tag", "showing About us Activity");
		intent.setClass(this, in.yousee.main.SettingsActivity.class);
		startActivity(intent);
	}

	public void reloadActivity()
	{
		sendRequest();
	}

	@Override
	public void onLoginFailed()
	{
		Toast.makeText(getContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
		if (loginMenuClicked)
		{
			showLoginScreen();
		}

	}

	@Override
	public void onLoginSuccess()
	{
		Log.i(LOG_TAG, "in login success");
		SessionHandler.isLoggedIn = true;
		setMenuState(true);
		supportInvalidateOptionsMenu();
		Toast.makeText(getContext(), "logged in successfully.", Toast.LENGTH_SHORT).show();
		reloadActivity();

	}

	public void showLoginScreen()
	{
		Intent intent = new Intent();
		Log.i("tag", "ACTIVITY_REQUEST_LOGIN");
		intent.setClass(this, in.yousee.main.LoginActivity.class);
		startActivityForResult(intent, RequestCodes.ACTIVITY_REQUEST_LOGIN);
	}

	@Override
	public void onResponseRecieved(Object response, int requestCode)
	{
		if (requestCode == RequestCodes.NETWORK_REQUEST_LOGOUT)
		{

			SessionHandler.isLoggedIn = false;
			setMenuState(false);
			supportInvalidateOptionsMenu();
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
