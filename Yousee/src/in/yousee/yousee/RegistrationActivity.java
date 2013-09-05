package in.yousee.yousee;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class RegistrationActivity extends RetryableActivity implements OnFocusChangeListener, OnClickListener
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

	private void instantiateAllFields()
	{
		firstName = (EditText) findViewById(R.id.regFirstName);
		lastName = (EditText) findViewById(R.id.regLastName);
		email = (EditText) findViewById(R.id.regEmail);
		password = (EditText) findViewById(R.id.regPassword);
	

		dob = (EditText) findViewById(R.id.regDob);
		
		Button registerButton = (Button) findViewById(R.id.regSubmit);
		registerButton.setOnClickListener(this);
		dob.setOnFocusChangeListener(this);

	}

	private boolean validateForm()
	{
		boolean success = true;
		if (isEmpty(firstName) &&  isEmpty(dob) && isEmpty(password) && isEmpty(lastName) && validateEmail(email) )
		{
			success = true;
		}

		return success;
	}

	private boolean isEmpty(EditText field)
	{
		String string = field.getText().toString();
		if (string.equals(""))
		{
			showErrorInField(field, "this field can't be empty");
			return true;
		}
		return false;
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
		if ((matcher.matches()))
			return true;
		else
		{
			showErrorInField(emailField, "invalid email format");
			return false;
		}

	}

	private void showErrorInField(EditText field, String errorMsg)
	{
		field.setHint(errorMsg);
		field.setTextSize(12);
		field.setHintTextColor(getResources().getColor(R.color.red));
		
	}

	@Override
	public void onClick(View v)
	{
		if(validateForm())
			submitRegistration();
		else
			Log.i(LOG_TAG, "biscuittttttttttttttt..");
			
		
	}
	
	private void submitRegistration()
	{
		
	}

}
