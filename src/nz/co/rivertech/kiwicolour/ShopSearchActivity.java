package nz.co.rivertech.kiwicolour;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Hossein
 * 
 */
public class ShopSearchActivity extends Activity {
	// private ListAdapter adapter;
	// private ProgressDialog pDialog;
	ProgressBar progressLoadShops;

	Spinner spnCity;
	String CityName = "";
	String ShopId = "";
	String strRelationShipStatus = "";
	String strSelectedShopId = "";

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> paintShopsList;

	// PHP URL
	private static String url_mycity_paintshops = "http://rivertech.co.nz/kiwicolour/androidphp/get_mycity_shops.php";
	private static String url_add_favorite ="http://rivertech.co.nz/kiwicolour/androidphp/add_fav_shop_request.php";
	private static String url_remove_favorite = "http://rivertech.co.nz/kiwicolour/androidphp/remove_favorite_shop.php";

	// JSON Node
	private static final String TAG_SUCCESS = "success";

	private static final String TAG_ALLPAINTSHOPS = "MyCityShops";
	private static final String TAG_ID = "SID";
	private static final String TAG_PAINTSHOP_NAME = "Name";
	private static final String TAG_ADDRESS = "Address";
	private static final String TAG_PICTURE = "Picture";
	private static final String TAG_STATUS = "Status"; // relationship between
														// painter and paintshop

	// Shops JSONArray
	JSONArray AllPainshops = null;
	ListView lv;
	LazyAdapter adapter;
	EditText etSearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_search_activity);
		progressLoadShops = (ProgressBar) findViewById(R.id.progressBarLoadsearchShop);

		setTitle("Shop search");

		Global.globalVariable = "SEARCH";

		paintShopsList = new ArrayList<HashMap<String, String>>();
		// EZAF SHODAN E SPINNER BARAYE NAMAYESHE SHAHRHA======================
		spnCity = (Spinner) findViewById(R.id.spnCity);

		String[] items = new String[] { "Hamilton", "Auckland" };
		ArrayAdapter<String> spinnerMenu = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		spinnerMenu
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnCity.setAdapter(spinnerMenu);

		spnCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				Boolean internetIsPresent = null;
				try {
					ConnectionDetector cd = new ConnectionDetector(
							getApplicationContext());
					internetIsPresent = cd.isConnectingToInternet();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (internetIsPresent) {
					CityName = parent.getItemAtPosition(position).toString();
					new LoadAllpaintshops().execute();
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
		// ================================================================

		// Get listview
		// ListView lv = getListView();
		lv = (ListView) findViewById(R.id.list);

		// on seleting single paintshop************************************
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean internetIsPresent = null;
				try {
					ConnectionDetector cd = new ConnectionDetector(
							getApplicationContext());
					internetIsPresent = cd.isConnectingToInternet();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (internetIsPresent) {
					TextView tvShopName = ((TextView) view
							.findViewById(R.id.tvPaintShop));
					String strShopName = tvShopName.getText().toString();
					TextView tvShopID = ((TextView) view
							.findViewById(R.id.tvId));
					Integer intShopID = Integer.valueOf(tvShopID.getText()
							.toString());
					// Starting new intent
					Intent in = new Intent(getApplicationContext(),
							ChoosePaintActivity.class);
					// Sending ShopID and ShopName to next activity
					in.putExtra("ShopID", intShopID);
					in.putExtra("ShopName", strShopName);
					in.putExtra("ACTIVITY", "SHOPSEARCH");
					startActivity(in);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_up_out);
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// text change listener for
		// search***************************************
		EditText etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				(ShopSearchActivity.this.adapter).getFilter().filter(s);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		registerForContextMenu(lv);
		lv.setLongClickable(true);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tvRelationShip = ((TextView) view
						.findViewById(R.id.tvRelationStatusSearchShop));
				strRelationShipStatus = tvRelationShip.getText().toString();

				TextView tvId = ((TextView) view.findViewById(R.id.tvId));
				strSelectedShopId = tvId.getText().toString();

				return false;
			}
		});
		
		lv.setEnabled(false);
		etSearch.setEnabled(false);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		if (strRelationShipStatus.equals("1")) {
			inflater.inflate(R.menu.mnu_remove_from_favorite, menu);
		} else if (strRelationShipStatus.equals("3")) {
			inflater.inflate(R.menu.mnu_add_to_favorite, menu);
		} else if (strRelationShipStatus.equals("null")) {
			inflater.inflate(R.menu.mnu_add_to_favorite, menu);
		} else if(strRelationShipStatus.equals("0")) {
			inflater.inflate(R.menu.mnu_cancel_request, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (strRelationShipStatus.equals("1")) {
			new RemoveFromFavorite().execute();
		} else if (strRelationShipStatus.equals("3")) {
			new AddToFavorite().execute();
		} else if (strRelationShipStatus.equals("null")) {
			new AddToFavorite().execute();
		} else if(strRelationShipStatus.equals("0")) {
			new RemoveFromFavorite().execute();
		}
		return false;
	}

	class LoadAllpaintshops extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressLoadShops.setVisibility(View.VISIBLE);
			paintShopsList.clear();
		}

		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("city_name", CityName));
			params.add(new BasicNameValuePair("painter_id", Global.PainterID));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_mycity_paintshops,
					"GET", params);

			// Check your log cat for JSON reponse
			Log.d("All paintshops: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// painshop/s found
					// Getting Array of paintshops
					AllPainshops = json.getJSONArray(TAG_ALLPAINTSHOPS);

					// looping through All Products
					for (int i = 0; i < AllPainshops.length(); i++) {
						JSONObject c = AllPainshops.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
						// Storing each json item in variable
						String ID = c.getString(TAG_ID);
						ShopId = ID;
						String Name = c.getString(TAG_PAINTSHOP_NAME);
						String Address = c.getString(TAG_ADDRESS);
						String PicturePath = c.getString(TAG_PICTURE);
						String CurrectPathPic = "";
						if (!(PicturePath.equalsIgnoreCase("null"))) {
							String[] parts = PicturePath.split("/");
							CurrectPathPic = parts[0] + "/" + parts[1] + "/"
									+ parts[2]; // !!!!
						}
						String Status = c.getString(TAG_STATUS);
						PicturePath = "http://rivertech.co.nz/kiwicolour/public/"
								+ CurrectPathPic;

						map.put(TAG_PICTURE, PicturePath);
						map.put(TAG_ID, ID);
						map.put(TAG_PAINTSHOP_NAME, Name);
						map.put(TAG_ADDRESS, Address);
						map.put(TAG_STATUS, Status);
						// adding HashList to ArrayList
						paintShopsList.add(map);
					}
				}
			} catch (JSONException e) {
				Log.d("ERROR IN MAP", "ERROR");
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			progressLoadShops.setVisibility(View.INVISIBLE);
			runOnUiThread(new Runnable() {
				public void run() {
					adapter = new LazyAdapter(ShopSearchActivity.this,
							paintShopsList);
					lv.setAdapter(adapter);

					final EditText etSearch = (EditText) findViewById(R.id.etSearch);
					etSearch.setText("");
					lv.setEnabled(true);
					etSearch.setEnabled(true);
				}
			});

		}

	}

	/*
	 * class DownloadFileFromURL extends AsyncTask<String, String, String> {
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute(); }
	 * 
	 * @Override protected String doInBackground(String... f_url) { int count;
	 * try { URL url = new URL(f_url[0]); URLConnection conection =
	 * url.openConnection(); conection.connect(); // getting file length int
	 * lenghtOfFile = conection.getContentLength();
	 * 
	 * // input stream to read file - with 8k buffer InputStream input = new
	 * BufferedInputStream(url.openStream(), 8192);
	 * 
	 * String PATH = Environment.getExternalStorageDirectory() +
	 * "/KiwiColour/Cache/"; // File file = new File(PATH); // file.mkdirs(); //
	 * Output stream to write file OutputStream output = new
	 * FileOutputStream(PATH+f_url[1]);
	 * 
	 * byte data[] = new byte[1024];
	 * 
	 * long total = 0;
	 * 
	 * while ((count = input.read(data)) != -1) { total += count; // publishing
	 * the progress.... // After this onProgressUpdate will be called
	 * publishProgress(""+(int)((total*100)/lenghtOfFile));
	 * 
	 * // writing data to file output.write(data, 0, count); }
	 * 
	 * // flushing output output.flush();
	 * 
	 * // closing streams output.close(); input.close();
	 * 
	 * } catch (Exception e) { Log.e("Error in Download file ", "Error"); }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(String file_url) {
	 * 
	 * }
	 * 
	 * }
	 */

	class AddToFavorite extends AsyncTask<String, String, String> {
		int success = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressLoadShops.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("painter_id", Global.PainterID));
			params2.add(new BasicNameValuePair("shop_id", strSelectedShopId));
			JSONObject json2 = null;
			try {
				json2 = jParser.makeHttpRequest(url_add_favorite, "GET",
						params2);
				success = json2.getInt(TAG_SUCCESS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (success == 1 && json2 != null) {
				runOnUiThread(new Runnable() {
					public void run() {
						new LoadAllpaintshops().execute();
					}
				});
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(ShopSearchActivity.this, "Request sent to paint shop.", Toast.LENGTH_SHORT).show();
		}

	}// end of addtofavorite

	class RemoveFromFavorite extends AsyncTask<String, String, String> {
		int success = 0;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressLoadShops.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair("painter_id", Global.PainterID));
			params1.add(new BasicNameValuePair("shop_id", strSelectedShopId));
			JSONObject json1 = null;
			try {
				json1 = jParser.makeHttpRequest(url_remove_favorite, "GET",
						params1);
				success = json1.getInt(TAG_SUCCESS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (success == 1 && json1 != null) {
				runOnUiThread(new Runnable() {
					public void run() {
						new LoadAllpaintshops().execute();
					}
				});
			}
			return null;
		}

	}// end of removefromfavorite

	@Override
	protected void onStop() {
		super.onStop();
		Global.globalVariable = "SEARCH";
	}
}
