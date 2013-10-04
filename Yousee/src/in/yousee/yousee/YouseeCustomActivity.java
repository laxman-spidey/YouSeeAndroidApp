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
import android.view.ViewConfiguration;
import android.widget.Toast;
import java.lang.reflect.Field;

public class YouseeCustomActivity extends SherlockFragmentActivity implements UsesLoginFeature, OnResponseRecievedListener
{

	MenuItem loginMenuItem;
	Menu menu;
	boolean loginMenuClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setWindowProgressBar();
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
		getSupportMenuInflater().inflate(R.menu.default_menu, menu);
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		setMenuState(SessionHandler.isSessionIdExists(getApplicationContext()));
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean refresh = false;

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
				promptRetry(e.getErrorMsg());
				e.printStackTrace();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Please wait.. ", Toast.LENGTH_SHORT);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_RETRY)
		{
			if (resultCode == RESULT_OK)
			{
				reloadActivity();

			}
		}
		if (requestCode == RequestCodes.ACTIVITY_REQUEST_LOGIN)
		{
			if (resultCode == RESULT_OK)
			{
				onLoginSuccess();
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
		default:
			break;
		}
		return true;
	}

	private void setMenuState(boolean loggedIn)
	{
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
		intent.setClass(this, in.yousee.yousee.RegistrationActivity.class);
		startActivity(intent);
	}

	private void showAboutUsActivity()
	{

		Intent intent = new Intent();
		intent.setClass(this, in.yousee.yousee.AboutUs.class);
		startActivity(intent);
	}

	public void reloadActivity()
	{
		sendRequest();
	}

	@Override
	public void onLoginFailed()
	{
		if (loginMenuClicked)
		{
			showLoginScreen();
		}

	}

	@Override
	public void onLoginSuccess()
	{
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
		intent.setClass(this, in.yousee.yousee.LoginActivity.class);
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
		return getApplicationContext();
	}

}
