package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends SherlockDialogFragment implements OnClickListener, OnFocusChangeListener
{
	EditText usernameEditText;
	EditText passwordEditText;
	Button loginButton;
	Button RegisterButton;
	TextView usernameErrorMsg;
	TextView passwordErrorMsg;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.login_form, container, false);
		instantiate(view);
		return view;
	}

	
	
	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}


	public void instantiate(View view)
	{
		View layout = getView();
		usernameEditText = (EditText) layout.findViewById(R.id.username);
		passwordEditText = (EditText) layout.findViewById(R.id.password);
		loginButton = (Button) layout.findViewById(R.id.loginButton);
		RegisterButton = (Button) layout.findViewById(R.id.registerButton);
		//usernameErrorMsg = (TextView) layout.findViewById(R.id.usernameErrorMessage);
		//passwordErrorMsg = (TextView) layout.findViewById(R.id.passwordErrorMessage);

		usernameEditText.setOnFocusChangeListener(this);
		passwordEditText.setOnFocusChangeListener(this);
		loginButton.setOnClickListener(this);

		// usernameEditText.setText("gunaranjan");
		// passwordEditText.setText("password");

	}

	@Override
	public void onClick(View v)
	{
		if (validateForm())
		{
			Log.i("tag", "Logging in ........");
			SessionHandler session = new SessionHandler(this.getActivity());
			try
			{
				session.loginExec(usernameEditText.getText().toString(), passwordEditText.getText().toString());
			}
			catch(CustomException e)
			{
				CustomException.showToastError(this.getActivity().getApplicationContext(), e);
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
		//usernameErrorMsg.setText(errorMsg);
		//usernameErrorMsg.setVisibility(View.VISIBLE);
	}

	private void showPasswordError(String errorMsg)
	{
		passwordEditText.setHighlightColor(Color.RED);
		passwordEditText.setHint("Password invalid");
		passwordEditText.setHintTextColor(Color.RED);
		//passwordErrorMsg.setText(errorMsg);
		//passwordErrorMsg.setVisibility(View.VISIBLE);
	}

}
