package nz.co.rivertech.kiwicolour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nz.co.rivertech.kiwicolour.CheckOutActivity.deleteOrder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WhichFandeckActivity extends Activity {
	String strColour; // getintent
	private ListAdapter adapter;
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> FandecksList;
	private static String url_fandeck = "http://rivertech.co.nz/kiwicolour/androidphp/get_fandecks.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ALL_FANDECKS = "Fandecks";
	private static final String TAG_FANDECK_NAME = "FandeckName";
	private static final String TAG_FANDECK_ID = "FandeckID";
	private static final String TAG_COLOUR = "colour";
	JSONArray JAllFandecks = null;
	private ProgressDialog pDialog;
	ListView listviewFandeck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.which_fandeck_activity);
		FandecksList = new ArrayList<HashMap<String, String>>();

		strColour = getIntent().getExtras().getString("COLOUR");
		setTitle("Which fandeck do you mean for color " + strColour);
		listviewFandeck = (ListView) findViewById(R.id.listWhichFandeck);
		new loadAllFandeck().execute();

		listviewFandeck.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tvFD = ((TextView) view.findViewById(R.id.tvFandeck));
				String s = tvFD.getText().toString();
				Intent in = new Intent(WhichFandeckActivity.this,
						ChoosePaintActivity.class);
				in.putExtra("fandeck", s);
				setResult(1, in);
				finish();
			}
		});

	}

	class loadAllFandeck extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(WhichFandeckActivity.this);
			pDialog.setMessage("Loading all fandecks...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			// Building Parameters
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair(TAG_COLOUR, strColour));
			// getting JSON string from URL
			JSONObject json = null;
			try {
				json = jParser.makeHttpRequest(url_fandeck, "GET", params1);
				if (json.length() != 0) {

					// Check your log cat for JSON reponse
					Log.d("All Fandecks: ", json.toString());

					try {
						// Checking for SUCCESS TAG
						int success = json.getInt(TAG_SUCCESS);

						if (success == 1) {
							// fandeck/s found
							// Getting Array of paintshops
							JAllFandecks = json.getJSONArray(TAG_ALL_FANDECKS);

							// looping through All Products
							for (int i = 0; i < JAllFandecks.length(); i++) {
								JSONObject c = JAllFandecks.getJSONObject(i);

								// Storing each json item in variable
								// String FId = c.getString(TAG_FANDECK_ID);
								String FName = c.getString(TAG_FANDECK_NAME);
								String ID = c.getString(TAG_FANDECK_ID);
								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key =>
								// value
								// map.put(TAG_FANDECK_ID, FId);
								map.put(TAG_FANDECK_NAME, FName);
								map.put(TAG_FANDECK_ID, ID);
								// adding HashList to ArrayList
								FandecksList.add(map);
							}
						}
					} catch (JSONException e) {
						Log.e("NETWORK ERROR", "SUCCESS IS 0");
						return null;
					}
				}
			} catch (Exception e1) {
				Log.e("NETWORK ERROR", "JSON IS NULL");
				return null;
			}
			Log.d("NETWORK SUCCESSED", "DONE");
			return "1";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result != null) {
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						// Log.d("All Fandecks: ",
						// String.valueOf(FandecksList.get(0).size()));
						adapter = new SimpleAdapter(
								WhichFandeckActivity.this,
								FandecksList,
								R.layout.list_row_which_fandeck,
								new String[] { TAG_FANDECK_ID, TAG_FANDECK_NAME },
								new int[] { R.id.tvFandeckID, R.id.tvFandeck });
						// updating listview

						listviewFandeck.setAdapter(adapter);

					}
				});
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						showAlertDialog(WhichFandeckActivity.this,
								"Network error...",
								"Something is wrong with network! Try again?",
								false);
					}
				});
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setPositiveButton("Retry",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						new loadAllFandeck().execute();
					}
				});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	};

}// end main class
