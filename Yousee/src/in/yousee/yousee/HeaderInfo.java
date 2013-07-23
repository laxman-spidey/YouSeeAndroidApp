package in.yousee.yousee;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class HeaderInfo 
{

	private String name;
	private ArrayList<DetailInfo> productList = new ArrayList<DetailInfo>();
	
	private boolean checked = false;

	public boolean isChecked()
	{
		return checked;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
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

	 
}