package nz.co.rivertech.kiwicolour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class OrderArchiveActivity extends Activity implements OnClickListener {
	Button btnPrevious, btnNext;
	ListView listviewOrderArchive;
	boolean ListIsEmpty = false;
	private ListAdapter adapter;
	private ProgressDialog pDialog;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONArray AllMyOrders = null;
	ArrayList<HashMap<String, Object>> MyOrdersList;
	// url to get my paint shops and my orders
	private static String url_all_myorders = "http://rivertech.co.nz/kiwicolour/androidphp/get_myorders.php";
	private static final String TAG_NEWCOMMENT = "NewComment";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MYORDERS = "MyOrders";
	private static final String TAG_ORDERID = "OrderID";
	private static final String TAG_PICTURE = "StorePicture";
	private static final String TAG_STORENAME = "StoreName";
	private static final String TAG_STATE = "State";
	private static final String TAG_ITEM = "Item";
	private static final String TAG_DELIVERYADDRESS = "DeliveryAddress";
	private static final String TAG_READYDATETIME = "ReadyDateTime";

	String PainterID = Global.PainterID, strStart = "0", strRows = "6";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_archive_activity);
		setTitle("Order archive");

		MyOrdersList = new ArrayList<HashMap<String, Object>>();
		listviewOrderArchive = (ListView) findViewById(R.id.listviewOrderArchive);
		btnPrevious = (Button) findViewById(R.id.btnPrevious);
		btnNext = (Button) findViewById(R.id.btnNext);

		btnPrevious.setOnClickListener(this);
		btnNext.setOnClickListener(this);

		new LoadAllMyOrders().execute();

		listviewOrderArchive.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean internetIsPresent = null;
				try {
					ConnectionDetector cd = new ConnectionDetector(
							getApplicationContext());
					internetIsPresent = cd.isConnectingToInternet();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (internetIsPresent) {
					TextView tempState = ((TextView) view
							.findViewById(R.id.tvStateOrdrArchive));
					String strState = tempState.getText().toString();

					if (!strState.equals("Draft")) {
						Global.globalVariable = "ARCHIVE";
						Intent in = new Intent(getApplicationContext(),
								CheckOutActivity.class);
						TextView tempId = ((TextView) view
								.findViewById(R.id.tvOrderIDOrdrArchive));
						String strOrderId = tempId.getText().toString();

						TextView tempName = ((TextView) view
								.findViewById(R.id.tvStoreNameOrdrArchive));
						String strShopName = tempName.getText().toString();

						TextView tempAddress = ((TextView) view
								.findViewById(R.id.tvDeliveryAddressOrdrArchive));
						String strAddress = tempAddress.getText().toString();

						TextView tempDateTime = ((TextView) view
								.findViewById(R.id.tvReadyDateTimeOrdrArchive));
						String strDateTime = tempDateTime.getText().toString();

						in.putExtra("OrderID", strOrderId);
						in.putExtra("ShopName", strShopName);
						in.putExtra("Address", strAddress);
						in.putExtra("DateTime", strDateTime);
						in.putExtra("Status", strState);
						startActivity(in);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_up_out);

					} else {
						Global.globalVariable = "ARCHIVE";
						Intent in = new Intent(getApplicationContext(),
								CheckOutActivity.class);
						TextView tempId = ((TextView) view
								.findViewById(R.id.tvOrderIDOrdrArchive));
						String strOrderId = tempId.getText().toString();

						TextView tempName = ((TextView) view
								.findViewById(R.id.tvStoreNameOrdrArchive));
						String strShopName = tempName.getText().toString();

						in.putExtra("OrderID", strOrderId);
						in.putExtra("ShopName", strShopName);
						in.putExtra("Status", strState);
						startActivity(in);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_up_out);

					}
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}
			}// end of onitemclick
		});
	}

	@Override
	public void onClick(View v) {
		Boolean internetIsPresent = null;
		try {
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());
			internetIsPresent = cd.isConnectingToInternet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (v.getId()) {
		case R.id.btnNext:
			if (internetIsPresent) {
				strStart = String.valueOf(Integer.valueOf(strStart) + 5);
				new LoadAllMyOrders().execute();
			} else {
				Toast.makeText(getApplicationContext(), "Connection Lost!",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btnPrevious:
			if (internetIsPresent) {
				strStart = String.valueOf(Integer.valueOf(strStart) - 5);
				new LoadAllMyOrders().execute();
			} else {
				Toast.makeText(getApplicationContext(), "Connection Lost!",
						Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	//
	// /**
	// * Background Async Task to Load all product by making HTTP Request
	// * */
	class LoadAllMyOrders extends AsyncTask<String, String, String> {
		Integer listRowNum = 0;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(OrderArchiveActivity.this);
			pDialog.setMessage("Loading your orders.Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * /** getting my orders from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("painter_id", PainterID));
			Log.d("start = " + strStart, "rows = " + strRows);
			params.add(new BasicNameValuePair("start", strStart));
			params.add(new BasicNameValuePair("rows", strRows));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_myorders, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("All My orders: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					ListIsEmpty = false;
					MyOrdersList.clear();
					// painshop/s found
					// Getting Array of paintshops
					AllMyOrders = json.getJSONArray(TAG_MYORDERS);

					// looping through All Orders
					listRowNum = ((AllMyOrders.length() > 5) ? AllMyOrders
							.length() - 1 : AllMyOrders.length());
					for (int i = 0; i < listRowNum; i++) {
						// Log.d("i = " + i, "i < " +
						// String.valueOf(AllMyOrders.length() -1));
						JSONObject c = AllMyOrders.getJSONObject(i);
						// Storing each json2 item in variable
						String Picture = c.getString(TAG_PICTURE);
						String StoreName = c.getString(TAG_STORENAME); // ****************
						String strNewComment = c.getString(TAG_NEWCOMMENT);

						if (strNewComment.equals("0")) {
							strNewComment = "";
						} else {
							strNewComment = " " + strNewComment + " ";
						}
//						String[] parts = StoreName.split(" ");
//						if (parts[0].length() + parts[1].length() + 4 <= 20) {
//							StoreName = parts[0] + " " + parts[1] + "...";
//						} else {
//							StoreName = parts[0] + "...";
//						}

						String State = c.getString(TAG_STATE);
						String DeliveryAddress = c
								.getString(TAG_DELIVERYADDRESS);
						if (DeliveryAddress.length() > 35) {
							DeliveryAddress = (String) DeliveryAddress
									.subSequence(0, 32) + "...";
						}
						String ReadyDateTime = c.getString(TAG_READYDATETIME);

						// ===============================================CHANGE
						// DATE FORMAT=================
						Date date = null;
						try {
							date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.parse(ReadyDateTime);
							Log.e("ReadyDateTime", ReadyDateTime);
							ReadyDateTime = new SimpleDateFormat(
									"E yyyy-MM-dd HH:mm").format(date);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						// ===============================END OF CHANGE DATE
						// FORMAT===========================
						String Item = c.getString(TAG_ITEM);
						String orderID = c.getString(TAG_ORDERID);

						// creating new HashMap
						HashMap<String, Object> map = new HashMap<String, Object>();

						// adding each child node to HashMap key => value
						if (State.equalsIgnoreCase("Draft")) {
							map.put(TAG_PICTURE, R.drawable.state1);
						} else if (State.equalsIgnoreCase("Sent")) {
							map.put(TAG_PICTURE, R.drawable.state2);
						} else if (State.equalsIgnoreCase("Received")) {
							map.put(TAG_PICTURE, R.drawable.state3);
						} else if (State.equalsIgnoreCase("Accepted")) {
							map.put(TAG_PICTURE, R.drawable.state4);
						} else if (State.equalsIgnoreCase("Canceled")) {
							map.put(TAG_PICTURE, R.drawable.state5);
						} else if (State.equalsIgnoreCase("In Process")) {
							map.put(TAG_PICTURE, R.drawable.state6);
						} else if (State.equalsIgnoreCase("Ready")) {
							map.put(TAG_PICTURE, R.drawable.state7);
						} else if (State.equalsIgnoreCase("Shipped")) {
							map.put(TAG_PICTURE, R.drawable.state8);
						}
						map.put(TAG_STORENAME, StoreName);
						map.put(TAG_STATE, State);
						map.put(TAG_DELIVERYADDRESS, DeliveryAddress);
						map.put(TAG_READYDATETIME, ReadyDateTime);
						map.put(TAG_ITEM, Item);
						map.put(TAG_ORDERID, orderID);
						map.put(TAG_NEWCOMMENT, strNewComment);

						// adding HashList to ArrayList
						MyOrdersList.add(map);
					}
				} else {
					ListIsEmpty = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					Integer intFrom = Integer.valueOf(strStart) + 1;
					Integer intTo = Integer.valueOf(strStart) + listRowNum;
					setTitle("Order archive " + String.valueOf(intFrom) + "-"
							+ String.valueOf(intTo));

					try {
						if (AllMyOrders.length() < 6)
							btnNext.setEnabled(false);
						else
							btnNext.setEnabled(true);

						if (intFrom == 1)
							btnPrevious.setEnabled(false);
						else
							btnPrevious.setEnabled(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Log.d("All My orders: ",String.valueOf(MyOrdersList.get(0).size()));
					if (!ListIsEmpty) {

						adapter = new SimpleAdapter(OrderArchiveActivity.this,
								MyOrdersList, R.layout.list_row_order_archive,
								new String[] { TAG_PICTURE, TAG_STORENAME,
										TAG_STATE, TAG_DELIVERYADDRESS,
										TAG_READYDATETIME, TAG_ITEM,
										TAG_ORDERID, TAG_NEWCOMMENT },
								new int[] { R.id.list_imageOrdrArchive,
										R.id.tvStoreNameOrdrArchive,
										R.id.tvStateOrdrArchive,
										R.id.tvDeliveryAddressOrdrArchive,
										R.id.tvReadyDateTimeOrdrArchive,
										R.id.tvItemOrdrArchive,
										R.id.tvOrderIDOrdrArchive,
										R.id.tvNewComment_archive });
						// updating listview
						listviewOrderArchive.setAdapter(adapter);
					} else {
						btnNext.setEnabled(false);
						btnPrevious.setEnabled(false);
					}
				}
			});

		}

	}
}
