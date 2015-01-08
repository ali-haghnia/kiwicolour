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

public class LazyAdaptor2 extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdaptor2(Activity a, ArrayList<HashMap<String, String>> d) {
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
			vi = inflater.inflate(R.layout.list_row_my_shops, null);

		TextView tvPaintShop = (TextView) vi
				.findViewById(R.id.tvPaintShopMyShop);
		TextView tvAddress = (TextView) vi.findViewById(R.id.tvAddress);
		TextView tvId = (TextView) vi.findViewById(R.id.tvIdMyShop);
		ImageView list_image = (ImageView) vi.findViewById(R.id.list_image);
		ImageView imgStatus = (ImageView) vi.findViewById(R.id.imgStarRequest);
		TextView tvStatusShopHome = (TextView) vi.findViewById(R.id.tvStatusShopHome);
		HashMap<String, String> ShopList_HashMap = new HashMap<String, String>();
		ShopList_HashMap = data.get(position);

		// Setting all values in listview
		tvPaintShop.setText(ShopList_HashMap.get("StoreName"));
		tvAddress.setText(ShopList_HashMap.get("StoreAddress"));
		tvId.setText(ShopList_HashMap.get("StoreID"));
		tvStatusShopHome.setText(ShopList_HashMap.get("Sts"));
		imageLoader.DisplayImage(ShopList_HashMap.get("StorePicture"),list_image);
		if(tvStatusShopHome.getText().equals("0")){
			imgStatus.setVisibility(View.VISIBLE);
			imgStatus.setBackgroundResource(R.drawable.zerorequested48);
		}else if(tvStatusShopHome.getText().equals("1")){
			imgStatus.setVisibility(View.INVISIBLE);
		}
		return vi;
	}
}