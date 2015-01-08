package nz.co.rivertech.kiwicolour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnClickListener {
	SharedPreferences settings, set;
	String strMyOrderQuantity = "";

	// DASTOORATE MARBOOT BE SOUND
	SoundPool spClick;
	int IntClick = 0;
	// boolean ListIsEmptyMyOrder,ListIsEmptyMyShop= false;
	TimerTask timerTask;
	ProgressBar wheel, wheel2;
	// Progress Dialog
	private ListAdapter adapter, adapter2;
	// private ProgressDialog pDialog, pDialog2;
	ImageButton btnallshops, btnorderarchive, btnmyprofile;
	// TextView tvMyOrderHeader,tvMyShopsHeader;
//	TextView tvNewUserPrompt;
	ImageView imgSyncMyOrder, imgSyncMyShop, imgConnectionLost,imgStarRequest;
	ListView listMyShop, listMyOrder;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONParser jParser2 = new JSONParser();

	ArrayList<HashMap<String, String>> MyShopsList;
	ArrayList<HashMap<String, Object>> MyOrdersList;
	// url to get my paint shops and my orders
	private static String url_all_myshops = "http://rivertech.co.nz/kiwicolour/androidphp/get_myfavorite_shops.php";

	// UPDATED: If you send "start" and "rows" as param it works but it's
	// optional.
	// If you don't send the default values are start=0 and rows=3
	// This help we use the same PHP file for Orders Archive (All Orders)
	private static String url_all_myorders = "http://rivertech.co.nz/kiwicolour/androidphp/get_myorders.php";

	String PainterID;

	// JSON Node
	private static final String TAG_SUCCESS = "success";

	private static final String TAG_MYPAINTSHOPS = "MyPaintshops";
	private static final String TAG_PAINTSHOP_ID = "StoreID";
	private static final String TAG_PAINTSHOP_NAME = "StoreName";
	private static final String TAG_ADDRESS = "StoreAddress";
	private static final String TAG_STORE_PICTURE = "StorePicture";

	private static final String TAG_NEWCOMMENT = "NewComment";
	private static final String TAG_MYORDERS = "MyOrders";
	private static final String TAG_ORDERID = "OrderID";
	private static final String TAG_PICTURE = "StorePicture";
	private static final String TAG_STORENAME = "StoreName";
	private static final String TAG_STATE = "State";
	private static final String TAG_STATE_RELATION = "Sts";
	
	private static final String TAG_ITEM = "Item";
	private static final String TAG_DELIVERYADDRESS = "DeliveryAddress";
	private static final String TAG_READYDATETIME = "ReadyDateTime";

	// JSONArray
	JSONArray AllMyshops, AllMyOrders = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		setTitle("KiwiColour-Home");
		PainterID = Global.PainterID;
		wheel = (ProgressBar) findViewById(R.id.wheel);
		wheel2 = (ProgressBar) findViewById(R.id.wheel2);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		strMyOrderQuantity = settings.getString("OrderQuantity", "4");
		Log.i("OrderQuantity from oncreate", strMyOrderQuantity);

//		tvNewUserPrompt = (TextView) findViewById(R.id.tvNewUserPrompt);
//		tvNewUserPrompt.setOnClickListener(this);
		if (Global.PainterID.equals("")) {
			set = getSharedPreferences("MYPREFS", 0);
			Global.PainterID = set.getString("painterid", "");
			PainterID = Global.PainterID;
			Log.i("home", "on create Painter id:" + Global.PainterID);
		}

		imgStarRequest = (ImageView) findViewById(R.id.imgStarRequest);
		imgConnectionLost = (ImageView) findViewById(R.id.imgConnectionLost);
		imgConnectionLost.setOnClickListener(this);
		// marboot be sedaye click
		// spClick = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		// IntClick =spClick.load(this,R.raw.click,1);

		// initial buttons
		btnallshops = (ImageButton) findViewById(R.id.btnAllShops);
		btnorderarchive = (ImageButton) findViewById(R.id.btnOrdersArchive);
		btnmyprofile = (ImageButton) findViewById(R.id.btnMyprofile);

		btnallshops.setOnClickListener(this);
		btnorderarchive.setOnClickListener(this);
		btnmyprofile.setOnClickListener(this);
		// tvMyOrderHeader = (TextView) findViewById(R.id.tvMyOrders);
		// tvMyOrderHeader.setOnClickListener(this);
		// tvMyShopsHeader = (TextView) findViewById(R.id.tvMyShops);
		// tvMyShopsHeader.setOnClickListener(this);
		imgSyncMyOrder = (ImageView) findViewById(R.id.imgSyncMyOrder);
		imgSyncMyOrder.setOnClickListener(this);
		imgSyncMyShop = (ImageView) findViewById(R.id.imgSyncMyShop);
		imgSyncMyShop.setOnClickListener(this);

		MyShopsList = new ArrayList<HashMap<String, String>>();
		MyOrdersList = new ArrayList<HashMap<String, Object>>();

		// Loading favorite paintshops in Background Thread
		Log.e("FROM CHECKOUT", getIntent().getExtras().getString("ACTIVITY"));
		// ===============================================
		// if(getIntent().getExtras().getString("FROMCHECKOUT")=="1"){
		// new LoadAllMyOrdersWithoutDialogBox().execute();
		// }else{
		// new LoadAllMyshops().execute();
		// }
		// ================================================
		// Loading orders in Background Thread
		// new LoadAllMyOrders().execute();

		// Get listview
		// ListView lv = getListView();
		listMyShop = (ListView) findViewById(R.id.listMyShops);
		listMyOrder = (ListView) findViewById(R.id.listMyOrders);

		if (Global.globalVariable == "LOGIN") {
			new LoadAllMyshops().execute();
		}

		// if(!ListIsEmptyMyShop){
		listMyShop.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TextView temp = ((TextView)
				// view.findViewById(R.id.tvPaintShop));
				// String shopId = temp.getText().toString();
				//
				// // Starting new intent
				// Intent in = new
				// Intent(getApplicationContext(),ChoosePaintActivity.class);
				// // // sending pid to next activity
				// // in.putExtra("SHOP_ID", shopId);
				// in.putExtra("ShopName", shopId);
				// startActivity(in);
				// CHECK INTERNET CONNECTION
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
					TextView tvShopName = ((TextView) view
							.findViewById(R.id.tvPaintShopMyShop));
					String strShopName = tvShopName.getText().toString();
					TextView tvShopID = ((TextView) view
							.findViewById(R.id.tvIdMyShop));
					Integer intShopID = Integer.valueOf(tvShopID.getText()
							.toString());
					// Starting new intent
					Global.globalVariable = "HOME";
					Intent in = new Intent(getApplicationContext(),
							ChoosePaintActivity.class);
					// Sending ShopID and ShopName to next activity
					in.putExtra("ShopID", intShopID);
					in.putExtra("ShopName", strShopName);
					in.putExtra("ACTIVITY", "HOME");
					startActivity(in);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_up_out);
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// }//end of if

		// if(!ListIsEmptyMyOrder){
		listMyOrder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// TextView temp = ((TextView)
				// view.findViewById(R.id.tvOrderID));
				// String shopId = temp.getText().toString();
				// String label =
				// (TextView)(parent.findViewById(R.id.tvId)).getText().toString();
				// etCity.setText(shopId);
				// Starting new intent
				// Intent in = new
				// Intent(getApplicationContext(),ShopProfileActivity.class);
				// // sending pid to next activity
				// in.putExtra(TAG_ID, ShopId);
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
							.findViewById(R.id.tvState));
					String strState = tempState.getText().toString();
					if (strState.equals("Draft")) {
						Global.globalVariable = "HOME"; // JABEJAYI BEINE
														// ACTIVITY-HA BEHTARE
														// BA PUTEXTRA HANDLE
														// BESHE
						Intent in = new Intent(getApplicationContext(),
								CheckOutActivity.class);
						TextView tempId = ((TextView) view
								.findViewById(R.id.tvOrderID));
						String strOrderId = tempId.getText().toString();

						TextView tempName = ((TextView) view
								.findViewById(R.id.tvStoreName));
						String strShopName = tempName.getText().toString();

						in.putExtra("OrderID", strOrderId);
						in.putExtra("ShopName", strShopName);
						in.putExtra("Status", strState);
						startActivity(in);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_up_out);

					} else {
						// Toast.makeText(getApplicationContext(),"Only draft orders are editable in this version."
						// , Toast.LENGTH_SHORT).show();
						Global.globalVariable = "ARCHIVE";
						Intent in = new Intent(getApplicationContext(),
								CheckOutActivity.class);
						TextView tempId = ((TextView) view
								.findViewById(R.id.tvOrderID));
						String strOrderId = tempId.getText().toString();

						TextView tempName = ((TextView) view
								.findViewById(R.id.tvStoreName));
						String strShopName = tempName.getText().toString();

						TextView tempAddress = ((TextView) view
								.findViewById(R.id.tvDeliveryAddress));
						String strAddress = tempAddress.getText().toString();

						TextView tempDateTime = ((TextView) view
								.findViewById(R.id.tvReadyDateTime));
						String strDateTime = tempDateTime.getText().toString();

						in.putExtra("OrderID", strOrderId);
						in.putExtra("ShopName", strShopName);
						in.putExtra("Address", strAddress);
						in.putExtra("DateTime", strDateTime);
						in.putExtra("Status", strState);
						startActivity(in);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_up_out);

					}
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		// }// end of if
		// }

	}

	class LoadAllMyshops extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// pDialog = new ProgressDialog(HomeActivity.this);
			// pDialog.setMessage("Loading your favourite paint shops.\nPlease wait...");
			// pDialog.setIndeterminate(false);
			// pDialog.setCancelable(false);
			// pDialog.show();
			wheel2.setVisibility(View.VISIBLE);
			imgSyncMyShop.setVisibility(View.INVISIBLE);
			// settings =
			// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			//
			if (Global.PainterID.equals("")) {
				set = getSharedPreferences("MYPREFS", 0);
				Global.PainterID = set.getString("painterid", "");
				PainterID = Global.PainterID;
			}

		}

		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("painter_id", PainterID));

			// getting JSON string from URL
			JSONObject json = null;
			try {
				json = jParser.makeHttpRequest(url_all_myshops, "GET", params);
				if (json.length() != 0) {
					// Check your log cat for JSON reponse
					Log.d("All My shops: ", json.toString());

					try {
						// Checking for SUCCESS TAG
						int success = json.getInt(TAG_SUCCESS);

						if (success == 1) {
							// ListIsEmptyMyShop =false;
							MyShopsList.clear();
							// painshop/s found
							// Getting Array of paintshops
							AllMyshops = json.getJSONArray(TAG_MYPAINTSHOPS);

							// looping through All Products
							for (int i = 0; i < AllMyshops.length(); i++) {
								JSONObject c = AllMyshops.getJSONObject(i);

								// Storing each json item in variable
								String name = c.getString(TAG_PAINTSHOP_NAME);
								String address = c.getString(TAG_ADDRESS);
								String id = c.getString(TAG_PAINTSHOP_ID);
								String PicturePath = c.getString(TAG_PICTURE);
								String Status = c.getString(TAG_STATE_RELATION);
								
								String CurrectPathPic = "";
								if (!(PicturePath.equalsIgnoreCase("null"))) {
									String[] parts = PicturePath.split("/");
									CurrectPathPic = parts[0] + "/" + parts[1]
											+ "/" + parts[2]; // !!!!
								}
								PicturePath = "http://rivertech.co.nz/kiwicolour/public/"
										+ CurrectPathPic;

								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key =>
								// value
								map.put(TAG_PAINTSHOP_NAME, name);
								map.put(TAG_ADDRESS, address);
								map.put(TAG_PAINTSHOP_ID, id);
								map.put(TAG_PICTURE, PicturePath);
								map.put(TAG_STATE_RELATION, Status);
								
								// adding HashList to ArrayList
								MyShopsList.add(map);
							}
						}
						// else if(success == 0){
						// ListIsEmptyMyShop=true;
						// }
					} catch (JSONException e) {
						return null;
					}
				}
			} catch (Exception e1) {
				return null;
			}
			return "1";
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			// dismiss the dialog after getting all products
			// pDialog.dismiss();
			wheel2.setVisibility(View.INVISIBLE);
			imgSyncMyShop.setVisibility(View.VISIBLE);

			// updating UI from Background Thread
			if (result != null) {
				runOnUiThread(new Runnable() {
					public void run() {
						/*
						 * adapter = new SimpleAdapter(HomeActivity.this,
						 * MyShopsList, R.layout.list_row_my_shops, new String[]
						 * { TAG_PAINTSHOP_ID, TAG_PAINTSHOP_NAME, TAG_ADDRESS
						 * }, new int[] { R.id.tvIdMyShop,
						 * R.id.tvPaintShopMyShop, R.id.tvAddress });
						 */
						adapter = new LazyAdaptor2(HomeActivity.this,
								MyShopsList);
						listMyShop.setAdapter(adapter);
						new LoadAllMyOrders().execute();
					}
				});
			}
			// else{
			// runOnUiThread(new Runnable() {
			// public void run() {
			// // new LoadAllMyshops().execute();
			// Toast.makeText(getApplicationContext(), "Error in network!",
			// Toast.LENGTH_LONG).show();
			//
			// // showAlertDialog(HomeActivity.this,"Network error...",
			// "Something is wrong with network! Try again?" , false,"Shop");
			// }
			// });
			// }
		}
	}

	class LoadAllMyshops_Only extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			wheel2.setVisibility(View.VISIBLE);
			imgSyncMyShop.setVisibility(View.INVISIBLE);
			// settings =
			// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			// settings = getSharedPreferences("MYPREFS", 0);
			if (Global.PainterID.equals("")) {
				set = getSharedPreferences("MYPREFS", 0);
				Global.PainterID = set.getString("painterid", "");
				PainterID = Global.PainterID;
			}
		}

		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("painter_id", PainterID));
			// getting JSON string from URL
			JSONObject json = null;
			try {
				json = jParser.makeHttpRequest(url_all_myshops, "GET", params);
				if (json.length() != 0) {
					// Check your log cat for JSON reponse
					Log.d("All My shops: ", json.toString());

					try {
						// Checking for SUCCESS TAG
						int success = json.getInt(TAG_SUCCESS);

						if (success == 1) {
							// ListIsEmptyMyShop =false;
							MyShopsList.clear();
							// painshop/s found
							// Getting Array of paintshops
							AllMyshops = json.getJSONArray(TAG_MYPAINTSHOPS);

							// looping through All Products
							for (int i = 0; i < AllMyshops.length(); i++) {
								JSONObject c = AllMyshops.getJSONObject(i);

								// Storing each json item in variable
								String name = c.getString(TAG_PAINTSHOP_NAME);
								String address = c.getString(TAG_ADDRESS);
								String id = c.getString(TAG_PAINTSHOP_ID);
								String PicturePath = c.getString(TAG_PICTURE);
								String Status = c.getString(TAG_STATE_RELATION);

								String CurrectPathPic = "";
								if (!(PicturePath.equalsIgnoreCase("null"))) {
									String[] parts = PicturePath.split("/");
									CurrectPathPic = parts[0] + "/" + parts[1]
											+ "/" + parts[2]; // !!!!
								}
								PicturePath = "http://rivertech.co.nz/kiwicolour/public/"
										+ CurrectPathPic;

								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key =>
								// value
								map.put(TAG_PAINTSHOP_NAME, name);
								map.put(TAG_ADDRESS, address);
								map.put(TAG_PAINTSHOP_ID, id);
								map.put(TAG_PICTURE, PicturePath);
								map.put(TAG_STATE_RELATION, Status);
								// adding HashList to ArrayList
								MyShopsList.add(map);
							}
						}
						// else if(success == 0){
						// ListIsEmptyMyShop=true;
						// }
					} catch (JSONException e) {
						return null;
					}
				}
			} catch (Exception e1) {
				return null;
			}
			return "1";
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			// dismiss the dialog after getting all products
			wheel2.setVisibility(View.INVISIBLE);
			imgSyncMyShop.setVisibility(View.VISIBLE);

			// updating UI from Background Thread
			if (result != null) {
				runOnUiThread(new Runnable() {
					public void run() {
						/*
						 * adapter = new SimpleAdapter(HomeActivity.this,
						 * MyShopsList, R.layout.list_row_my_shops, new String[]
						 * { TAG_PAINTSHOP_ID, TAG_PAINTSHOP_NAME, TAG_ADDRESS
						 * }, new int[] { R.id.tvIdMyShop,
						 * R.id.tvPaintShopMyShop, R.id.tvAddress });
						 */
						adapter = new LazyAdaptor2(HomeActivity.this,
								MyShopsList);
						listMyShop.setAdapter(adapter);

					}
				});
			}
			// else{
			// runOnUiThread(new Runnable() {
			// public void run() {
			// Toast.makeText(getApplicationContext(), "Error in network!",
			// Toast.LENGTH_LONG).show();
			// // new LoadAllMyshops().execute();
			// // showAlertDialog(HomeActivity.this,"Network error...",
			// "Something is wrong with network! Try again?" , false,"Shop");
			// }
			// });
			// }
		}
	}

	// @SuppressWarnings("deprecation")
	// public void showAlertDialog(Context context, String title, String
	// message,Boolean status,final String sender) {
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(context);
	// builder.setTitle(title);
	// builder.setMessage(message);
	//
	// builder.setPositiveButton("Retry", new DialogInterface.OnClickListener()
	// {
	//
	// public void onClick(DialogInterface dialog, int which) {
	// if(sender.equals("Shop")){
	// new LoadAllMyshops().execute();
	// }else if(sender.equals("Order")){
	// new LoadAllMyOrders().execute();
	// }else if(sender.equals("Silent")){
	// new LoadAllMyOrders_Silent().execute();
	// }
	// }
	// });
	//
	// builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
	// {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// finish();
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// };
	// /**
	// * Background Async Task to Load all product by making HTTP Request
	// * */
	class LoadAllMyOrders extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		
			wheel.setVisibility(View.VISIBLE);
			imgSyncMyOrder.setVisibility(View.INVISIBLE);

			if (Global.PainterID.equals("")) {
				set = getSharedPreferences("MYPREFS", 0);
				Global.PainterID = set.getString("painterid", "");
				PainterID = Global.PainterID;
			}
		}

		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();

			params2.add(new BasicNameValuePair("painter_id", PainterID));
			params2.add(new BasicNameValuePair("rows", strMyOrderQuantity));

			// getting JSON string from URL
			JSONObject json2 = null;
			try {
				json2 = jParser2.makeHttpRequest(url_all_myorders, "GET",
						params2);
				if (json2.length() != 0) {
					// Check your log cat for JSON reponse
					Log.d("All My orders: ", json2.toString());

					try {
						// Checking for SUCCESS TAG
						int success = json2.getInt(TAG_SUCCESS);

						if (success == 1) {
							MyOrdersList.clear();
							// ListIsEmptyMyOrder=false;
							// painshop/s found
							// Getting Array of paintshops
							AllMyOrders = json2.getJSONArray(TAG_MYORDERS);

							// looping through All Orders
							for (int i = 0; i < AllMyOrders.length(); i++) {
								JSONObject c2 = AllMyOrders.getJSONObject(i);

								// Storing each json2 item in variable
								String Picture = c2.getString(TAG_PICTURE);
								String StoreName = c2.getString(TAG_STORENAME); // ****************
								String strNewComment = c2
										.getString(TAG_NEWCOMMENT);
								if (strNewComment.equals("0")) {
									strNewComment = "";
								} else {
									strNewComment = " " + strNewComment + " ";
								}

//								String[] parts = StoreName.split(" ");
//								if (parts[0].length() + parts[1].length() + 4 <= 20) {
//									StoreName = parts[0] + " " + parts[1]
//											+ "...";
//								} else {
//									StoreName = parts[0] + "...";
//								}

								String State = c2.getString(TAG_STATE);
								String DeliveryAddress = c2
										.getString(TAG_DELIVERYADDRESS);
								if (DeliveryAddress.length() > 35) {
								
									DeliveryAddress = (String) DeliveryAddress
											.subSequence(0, 32) + "...";
								}
								String ReadyDateTime = c2
										.getString(TAG_READYDATETIME);

								// ===============================================CHANGE
								// DATE FORMAT=================
								Date date = null;
								try {
									date = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss")
											.parse(ReadyDateTime);
									ReadyDateTime = new SimpleDateFormat(
											"E yyyy-MM-dd HH:mm").format(date);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								// ===============================END OF CHANGE
								// DATE FORMAT===========================
								String Item = c2.getString(TAG_ITEM);
								String orderID = c2.getString(TAG_ORDERID);

								// creating new HashMap
								HashMap<String, Object> map2 = new HashMap<String, Object>();

								// adding each child node to HashMap key =>
								// value
								if (State.equalsIgnoreCase("Draft")) {
									map2.put(TAG_PICTURE, R.drawable.state1);
								} else if (State.equalsIgnoreCase("Sent")) {
									map2.put(TAG_PICTURE, R.drawable.state2);
								} else if (State.equalsIgnoreCase("Received")) {
									map2.put(TAG_PICTURE, R.drawable.state3);
								} else if (State.equalsIgnoreCase("Accepted")) {
									map2.put(TAG_PICTURE, R.drawable.state4);
								} else if (State.equalsIgnoreCase("Canceled")) {
									map2.put(TAG_PICTURE, R.drawable.state5);
								} else if (State.equalsIgnoreCase("In Process")) {
									map2.put(TAG_PICTURE, R.drawable.state6);
								} else if (State.equalsIgnoreCase("Ready")) {
									map2.put(TAG_PICTURE, R.drawable.state7);
								} else if (State.equalsIgnoreCase("Shipped")) {
									map2.put(TAG_PICTURE, R.drawable.state8);
								}

								map2.put(TAG_STORENAME, StoreName);
								map2.put(TAG_STATE, State);
								map2.put(TAG_DELIVERYADDRESS, DeliveryAddress);
								map2.put(TAG_READYDATETIME, ReadyDateTime);
								map2.put(TAG_ITEM, Item);
								map2.put(TAG_ORDERID, orderID);
								map2.put(TAG_NEWCOMMENT, strNewComment);
								// adding HashList to ArrayList
								MyOrdersList.add(map2);
							}
						}
						// else if(success == 0){
						// ListIsEmptyMyOrder=true;
						// }
					} catch (JSONException e) {
						return null;
					}
				}
			} catch (Exception e1) {
				return null;
			}
			return "1";
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			// dismiss the dialog after getting all products
			// pDialog2.dismiss();
			wheel.setVisibility(View.INVISIBLE);
			imgSyncMyOrder.setVisibility(View.VISIBLE);

			// updating UI from Background Thread
			if (result != null) {
				runOnUiThread(new Runnable() {
					public void run() {
						adapter2 = new SimpleAdapter(
								HomeActivity.this,
								MyOrdersList,
								R.layout.list_row_my_order,
								new String[] { TAG_PICTURE, TAG_STORENAME,
										TAG_STATE, TAG_DELIVERYADDRESS,
										TAG_READYDATETIME, TAG_ITEM,
										TAG_ORDERID, TAG_NEWCOMMENT },
								new int[] { R.id.list_image, R.id.tvStoreName,
										R.id.tvState, R.id.tvDeliveryAddress,
										R.id.tvReadyDateTime, R.id.tvItem,
										R.id.tvOrderID, R.id.tvNewComment_home });
						listMyOrder.setAdapter(adapter2);

						// to find out list is empty or not
						if (listMyOrder.getCount() <= 0) {
//							tvNewUserPrompt.setText("\t\t\t\t\t\t\t\t\t\t\tNew User?\nTap to find your favorite paint shops");
							imgConnectionLost.setVisibility(View.VISIBLE);
							imgConnectionLost.setTag(R.drawable.newuser);
							imgConnectionLost.setBackgroundResource(R.drawable.newuser);
						} else {
							imgConnectionLost.setVisibility(View.INVISIBLE);
						}
					}
				});
			} else if (result == null && listMyOrder.getCount() <= 0) { // agar
																		// null
																		// bahsad
																		// va
																		// list
																		// ham
																		// khali
																		// bashad
			// new LoadAllMyshops().execute();
				imgConnectionLost.setTag(R.drawable.connection_lost);
				imgConnectionLost.setImageResource(R.drawable.connection_lost);
				imgConnectionLost.setVisibility(View.VISIBLE);
				// showAlertDialog(HomeActivity.this,"Network error...",
				// "Something is wrong with network! Try again?" ,
				// false,"Order");
			}
			// else if(listMyOrder.getChildCount() > 0){ // agar null bood va
			// list khali nabood
			// // do nothing
			// Toast.makeText(getApplicationContext(),
			// "Error in network!\ncouldn't refresh",Toast.LENGTH_LONG).show();
			//
			// }

		}
	}

	// ========================================================================
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
		case R.id.btnAllShops:

			if (internetIsPresent) {
				// spClick.play(IntClick, 1, 1, 0, 0, 1);
				Vibrator a = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				a.vibrate(20);
				Global.globalVariable = "HOME";
				Intent in = new Intent(getApplicationContext(),
						ShopSearchActivity.class);
				startActivity(in);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_up_out);
			} else {
				Toast.makeText(getApplicationContext(), "Connection Lost!",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btnOrdersArchive:
			if (internetIsPresent) {
				// spClick.play(IntClick, 1, 1, 0, 0, 1);
				Vibrator b = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				b.vibrate(20);
				Global.globalVariable = "HOME";
				Intent in2 = new Intent(getApplicationContext(),
						OrderArchiveActivity.class);
				startActivity(in2);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_up_out);
			} else {
				Toast.makeText(getApplicationContext(), "Connection Lost!",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.btnMyprofile:
			// spClick.play(IntClick, 1, 1, 0, 0, 1);
			Vibrator c = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			c.vibrate(20);
			break;
		case R.id.imgConnectionLost:
			Log.i("IMG","CLICKED");
			Object tag = imgConnectionLost.getTag();
			int id=(Integer) tag;
//			if(id==R.drawable.newuser){
//				Log.i("IMG","NEWUSER");
//				if (internetIsPresent) {
//					Global.globalVariable = "HOME";
//					Intent in3 = new Intent(getApplicationContext(),ShopSearchActivity.class);
//					startActivity(in3);
//					overridePendingTransition(R.anim.push_left_in,R.anim.push_up_out);
//				} else {
//					Toast.makeText(getApplicationContext(), "Connection Lost!",Toast.LENGTH_LONG).show();
//				}
//			}else 
			if(id==R.drawable.connection_lost){
				Log.i("IMG","CONNECTION LOST");
				imgConnectionLost.setVisibility(View.INVISIBLE);
				new LoadAllMyOrders().execute();
			}
			break;
		case R.id.imgSyncMyOrder:
			new LoadAllMyOrders().execute();
			break;
		case R.id.imgSyncMyShop:
			new LoadAllMyshops_Only().execute();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		// SharedPreferences settings = getSharedPreferences("MYPREFS", 0);
		strMyOrderQuantity = settings.getString("OrderQuantity", "4");

		Log.i("OrderQuantity from onresume", strMyOrderQuantity);

		// getIntent().getExtras().getString("ACTIVITY").equals("CHECKOUT")
		// Log.e("FROM CHECKOUT RESUME",
		// getIntent().getExtras().getString("FROMCHECKOUT")); //shayad lazem
		// beshe az bundle estefade konim choon getintent kar nemikone dar in
		// ravesh...
		Log.e("GLOBAL+++++++++++++++++++++++", Global.globalVariable);
		if (Global.globalVariable.equals("LOGIN")) {
			// new LoadAllMyshops().execute();
		} else if (Global.globalVariable.equals("CHECKOUT")) {
			new LoadAllMyOrders().execute();
		} else if (Global.globalVariable.equals("CHOOSEPAINT")) {
			new LoadAllMyOrders().execute();
		} else if (Global.globalVariable.equals("SEARCH")) {
			new LoadAllMyshops().execute();
			new LoadAllMyshops_Only().execute();
		} else if (Global.globalVariable.equals("HOME")) {
			// do nothing
		} else {
			new LoadAllMyOrders().execute();
			// new LoadAllMyshops_Only().execute();
		}
		Global.globalVariable = "HOME";
		Global.timerTaskHome = true;

		set = getSharedPreferences("MYPREFS", 0);
		if (Global.PainterID.equals("")) {
			Global.PainterID = set.getString("painterid", "");
			PainterID = Global.PainterID;
			Log.i("home painter id was null", "on resume Painter id:"
					+ Global.PainterID);
		}

		Log.i("home", "on resume Painter id:" + Global.PainterID);

		doAsyncTaskPeriodically();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mnusettings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings) {
			Intent in = new Intent(HomeActivity.this, SettingsActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);

		}
		if (item.getItemId() == R.id.about) {
			Intent in = new Intent(HomeActivity.this, AboutActivity.class);
			startActivity(in);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_up_out);

		}
		return super.onOptionsItemSelected(item);
	}

	public void doAsyncTaskPeriodically() {
		final Timer timer = new Timer();
		final Handler handler = new Handler();

		timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {
							if (Global.timerTaskHome) {
								LoadAllMyOrders LAMO = new LoadAllMyOrders();
								LAMO.execute();
							} else {
								timerTask.cancel();
								timerTask = null;
								timer.cancel();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				});
			}
		};
		timer.schedule(timerTask, 10000, 30000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("home", "onstop Painter id:" + Global.PainterID);
		Global.timerTaskHome = false;
		timerTask.cancel();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("home", "on pause Painter id:" + Global.PainterID);
		Global.timerTaskHome = false;
		timerTask.cancel();
	}

}
