package in.yousee.yousee;

import in.yousee.yousee.constants.RequestCodes;
import in.yousee.yousee.model.CustomException;
import in.yousee.yousee.model.RegistrationFormObject;

import java.net.ResponseCache;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;

public class RegistrationActivity extends YouseeCustomActivity implements OnFocusChangeListener, OnClickListener, UsesLoginFeature, OnResponseRecievedListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setSupportProgressBarIndeterminateVisibility(false);
		getWindow().setTitle("Registration");
		getWindow().setTitleColor(getResources().getColor(R.color.red));
		setContentView(R.layout.registration_form);
		instantiateAllFields();

	}

	public void showDatePickerDialog(View v)
	{
		SherlockDialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");

	}

	@Override
	public void onFocusChange(View v, boolean isFocused)
	{
		if (isFocused == true)
		{
			Log.i("tag", "focus gained");
			showDatePickerDialog(v);
		}
		else
		{
			Log.i("tag", "focus lost");
		}

	}

	EditText firstName;
	EditText lastName;
	EditText email;
	EditText password;
	EditText dob;
	TextView errorField;

	private void instantiateAllFields()
	{
		firstName = (EditText) findViewById(R.id.regFirstName);
		firstName.setText("fsadhgfsd");
		lastName = (EditText) findViewById(R.id.regLastName);
		lastName.setText("fsadhgfsd");
		email = (EditText) findViewById(R.id.regEmail);
		email.setText("mittu.thefire@gmail.com");
		password = (EditText) findViewById(R.id.regPassword);
		password.setText("fsadhgfsd");

		dob = (EditText) findViewById(R.id.regDob);

		errorField = (TextView) findViewById(R.id.regErrorTextView);
		Button registerButton = (Button) findViewById(R.id.regSubmit);
		registerButton.setOnClickListener(this);
		dob.setOnFocusChangeListener(this);

	}

	private boolean validateForm()
	{

		Log.i(LOG_TAG, "first : " + firstName.getId());
		Log.i(LOG_TAG, "last : " + lastName.getId());
		Log.i(LOG_TAG, "email : " + email.getId());
		Log.i(LOG_TAG, "dob : " + dob.getId());
		Log.i(LOG_TAG, "password : " + password.getId());
		if (!isEmpty(firstName) && !isEmpty(dob) && !isEmpty(password) && !isEmpty(lastName) && validateEmail(email))
		{
			return true;
		}

		return false;
	}

	private boolean isEmpty(EditText field)
	{
		String string = field.getText().toString();
		if (string.equals(""))
		{
			showErrorInField(field, "this field can't be empty");
			return true;
		}
		else
		{
			Log.i(LOG_TAG, "non empty id: " + field.getId());
			return false;
		}
	}

	// CustomException.showToastError(context, new
	// CustomException(CustomException.LOGIN_ERROR));

	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *                hex for validation
	 * @return true valid hex, false invalid hex
	 */
	private boolean validateEmail(EditText emailField)
	{

		Pattern pattern;
		Matcher matcher;

		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		String emailString = emailField.getText().toString();
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(emailString);
		if (!isEmpty(emailField))
		{
			if ((matcher.matches()))
			{
				Log.i(LOG_TAG, "matched email id ");
				return true;
			}
			else
			{
				Log.i(LOG_TAG, "not matched email id ");

				showErrorInField(emailField, "invalid email format");
				return false;
			}
		}
		else
		{
			Log.i(LOG_TAG, "email id empty");
			return false;
		}

	}

	public void onResponseRecieved(Object response, int requestCode)
	{
		int responseCode;
		Log.i(LOG_TAG, (String) response);
		try
		{
			JSONObject obj = new JSONObject((String) response);
			responseCode = obj.getInt(Chef.TAG_NETWORK_RESULT_CODE);
			if (responseCode == CustomException.SUCCESS_CODE)
			{
				SessionHandler sessionHandler = new SessionHandler(getApplicationContext(), this);
				sessionHandler.serveResponse((String) response, RequestCodes.NETWORK_REQUEST_LOGIN);
			}
			else if(responseCode == CustomException.REGISTRATION_EMAIL_ALREADY_TAKEN)
			{
				Toast.makeText(getApplicationContext(), "Email, you have entered is already taken, Try with another Email", Toast.LENGTH_LONG).show();
				//errorField.setText("Email, you have entered is already taken, Try with another Email");
				//errorField.setVisibility(View.VISIBLE);
			}
			else if(responseCode == CustomException.REGISTRATION_EMAIL_ALREADY_TAKEN)
			{
				Toast.makeText(getApplicationContext(), "Something went wrong, Please try submitting again.", Toast.LENGTH_LONG).show();
			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Context getContext()
	{
		return getApplicationContext();

	}

	private void showErrorInField(EditText field, String errorMsg)
	{
		field.setText("");
		field.setHint(errorMsg);

		field.setHintTextColor(Color.RED);
		// CustomException.showToastError(context, new
		// CustomException(CustomException.LOGIN_ERROR));sources().getColor(R.color.red));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public void onClick(View v)
	{
		if (validateForm())
			submitRegistration();
		else
			Log.i(LOG_TAG, "biscuittttttttttttttt..");

	}

	private void submitRegistration()
	{
		RegistrationFormObject regFormObject = new RegistrationFormObject();
		regFormObject.setFirstName(firstName.getText().toString());
		regFormObject.setLastname(lastName.getText().toString());
		regFormObject.setEmail(email.getText().toString());
		regFormObject.setPassword(password.getText().toString());
		regFormObject.setDob(dob.getText().toString());
		RegistrationProcessor registrationProcessor = new RegistrationProcessor(this, regFormObject);
		requestSenderChef = registrationProcessor;
		sendRequest();
	}

	@Override
	public void onLoginFailed()
	{
		
	}

	@Override
	public void onLoginSuccess()
	{
		Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_LONG).show();
		setResult(Activity.RESULT_OK, new Intent().putExtra("result", "success"));
		finish();
	}
}
