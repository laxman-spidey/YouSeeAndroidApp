package in.yousee.yousee;

import in.yousee.yousee.R;
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
import com.actionbarsherlock.view.Window;

public class RegistrationActivity extends YouseeCustomActivity implements OnFocusChangeListener, OnClickListener, UsesLoginFeature, OnResponseRecievedListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		getWindow().setTitle("Registration");
		getWindow().setTitleColor(getResources().getColor(R.color.red));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_form);
		instantiateAllFields();


	}

	
	@Override
	protected void setWindowProgressBar()
	{
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//setSupportProgressBarIndeterminateVisibility(false);
	}


	public void showDatePickerDialog(View v)
	{
		SherlockDialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return false;
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
	EditText city;
	EditText phNo;
	EditText dob;
	TextView errorField;

	private void instantiateAllFields()
	{
		firstName = (EditText) findViewById(R.id.regFirstName);
		lastName = (EditText) findViewById(R.id.regLastName);
		email = (EditText) findViewById(R.id.regEmail);
		password = (EditText) findViewById(R.id.regPassword);
		city = (EditText) findViewById(R.id.regCity);
		phNo = (EditText) findViewById(R.id.regPhno);

		dob = (EditText) findViewById(R.id.regDob);

		errorField = (TextView) findViewById(R.id.regErrorTextView);
		Button registerButton = (Button) findViewById(R.id.regSubmit);
		registerButton.setOnClickListener(this);
		dob.setOnFocusChangeListener(this);
		
		//test
		// firstName.setText("fsdaf");
		// lastName.setText("fsdaf");
		// email.setText("fsdaf@kh.com");
		// dob.setText("fsdaf");
		// password.setText("fsdaf");
		//city.setText("fsdaf");
		//phNo.setText("3456789");
		//test

	}

	private boolean validateForm()
	{

		Log.i(LOG_TAG, "first : " + firstName.getText());
		Log.i(LOG_TAG, "last : " + lastName.getText());
		Log.i(LOG_TAG, "email : " + email.getText());
		Log.i(LOG_TAG, "dob : " + dob.getText());
		Log.i(LOG_TAG, "password : " + password.getText());
		
		boolean validity = true;
		if (isEmpty(firstName))
		{
			Log.i(LOG_TAG, "firstname empty");
			validity = false;
		}
		if (isEmpty(lastName))
		{
			Log.i(LOG_TAG, "lastname empty");
			validity = false;
		}
		if (isEmpty(password))
		{
			Log.i(LOG_TAG, "password empty");
			validity = false;
		}
		if (isEmpty(city))
		{
			Log.i(LOG_TAG, "city empty");
			validity = false;
		}
		if (isEmpty(phNo))
		{
			Log.i(LOG_TAG, "phno empty");
			validity = false;
		}
		if (isEmpty(dob))
		{
			Log.i(LOG_TAG, "dob empty");
			validity = false;
		}
		if (!validateEmail(email))
		{
			Log.i(LOG_TAG, "email empty");
			validity = false;
		}
		Log.i(LOG_TAG,"validity: "+validity);
		return validity;
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
				Log.i(LOG_TAG, "email id matched ");
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
			responseCode = obj.getInt(Middleware.TAG_NETWORK_RESULT_CODE);
			if (responseCode == CustomException.SUCCESS_CODE)
			{
				SessionHandler sessionHandler = new SessionHandler(getApplicationContext(), this);
				Log.i(LOG_TAG, "Sending response to session handler");
				sessionHandler.serveResponse((String) response, RequestCodes.NETWORK_REQUEST_LOGIN);
			}
			else if (responseCode == CustomException.REGISTRATION_EMAIL_ALREADY_TAKEN)
			{
				showErrorInField(email, "Email, you have entered is already taken, Try with another Email");
			}
			else
			{
				Log.i(LOG_TAG, "response code not matched");
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
		field.setError(errorMsg);
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
		{
			setSupportProgressBarIndeterminateVisibility(true);
			submitRegistration();
		}
		else
			Log.i(LOG_TAG, "biscuittttttttttttttt..");

	}

	private void submitRegistration()
	{
		Log.i(LOG_TAG, "submitting");
		RegistrationFormObject regFormObject = new RegistrationFormObject();
		regFormObject.setFirstName(firstName.getText().toString());
		regFormObject.setLastname(lastName.getText().toString());
		regFormObject.setEmail(email.getText().toString());
		regFormObject.setPassword(password.getText().toString());
		regFormObject.setDob(dob.getText().toString());
		regFormObject.setPhNo(phNo.getText().toString());
		regFormObject.setCity(city.getText().toString());
		RegistrationProcessor registrationProcessor = new RegistrationProcessor(this, regFormObject);
		requestSenderMiddleware = registrationProcessor;
		sendRequest();
	}

	@Override
	public void onLoginFailed()
	{
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onLoginSuccess()
	{
		setSupportProgressBarIndeterminateVisibility(false);
		//Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_LONG).show();
		setResult(Activity.RESULT_OK, new Intent().putExtra("result", "success"));
		finish();
	}
}
