package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class LoginActivity extends SherlockActivity implements OnClickListener, OnFocusChangeListener
{
	EditText usernameEditText;
	EditText passwordEditText;
	Button loginButton;
	Button RegisterButton;
	TextView usernameErrorMsg;
	TextView passwordErrorMsg;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
		instantiate();

	}

	public void instantiate()
	{
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		RegisterButton = (Button) findViewById(R.id.registerButton);
		usernameErrorMsg = (TextView) findViewById(R.id.usernameErrorMessage);
		passwordErrorMsg = (TextView) findViewById(R.id.passwordErrorMessage);

		usernameEditText.setOnFocusChangeListener(this);
		passwordEditText.setOnFocusChangeListener(this);
		loginButton.setOnClickListener(this);

		usernameEditText.setText("gunaranjan");
		passwordEditText.setText("password");

	}

	@Override
	public void onClick(View v)
	{
		if (validateForm())
		{
			Log.i("tag", "Logging in ........");
			SessionHandler session = new SessionHandler(this);
			try
			{
				session.loginExec(usernameEditText.getText().toString(), passwordEditText.getText().toString());
			} catch (CustomException e)
			{
				switch (e.errorCode)
					{
					case CustomException.INVALID_URL:
					case CustomException.NETWORK_NOT_FOUND:
					case CustomException.NO_INTERNET_CONNECTIVITY:
						CustomException.showToastError(getApplicationContext(), e);
						break;
					case CustomException.USERNAME_INVALID:
						showUsernameError(e.getErrorMsg());
						break;
					case CustomException.PASSWORD_INVALID:
						showPasswordError(e.getErrorMsg());

					default:
						break;
					}

			}

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
		} else
		{
			showUsernameError("");
			return true;
		}

	}

	private boolean validatePassword()
	{
		if (passwordEditText.getText().toString().equalsIgnoreCase(""))
		{

			showPasswordError("Please Enter password");
			return false;
		} else
		{
			showPasswordError("");
			return true;
		}
	}

	private void showUsernameError(String errorMsg)
	{
		usernameEditText.setHighlightColor(Color.RED);
		usernameEditText.setHint("Username invalid");
		usernameEditText.setHintTextColor(Color.RED);
		usernameErrorMsg.setText(errorMsg);
		usernameErrorMsg.setVisibility(View.VISIBLE);
	}

	private void showPasswordError(String errorMsg)
	{
		passwordEditText.setHighlightColor(Color.RED);
		passwordEditText.setHint("Password invalid");
		passwordEditText.setHintTextColor(Color.RED);
		passwordErrorMsg.setText(errorMsg);
		passwordErrorMsg.setVisibility(View.VISIBLE);
	}

}
