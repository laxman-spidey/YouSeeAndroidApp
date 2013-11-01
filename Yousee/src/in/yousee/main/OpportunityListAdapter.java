package in.yousee.main;

import in.yousee.main.model.ProxyOpportunityItem;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OpportunityListAdapter extends ArrayAdapter<ProxyOpportunityItem>
{

	private final Context context;
	
	private ArrayList<ProxyOpportunityItem> proxyItemList;
	// private final ArrayList<Integer> imageIdList;

	HashMap<String, Integer> mIdMap;

	public OpportunityListAdapter(Context context, ArrayList<ProxyOpportunityItem> proxyItemList)
	{
		super(context, R.layout.opportunity_list_item, proxyItemList);
		this.context = context;
		this.proxyItemList = proxyItemList;
	}

	@Override
	public long getItemId(int position)
	{
		return proxyItemList.get(position).getId();
		
	}

	@Override
	public int getCount()
	{
		return proxyItemList.size();
	}

	static int i = 0;

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.opportunity_list_item, parent, false);

		TextView textView = (TextView) rowView.findViewById(R.id.opportunityTitle);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.opportunityCatagoryIcon);
		textView.setText(proxyItemList.get(position).getTitle());
		imageView.setBackgroundResource(proxyItemList.get(position).getResourceOfCatagoryType());
		
		return rowView;
	}

	@Override
	public ProxyOpportunityItem getItem(int position)
	{
		return proxyItemList.get(position);
	}

}
