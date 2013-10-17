package in.yousee.yousee;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilterChildInfo implements OnClickListener
{

	private String sequence = "";
	private String name = "";
	private boolean checked = false;
	CheckBox checkBox;
	LinearLayout layout;
	public LinearLayout getLayout()
	{
		return layout;
	}

	public void setLayout(LinearLayout layout)
	{
		this.layout = layout;
		this.layout.setOnClickListener(this);
	}

	//CheckedTextView checkBox;
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
		this.checkBox.setOnClickListener(this);
	}
	/*
	public CheckedTextView getCheckBox()
	{
		return checkBox;
	}

	public void setCheckBox(CheckedTextView checkBox)
	{
		this.checkBox = checkBox;
		this.checkBox.setOnClickListener(this);
	}
	*/

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
	public void onClick(View v)
	{
		
		setChecked(!isChecked());
		checkBox.setChecked(isChecked());
		
		//Log.i("tag", "child Item : " + textView.getText() + ", checked state :" + isChecked());

	}

}