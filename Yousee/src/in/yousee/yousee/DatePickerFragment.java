package in.yousee.yousee;

import java.util.Calendar;

import com.actionbarsherlock.app.SherlockDialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

public class DatePickerFragment extends SherlockDialogFragment implements DatePickerDialog.OnDateSetListener
{
	EditText dob;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		dob = (EditText) getActivity().findViewById(R.id.regDob);
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		dob.setText(""+day+"/"+month+"/"+year);
		Log.i("tag", "date has been set");
	}
}
