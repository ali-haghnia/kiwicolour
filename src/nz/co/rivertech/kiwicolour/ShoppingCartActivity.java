package nz.co.rivertech.kiwicolour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShoppingCartActivity extends Activity {

	private ProgressDialog pDialog;
	String strGetExtraOrderID;
	private static String url_get_items_list = "http://rivertech.co.nz/kiwicolour/androidphp/get_items_list.php";
	JSONParser jParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ITEMS = "Items";
	private static final String TAG_ITEM_ID = "item_id";
	private static final String TAG_ITEM_DESC = "item_desc";
	private ListAdapter adapter = null;
	JSONArray AllMyOrderItems = null;
	ArrayList<HashMap<String, String>> MyOrderItemList;
	ListView listvieworderitems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shopping_cart_activity);
		strGetExtraOrderID = getIntent().getExtras().getString("OrderId");
		MyOrderItemList = new ArrayList<HashMap<String, String>>();
		setTitle("Shopping cart for order #" + strGetExtraOrderID);
		listvieworderitems = (ListView) findViewById(R.id.listviewShoppingCart);
		new LoadAllMyOrderItems().execute();
	}

	// code e asynctask marboot be load shodane listview
	class LoadAllMyOrderItems extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ShoppingCartActivity.this);
			pDialog.setMessage("Loading your Order items. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		// getting my shops from url
		protected String doInBackground(String... args) {
			// Building Parameters

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("order_id", strGetExtraOrderID));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_get_items_list,
					"GET", params);

			// Check your log cat for JSON reponse
			Log.d("All My order items: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// item/s found
					// Getting Array of itemorders
					AllMyOrderItems = json.getJSONArray(TAG_ITEMS);

					// looping through All Products
					for (int i = 0; i < AllMyOrderItems.length(); i++) {
						JSONObject c = AllMyOrderItems.getJSONObject(i);

						// Storing each json item in variable
						String ItemId = c.getString(TAG_ITEM_ID);
						String ItemName = c.getString(TAG_ITEM_DESC);
						// creating new HashMap

						HashMap<String, String> map = new HashMap<String, String>();
						// adding each child node to HashMap key => value
						map.put(TAG_ITEM_ID, ItemId);
						map.put(TAG_ITEM_DESC, ItemName);

						// adding HashList to ArrayList
						MyOrderItemList.add(map);

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		// After completing background task Dismiss the progress dialog
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Updating parsed JSON data into ListView
					adapter = new SimpleAdapter(getApplicationContext(),
							MyOrderItemList, R.layout.list_row_shopping_cart,
							new String[] { TAG_ITEM_ID, TAG_ITEM_DESC },
							new int[] { R.id.tvorderItemIdcart,
									R.id.tvOrderDescriptioncart });
					// updating listview
					listvieworderitems.setAdapter(adapter);

				}
			});

		}

	}// payan e asynctask

}
