package in.yousee.yousee;

import java.util.ArrayList;

public class FilterGroupInfo 
{

	private String name;
	private ArrayList<FilterChildInfo> productList = new ArrayList<FilterChildInfo>();
	
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

	public ArrayList<FilterChildInfo> getProductList()
	{
		return productList;
	}

	public void setProductList(ArrayList<FilterChildInfo> productList)
	{
		this.productList = productList;
	}

	 
}