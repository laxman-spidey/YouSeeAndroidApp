package in.yousee.yousee;

import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OpportunityListAdapter extends ArrayAdapter<String>
{

	private final Context context;
	private final String[] values;
	private final int[] types;
	// private final ArrayList<Integer> imageIdList;

	HashMap<String, Integer> mIdMap;

	public OpportunityListAdapter(Context context, String[] values, int[] types)
	{
		super(context, R.layout.opportunity_list_item, values);
		this.context = context;
		this.values = values;
		this.types = types;
		// this.imageIdList = imageIdList;
		// Log.i("tag","item "+imageIdList);

		mIdMap = new HashMap<String, Integer>();

		for (int i = 0; i < values.length; ++i)
		{
			mIdMap.put(values[i], i);
		}

	}

	@Override
	public long getItemId(int position)
	{
		String item = getItem(position);
		return mIdMap.get(item);
		// return super.getItemId(position);
	}

	@Override
	public int getCount()
	{
		return values.length;
	}

	static int i = 0;

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.opportunity_list_item, parent, false);

		TextView textView = (TextView) rowView.findViewById(R.id.opportunityTitle);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.opportunityCatagoryIcon);
		textView.setText(values[position]);
		imageView.setBackgroundResource(types[position]);
		
		return rowView;
	}

	@Override
	public String getItem(int position)
	{
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

}
