package nz.co.rivertech.kiwicolour;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter implements Filterable {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private ArrayList<HashMap<String, String>> originalData;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		originalData = d;
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_row_search_shop, null);

		TextView tvPaintShop = (TextView) vi.findViewById(R.id.tvPaintShop);
		TextView tvAddress = (TextView) vi.findViewById(R.id.tvAddress);
		TextView tvId = (TextView) vi.findViewById(R.id.tvId);
		TextView tvRelationShip = (TextView) vi
				.findViewById(R.id.tvRelationStatusSearchShop);
		ImageView list_image = (ImageView) vi.findViewById(R.id.list_image);
		ImageView imgStar = (ImageView) vi.findViewById(R.id.imgStar);

		HashMap<String, String> ShopList_HashMap = new HashMap<String, String>();
		ShopList_HashMap = data.get(position);

		// Setting all values in listview
		tvPaintShop.setText(ShopList_HashMap.get("Name"));
		tvAddress.setText(ShopList_HashMap.get("Address"));
		tvId.setText(ShopList_HashMap.get("SID"));
		
		tvRelationShip.setText(ShopList_HashMap.get("Status"));

		if (tvRelationShip.getText().equals("1")) {
			imgStar.setVisibility(View.VISIBLE);
			imgStar.setBackgroundResource(R.drawable.oneaccepted48);
		} else if(tvRelationShip.getText().equals("0")) {
			imgStar.setVisibility(View.VISIBLE);
			imgStar.setBackgroundResource(R.drawable.zerorequested48);
		}else{
			imgStar.setVisibility(View.INVISIBLE);
		}
		imageLoader.DisplayImage(ShopList_HashMap.get("Picture"), list_image);
		return vi;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence charSequence) {
				FilterResults results = new FilterResults();

				// If there's nothing to filter on, return the original data for
				// your list
				if (charSequence == null || charSequence.length() == 0) {
					results.values = originalData;
					results.count = originalData.size();
				} else {
					ArrayList<HashMap<String, String>> filterResultsData = new ArrayList<HashMap<String, String>>();
					Log.i("originaldata", String.valueOf(originalData.size()));

					for (HashMap<String, String> s : originalData) {
						String ShopName = s.get("Name");
						String Address = s.get("Address");
						if (ShopName.toLowerCase().contains(
								String.valueOf(charSequence).toLowerCase())
								|| Address.toLowerCase().contains(
										String.valueOf(charSequence)
												.toLowerCase())) {
							filterResultsData.add(s);
						}
					}

					results.values = filterResultsData;
					results.count = filterResultsData.size();
				}
				Log.i("result", results.toString());
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence charSequence,
					FilterResults filterResults) {
				data = (ArrayList<HashMap<String, String>>) filterResults.values;

				notifyDataSetChanged();
			}

		};
	}

}