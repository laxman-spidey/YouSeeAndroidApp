package in.yousee.yousee;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class FilterChildInfo implements OnCheckedChangeListener       
{

	private String sequence = "";
	private String name = "";
	private boolean checked = false;
	CheckBox checkBox;
	TextView textView;

	public String getSequence()
	{
		return sequence;
	}

	public CheckBox getCheckBox()
	{
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox)
	{
		this.checkBox = checkBox;
		this.checkBox.setOnCheckedChangeListener(this);
	}

	public TextView getTextView()
	{
		return textView;
	}

	public void setTextView(TextView textView)
	{
		this.textView = textView;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		setChecked(isChecked);

	}

}