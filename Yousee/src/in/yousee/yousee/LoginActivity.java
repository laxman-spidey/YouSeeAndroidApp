package in.yousee.yousee;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends SherlockActivity implements OnClickListener, OnFocusChangeListener, Runnable
{
	EditText usernameEditText;
	EditText passwordEditText;
	Button loginButton;
	Button RegisterButton;
	TextView usernameErrorMsg;
	TextView passwordErrorMsg;
	Thread networkThread;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_form);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		RegisterButton = (Button) findViewById(R.id.registerButton);
		usernameErrorMsg = (TextView) findViewById(R.id.usernameErrorMessage);
		passwordErrorMsg = (TextView) findViewById(R.id.passwordErrorMessage);

		usernameEditText.setOnFocusChangeListener(this);
		passwordEditText.setOnFocusChangeListener(this);
		loginButton.setOnClickListener(this);
		
		usernameEditText.setText("fdas");
		passwordEditText.setText("ds");
		
		networkThread = new Thread(this);

	}

	@Override
	public void onClick(View v)
	{
		if (validateForm())
		{
			SessionHandler session= new SessionHandler(this);
			session.loginExec(usernameEditText.getText().toString(), passwordEditText.getText().toString());
			networkThread.start();
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
		usernameErrorMsg.setText(errorMsg);
		usernameErrorMsg.setVisibility(View.VISIBLE);
	}

	private void showPasswordError(String errorMsg)
	{
		passwordErrorMsg.setText(errorMsg);
		passwordErrorMsg.setVisibility(View.VISIBLE);
	}

	@Override
	public void run()
	{
		
		
	}

}
