package in.yousee.yousee;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class HeaderInfo implements OnCheckedChangeListener
{

	private String name;
	private ArrayList<DetailInfo> productList = new ArrayList<DetailInfo>();
	private CheckBox checkBox;
	private TextView textView;
	private boolean checked = false;

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public CheckBox getCheckBox()
	{
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox)
	{
		this.checkBox = checkBox;
		checkBox.setOnCheckedChangeListener(this);
	}

	public TextView getTextView()
	{
		return textView;
	}

	public void setTextView(TextView textView)
	{
		this.textView = textView;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ArrayList<DetailInfo> getProductList()
	{
		return productList;
	}

	public void setProductList(ArrayList<DetailInfo> productList)
	{
		this.productList = productList;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{

		Log.d("tag", "group checked");

		Iterator<DetailInfo> it = productList.listIterator();
		while (it.hasNext())
		{
			DetailInfo child = it.next();
			child.setChecked(isChecked);
			CheckBox checkbox = child.getCheckBox();
			if(checkbox!=null)
			{
				checkbox.setChecked(isChecked);
			}
		}

		checked = isChecked;

	}

}