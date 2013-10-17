package in.yousee.yousee;

import in.yousee.yousee.R;
import in.yousee.yousee.model.CustomException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener, OnFocusChangeListener, UsesLoginFeature
{

	private Context context;

	EditText usernameEditText;
	EditText passwordEditText;
	Button loginButton;
	Button RegisterButton;
	TextView usernameErrorMsg;
	TextView passwordErrorMsg;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);

		context = getApplicationContext();

		instantiate();

	}

	public void instantiate()
	{
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		RegisterButton = (Button) findViewById(R.id.registerButton);


		usernameEditText.setOnFocusChangeListener(this);
		passwordEditText.setOnFocusChangeListener(this);
		loginButton.setOnClickListener(this);
		RegisterButton.setOnClickListener(this);


	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.loginButton)
		{
			if (validateForm())
			{
				Log.i("tag", "Logging in ........");
				SessionHandler session = new SessionHandler(getApplicationContext(), this);
				try
				{
					session.loginExec(usernameEditText.getText().toString(), passwordEditText.getText().toString());

				}
				catch (CustomException e)
				{
					switch (e.errorCode)
					{
					case CustomException.ERROR_INVALID_URL:
					case CustomException.ERROR_NETWORK_NOT_FOUND:
					case CustomException.ERROR_NO_INTERNET_CONNECTIVITY:
						CustomException.showToastError(context, e);
						break;
					case CustomException.ERROR_USERNAME_INVALID:
						showUsernameError(e.getErrorMsg());
						break;
					case CustomException.ERROR_PASSWORD_INVALID:
						showPasswordError(e.getErrorMsg());

					default:
						break;
					}

				}

			}
		}
		else if (v.getId() == R.id.registerButton)
		{
			//Toast.makeText(getApplicationContext(), "register", Toast.LENGTH_LONG).show();
			setResult(Activity.RESULT_FIRST_USER, new Intent());
			finish();
			//showRegistrationForm();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if (hasFocus == false)
		{
			switch (v.getId())
			{
			case R.id.username:
				validateUsername();
				break;
			case R.id.password:
				validatePassword();
				break;
			default:
				break;
			}

		}

	}

	private boolean validateForm()
	{
		if (validateUsername() || validatePassword())
		{
			return true;
		}
		return false;
	}

	private boolean validateUsername()
	{
		if (usernameEditText.getText().toString().equalsIgnoreCase(""))
		{
			showUsernameError("Please Enter Username");
			return false;
		}
		else
		{

			return true;
		}

	}

	private boolean validatePassword()
	{
		if (passwordEditText.getText().toString().equalsIgnoreCase(""))
		{

			showPasswordError("Please Enter password");
			return false;
		}
		else
		{

			return true;
		}
	}

	private void showUsernameError(String errorMsg)
	{
		usernameEditText.setError(errorMsg);

	}

	private void showPasswordError(String errorMsg)
	{
		passwordEditText.setError(errorMsg);

	}

	private void addErrorMsg(String errorMsg)
	{

		TextView errorView = (TextView) findViewById(R.id.loginErrorTextView);
		errorView.setText(errorMsg);
		errorView.setVisibility(View.VISIBLE);

	}

	@Override
	public void onLoginFailed()
	{
		addErrorMsg(new CustomException(CustomException.ERROR_LOGIN_ERROR).getErrorMsg());
		CustomException.showToastError(context, new CustomException(CustomException.ERROR_LOGIN_ERROR));
	}

	@Override
	public void onLoginSuccess()
	{
		Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_LONG).show();
		setResult(Activity.RESULT_OK, new Intent().putExtra("result", "success"));
		finish();
	}

	private void showRegistrationForm()
	{

		Intent intent = new Intent();
		Log.i("tag", "showing Registration Activity");
		intent.setClass(this, in.yousee.yousee.RegistrationActivity.class);
	
	}
}
