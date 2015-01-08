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
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePaintActivity extends Activity implements OnClickListener {
	Integer intCC = 0;
	ListView listviewFandeck;
	// Lotfan in khat ba deghat paksaazi shavad:
	String intQty, strShopName = "";
	// private ListAdapter adapter;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONParser jParser2 = new JSONParser();
	ArrayList<HashMap<String, String>> BrandsList;
	ArrayList<HashMap<String, String>> FinishesList;
	ArrayList<HashMap<String, String>> ColoursList;
	// ArrayList<HashMap<String, String>> FandecksList;

	HashMap<String, Integer> BrandMap = new HashMap<String, Integer>();
	HashMap<String, Integer> FinishMap = new HashMap<String, Integer>();
	HashMap<String, Integer> ColourMap = new HashMap<String, Integer>();
	HashMap<String, Integer> QtySizeMap = new HashMap<String, Integer>();
	private static String url_fandeck = "http://rivertech.co.nz/kiwicolour/androidphp/get_fandecks.php";
	private static String url_brand_finish_colour = "http://rivertech.co.nz/kiwicolour/androidphp/get_brand_finish_colour.php";
	private static String url_create_order = "http://rivertech.co.nz/kiwicolour/androidphp/create_order.php";
	// private static String url_fandeck =
	// "http://rivertech.co.nz/kiwicolour/androidphp/get_fandecks.php";
	private static String url_create_item = "http://rivertech.co.nz/kiwicolour/androidphp/add_item.php";
	// JSON Node names
	private static final String TAG_SUCCESS = "success";

	private static final String TAG_BRANDS = "Brands";
	private static final String TAG_BRAND_ID = "BID";
	private static final String TAG_BRAND_NAME = "BName";

	private static final String TAG_FINISHES = "Finishes";
	private static final String TAG_FINISH_ID = "FID";
	private static final String TAG_FINISH_NAME = "FName";

	private static final String TAG_COLOURS = "Colours";
	private static final String TAG_COLOUR_NAME = "CName";
	private static final String TAG_COLOUR_COUNT = "CCount";

	private static final String TAG_QTY = "Quantity";
	private static final String TAG_CAN_SIZE = "CanSize";

	private static final String TAG_ALL_FANDECKS = "Fandecks";
	private static final String TAG_FANDECK_NAME = "FandeckName";
	private static final String TAG_FANDECK_ID = "FandeckID";
	private static final String TAG_COLOUR = "colour";

	JSONArray JBrands = null;
	JSONArray JFinishes = null;
	JSONArray JColours = null;
	JSONArray JNewOrder = null;
	JSONArray JAllFandecks = null;

	private ProgressDialog pDialog;

	private static String[] AllBrandNames = null;
	private static String[] AllFinishNames = null;
	private static String[] AllColourNames = null;

	SoundPool spClick;
	int IntClick = 0;

	String strSelectedBrand = null;
	String strSelectedFinish = null;
	String strSelectedColour = null;
	String strBID = null;
	String strFID = null;
	String strCName = null;
	String strQty = null;
	String strCanSize = null;

	boolean blnFirstItem = false;
	Integer intOrderID = 0; // ***********************************************************
							// namayeshe order id dar # *****************
	Integer intShopID;
	Integer intPainterID = 1; // badan az prefrences gerefte mishavad
								// ***************************************************

	Button btnAddToCart, btnCart, btnCheckOut;
	TextView tvShopName, tvX4, tvX10, tvX15, tvX20, ItemDesc, tvCartNum,
			tvFandeckDesc, tvOrderID;
	ImageView imgP4, imgP10, imgP15, imgP20, imgN4, imgN10, imgN15, imgN20,
			img4L, img10L, img15L, img20L;
	AutoCompleteTextView etBrands, etFinishes, etColours, etcolour;

	// ListView listviewFandeck;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_paint_activity);
		tvCartNum = (TextView) findViewById(R.id.tvCartNum);
		Global.globalVariable = "CHOOSEPAINT";
		btnCart = (Button) findViewById(R.id.btnCart);
		tvOrderID = (TextView) findViewById(R.id.tvPaintOrderID);
		tvShopName = (TextView) findViewById(R.id.tvShopName);
		// dar 2 khate zir az activity ghabl bayad ersal shavad: ShopID va
		// ShopName
		// ***********************************************************************
		
		if (getIntent().getExtras().getString("ACTIVITY").equals("SHOPSEARCH")) {
			tvShopName.setText(getIntent().getExtras().getString("ShopName"));
		}
		if (getIntent().getExtras().getString("ACTIVITY").equals("HOME")) {
			tvShopName.setText(getIntent().getExtras().getString("ShopName"));
		}
		if (getIntent().getExtras().getString("ACTIVITY").equals("CHECKOUT")) {
			String strShopName = getIntent().getExtras().getString("ShopName");
			tvShopName.setText(getIntent().getExtras().getString("ShopName"));
			setTitle("Choose Paint @ " + strShopName);
			// intShopID = getIntent().getExtras().getInt("ShopID");
			if (Integer.valueOf(getIntent().getExtras().getString("ItemNum")) != 0) {
				tvCartNum.setText(getIntent().getExtras().getString("ItemNum"));
				tvCartNum.setVisibility(View.VISIBLE);
				btnCart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cartfull64, 0,0, 0);

				// *************************************** be dalile vazeh
				// naboodane check out az icon "shopcartapply64"
				// *************************************** rooye dokmeye
				// checkout estefade mishavad.

				// btnCart.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				// R.drawable.shopcartapply64, 0); >>> ICONI KE TIK DARE FAGHAT
				// VASE DOKMEYE CHECKOUT ESTEFADE BESHE

			} else {
				tvCartNum.setVisibility(View.INVISIBLE);
			}
			tvOrderID.setText(getIntent().getExtras().getString("OrderID"));
		} else if (getIntent().getExtras().getString("ACTIVITY").equals("HOME")) {
			String strShopName = getIntent().getExtras().getString("ShopName");
			intShopID = getIntent().getExtras().getInt("ShopID");
			setTitle("Choose Paint @ " + strShopName);
		} else if (getIntent().getExtras().getString("ACTIVITY")
				.equals("SHOPSEARCH")) {
			String strShopName = getIntent().getExtras().getString("ShopName");
			intShopID = getIntent().getExtras().getInt("ShopID");
			setTitle("Choose Paint @ " + strShopName);
		}

		new LoadAllProducts().execute(); // age beshe badan az ye tarighe dige
											// load beshe na az we b kheili
											// khoob mishe

		// tvOrderID = (TextView) findViewById(R.id.tvPaintOrderID);

		// marboot be auto complete ============================
		// Hashmap for ListView
		BrandsList = new ArrayList<HashMap<String, String>>();
		FinishesList = new ArrayList<HashMap<String, String>>();
		ColoursList = new ArrayList<HashMap<String, String>>();

		// AutoCompleteTextView etbrand = (AutoCompleteTextView)
		// findViewById(R.id.etBrand);
		// AutoCompleteTextView etfinish = (AutoCompleteTextView)
		// findViewById(R.id.etFinish);
		etcolour = (AutoCompleteTextView) findViewById(R.id.etColour);

		// ====================================================

		spClick = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		IntClick = spClick.load(this, R.raw.click, 1);

		btnCheckOut = (Button) findViewById(R.id.btnCheckOut);
		btnCheckOut.setOnClickListener(this);

		tvFandeckDesc = (TextView) findViewById(R.id.tvFandeckDesc);

		img4L = (ImageView) findViewById(R.id.img4L);
		img10L = (ImageView) findViewById(R.id.img10L);
		img15L = (ImageView) findViewById(R.id.img15L);
		img20L = (ImageView) findViewById(R.id.img20L);

		imgP4 = (ImageView) findViewById(R.id.imgPos4L);
		imgP10 = (ImageView) findViewById(R.id.imgPos10L);
		imgP15 = (ImageView) findViewById(R.id.imgPos15L);
		imgP20 = (ImageView) findViewById(R.id.imgPos20L);

		imgN4 = (ImageView) findViewById(R.id.imgNeg4L);
		imgN10 = (ImageView) findViewById(R.id.imgNeg10L);
		imgN15 = (ImageView) findViewById(R.id.imgNeg15L);
		imgN20 = (ImageView) findViewById(R.id.imgNeg20L);

		tvX4 = (TextView) findViewById(R.id.tv4L);
		tvX10 = (TextView) findViewById(R.id.tv10L);
		tvX15 = (TextView) findViewById(R.id.tv15L);
		tvX20 = (TextView) findViewById(R.id.tv20L);

		btnAddToCart = (Button) findViewById(R.id.btnAddToCart);

		img4L.setOnClickListener(this);
		img10L.setOnClickListener(this);
		img15L.setOnClickListener(this);
		img20L.setOnClickListener(this);

		btnAddToCart.setOnClickListener(this);

		imgP4.setOnClickListener(this);
		imgP10.setOnClickListener(this);
		imgP15.setOnClickListener(this);
		imgP20.setOnClickListener(this);

		imgN4.setOnClickListener(this);
		imgN10.setOnClickListener(this);
		imgN15.setOnClickListener(this);
		imgN20.setOnClickListener(this);

		btnCart.setOnClickListener(this);

		etcolour.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tvFandeckDesc.setText("");
				String s = (String) parent.getItemAtPosition(position);
				intCC = ((ColourMap.containsKey(s)) ? ColourMap.get(s) : 0);
				if (intCC > 1) {
					Boolean internetIsPresent = null;
					try {
						ConnectionDetector cd = new ConnectionDetector(
								getApplicationContext());
						internetIsPresent = cd.isConnectingToInternet();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (internetIsPresent) {
						Intent in = new Intent(ChoosePaintActivity.this,
								WhichFandeckActivity.class);
						in.putExtra("COLOUR", s);
						startActivityForResult(in, 0);
						overridePendingTransition(R.anim.push_left_in,
								R.anim.push_up_out);
					} else {
						Toast.makeText(getApplicationContext(),
								"Connection Lost!", Toast.LENGTH_LONG).show();
						etcolour.setText("");
					}
					// fandeckDialogBox();
				} else if (intCC == 1) {
					// dar in ghesmat bayad dar soorate boodane faghat 1 fandeck
					// aan ra neshan dahad
					Boolean internetIsPresent = null;
					try {
						ConnectionDetector cd = new ConnectionDetector(
								getApplicationContext());
						internetIsPresent = cd.isConnectingToInternet();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (internetIsPresent) {
						new loadFandeck().execute();
						Toast ts = Toast.makeText(getApplicationContext(),
								"Loading Fandeck...", Toast.LENGTH_SHORT);
						ts.setGravity(Gravity.CENTER, 0, 0);
						ts.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Connection Lost!", Toast.LENGTH_LONG).show();
						etcolour.setText("");
					}
				} else {
					tvFandeckDesc.setText("");
				}
			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == 1) {
				tvFandeckDesc
						.setText(data.getStringExtra("fandeck").toString());
			} else {
				// Toast.makeText(this, "Blank", Toast.LENGTH_SHORT).show();
				tvFandeckDesc.setText("Fandeck not selected !");
			}
			// etColours.setText(""); //in khat baes mishe age dokmeye back ro
			// zad va bargasht colour khali beshe va validity joor beshe.
		}
	}

	private void VisibleInvisible(int Bucket) {

		switch (Bucket) {
		case 4:
			if (Integer.valueOf(tvX4.getText().toString()) == 0) {
				imgP4.setVisibility(View.VISIBLE);
				imgP10.setVisibility(View.INVISIBLE);
				imgP15.setVisibility(View.INVISIBLE);
				imgP20.setVisibility(View.INVISIBLE);

				tvX4.setText("1");
				tvX10.setText("0");
				tvX15.setText("0");
				tvX20.setText("0");

				tvX4.setVisibility(View.VISIBLE);
				tvX10.setVisibility(View.INVISIBLE);
				tvX15.setVisibility(View.INVISIBLE);
				tvX20.setVisibility(View.INVISIBLE);

				imgN4.setVisibility(View.VISIBLE);
				imgN10.setVisibility(View.INVISIBLE);
				imgN15.setVisibility(View.INVISIBLE);
				imgN20.setVisibility(View.INVISIBLE);
			}
			break;
		case 10:
			if (Integer.valueOf(tvX10.getText().toString()) == 0) {
				imgP4.setVisibility(View.INVISIBLE);
				imgP10.setVisibility(View.VISIBLE);
				imgP15.setVisibility(View.INVISIBLE);
				imgP20.setVisibility(View.INVISIBLE);

				tvX4.setText("0");
				tvX10.setText("1");
				tvX15.setText("0");
				tvX20.setText("0");

				tvX4.setVisibility(View.INVISIBLE);
				tvX10.setVisibility(View.VISIBLE);
				tvX15.setVisibility(View.INVISIBLE);
				tvX20.setVisibility(View.INVISIBLE);

				imgN4.setVisibility(View.INVISIBLE);
				imgN10.setVisibility(View.VISIBLE);
				imgN15.setVisibility(View.INVISIBLE);
				imgN20.setVisibility(View.INVISIBLE);
			}
			break;
		case 15:
			if (Integer.valueOf(tvX15.getText().toString()) == 0) {
				imgP4.setVisibility(View.INVISIBLE);
				imgP10.setVisibility(View.INVISIBLE);
				imgP15.setVisibility(View.VISIBLE);
				imgP20.setVisibility(View.INVISIBLE);

				tvX4.setText("0");
				tvX10.setText("0");
				tvX15.setText("1");
				tvX20.setText("0");

				tvX4.setVisibility(View.INVISIBLE);
				tvX10.setVisibility(View.INVISIBLE);
				tvX15.setVisibility(View.VISIBLE);
				tvX20.setVisibility(View.INVISIBLE);

				imgN4.setVisibility(View.INVISIBLE);
				imgN10.setVisibility(View.INVISIBLE);
				imgN15.setVisibility(View.VISIBLE);
				imgN20.setVisibility(View.INVISIBLE);
			}
			break;
		case 20:
			if (Integer.valueOf(tvX20.getText().toString()) == 0) {
				imgP4.setVisibility(View.INVISIBLE);
				imgP10.setVisibility(View.INVISIBLE);
				imgP15.setVisibility(View.INVISIBLE);
				imgP20.setVisibility(View.VISIBLE);

				tvX4.setText("0");
				tvX10.setText("0");
				tvX15.setText("0");
				tvX20.setText("1");

				tvX4.setVisibility(View.INVISIBLE);
				tvX10.setVisibility(View.INVISIBLE);
				tvX15.setVisibility(View.INVISIBLE);
				tvX20.setVisibility(View.VISIBLE);

				imgN4.setVisibility(View.INVISIBLE);
				imgN10.setVisibility(View.INVISIBLE);
				imgN15.setVisibility(View.INVISIBLE);
				imgN20.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img4L:
			VisibleInvisible(4);
			break;
		case R.id.img10L:
			VisibleInvisible(10);
			break;
		case R.id.img15L:
			VisibleInvisible(15);
			break;
		case R.id.img20L:
			VisibleInvisible(20);
			break;
		case R.id.btnCart:

			if (tvCartNum.getVisibility() == View.INVISIBLE) {
				Toast.makeText(getApplicationContext(),
						"Shopping Cart in empty!", Toast.LENGTH_SHORT).show();
			} else {
				Boolean internetIsPresent = null;
				try {
					ConnectionDetector cd = new ConnectionDetector(
							getApplicationContext());
					internetIsPresent = cd.isConnectingToInternet();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (internetIsPresent) {
					Global.globalVariable = "CHOOSEPAINT";
					Intent in = new Intent(getApplicationContext(),
							ShoppingCartActivity.class);
					// Sending OrderID to next activity
					in.putExtra("OrderId", tvOrderID.getText().toString());
					startActivity(in);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_up_out);
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		case R.id.btnAddToCart:
			Boolean internetIsPresent = null;
			try {
				ConnectionDetector cd = new ConnectionDetector(
						getApplicationContext());
				internetIsPresent = cd.isConnectingToInternet();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (internetIsPresent) {
				// Brand - Finish - Colour Fields
				etBrands = (AutoCompleteTextView) findViewById(R.id.etBrand);
				strSelectedBrand = etBrands.getText().toString().trim();
				etFinishes = (AutoCompleteTextView) findViewById(R.id.etFinish);
				strSelectedFinish = etFinishes.getText().toString().trim();
				etColours = (AutoCompleteTextView) findViewById(R.id.etColour);
				strSelectedColour = etColours.getText().toString().trim();
				// Quantity - Can Size is returned as HashMap
				QtySizeMap = getQtySize();
				intCC = ((ColourMap.containsKey(strSelectedColour)) ? ColourMap
						.get(strSelectedColour) : 0);
				String strSelectedFandeck = tvFandeckDesc.getText().toString();
				// Checking Colour Count to show ChooseFandesck:
				// intCC = ((ColourMap.containsKey(strSelectedColour)) ?
				// ColourMap.get(strSelectedColour) : 0) ;
				// if (intCC > 1) {
				// // fandeckDialogBox();
				// Intent in = new
				// Intent(ChoosePaintActivity.this,WhichFandeckActivity.class);
				// in.putExtra("COLOUR", strSelectedColour);
				// startActivity(in);
				// }
				// Input Validations:
				if (intCC > 1
						&& (strSelectedFandeck == "" || strSelectedFandeck == "Fandeck not selected !")) {
					Intent in = new Intent(ChoosePaintActivity.this,
							WhichFandeckActivity.class);
					in.putExtra("COLOUR", strSelectedColour);
					startActivityForResult(in, 0);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_up_out);

				} else if (etValidate(strSelectedBrand, strSelectedFinish,
						strSelectedColour, QtySizeMap)) {
					strBID = BrandMap.get(strSelectedBrand).toString();
					strFID = FinishMap.get(strSelectedFinish).toString();
					strCName = strSelectedColour + " from "
							+ strSelectedFandeck; // ******************************************
					// + Fandeck
					// strCName.concat(" " +
					// tvFandeckDesc.getText().toString());//**************
					strQty = QtySizeMap.get(TAG_QTY).toString();
					strCanSize = QtySizeMap.get(TAG_CAN_SIZE).toString();

					if (tvCartNum.getVisibility() == View.INVISIBLE) {
						blnFirstItem = true;
					}

					if (tvOrderID.getText().toString().equals("")) {
						new createNewOrder().execute();
					} else {
						new createNewItems().execute();
					}
					clrForm(); // dar poste new item ....... dar sooorate
								// success
								// boodane newItem
					String strItem = strQty + " x " + strCanSize + "L  "
							+ strSelectedBrand + "  " + strSelectedFinish
							+ "  " + strSelectedColour + " "
							+ tvFandeckDesc.getText().toString();
					// btnCheckOut = (Button) findViewById(R.id.btnCheckOut);

					// *************************************** be dalile vazeh
					// naboodane check out az icon "shopcartapply64"
					// *************************************** rooye dokmeye
					// checkout estefade mishavad.

					// btnCart.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					// R.drawable.shopcartapply64, 0); >>> ICONI KE TIK DARE
					// FAGHAT VASE DOKMEYE CHECKOUT ESTEFADE BESHE

					if (blnFirstItem) {
						tvCartNum.setVisibility(View.VISIBLE);
						blnFirstItem = false;
						btnCart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cartfull64, 0,0, 0);
//						btnCart.setBackgroundResource(R.drawable.cartfull64);
					} else {
						tvCartNum.setText(String.valueOf(Integer
								.parseInt(tvCartNum.getText().toString()) + 1));
						
					} // ************ ezafe shodane tvCartNum Trace shavad ****
					etBrands.setFocusable(true);
					etBrands.requestFocus();
					Toast.makeText(this,
							strItem + "\n\nItem added to shopping cart.",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Connection Lost!",
						Toast.LENGTH_LONG).show();
			}
			break;

		// badan barasi beshe bebinim mishe be shekle
		// "case R.id.imgPos4L, R.id.imgPos10L,..."
		case R.id.imgPos4L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			tvX4.setText(String.valueOf(Integer.parseInt(tvX4.getText()
					.toString()) + 1));
			break;
		case R.id.imgPos10L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			tvX10.setText(String.valueOf(Integer.parseInt(tvX10.getText()
					.toString()) + 1));
			break;
		case R.id.imgPos15L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			tvX15.setText(String.valueOf(Integer.parseInt(tvX15.getText()
					.toString()) + 1));
			break;
		case R.id.imgPos20L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			tvX20.setText(String.valueOf(Integer.parseInt(tvX20.getText()
					.toString()) + 1));
			break;
		case R.id.imgNeg4L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			if (Integer.valueOf(tvX4.getText().toString()) > 1) {
				tvX4.setText(String.valueOf(Integer.parseInt(tvX4.getText()
						.toString()) - 1));
			}
			break;
		case R.id.imgNeg10L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			if (Integer.valueOf(tvX10.getText().toString()) > 1) {
				tvX10.setText(String.valueOf(Integer.parseInt(tvX10.getText()
						.toString()) - 1));
			}
			break;
		case R.id.imgNeg15L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			if (Integer.valueOf(tvX15.getText().toString()) > 1) {
				tvX15.setText(String.valueOf(Integer.parseInt(tvX15.getText()
						.toString()) - 1));
			}
			break;
		case R.id.imgNeg20L:
			spClick.play(IntClick, 1, 1, 0, 0, 1);
			if (Integer.valueOf(tvX20.getText().toString()) > 1) {
				tvX20.setText(String.valueOf(Integer.parseInt(tvX20.getText()
						.toString()) - 1));
			}
			break;
		case R.id.btnCheckOut:
			tvCartNum = (TextView) findViewById(R.id.tvCartNum);
			if (tvCartNum.getVisibility() == View.INVISIBLE) {
				Toast.makeText(this, "Shopping cart is empty !",
						Toast.LENGTH_SHORT).show();
			} else {
				internetIsPresent = null;
				try {
					ConnectionDetector cd = new ConnectionDetector(
							getApplicationContext());
					internetIsPresent = cd.isConnectingToInternet();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (internetIsPresent) {
					Global.globalVariable = "CHOOSEPAINT";
					Intent in = new Intent(this, CheckOutActivity.class);
					in.putExtra("OrderID", tvOrderID.getText().toString());
					in.putExtra("ShopName", tvShopName.getText().toString());
					in.putExtra("Status", "Draft");
					// in.putExtra("ShopID", intShopID.toString());
					startActivity(in);
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_up_out);
				} else {
					Toast.makeText(getApplicationContext(), "Connection Lost!",
							Toast.LENGTH_LONG).show();
				}
			}
			break;
		}

	}

	private void clrForm() {
		etBrands.setText("");
		etFinishes.setText("");
		etColours.setText("");
		tvFandeckDesc.setText("");

		imgP4.setVisibility(View.INVISIBLE);
		imgP10.setVisibility(View.INVISIBLE);
		imgP15.setVisibility(View.INVISIBLE);
		imgP20.setVisibility(View.INVISIBLE);

		tvX4.setText("0");
		tvX10.setText("0");
		tvX15.setText("0");
		tvX20.setText("0");

		tvX4.setVisibility(View.INVISIBLE);
		tvX10.setVisibility(View.INVISIBLE);
		tvX15.setVisibility(View.INVISIBLE);
		tvX20.setVisibility(View.INVISIBLE);

		imgN4.setVisibility(View.INVISIBLE);
		imgN10.setVisibility(View.INVISIBLE);
		imgN15.setVisibility(View.INVISIBLE);
		imgN20.setVisibility(View.INVISIBLE);
	}

	private HashMap<String, Integer> getQtySize() {
		HashMap<String, Integer> mapQtySize = new HashMap<String, Integer>();
		Integer intCanSize = 0;
		Integer intQty = 0;
		tvX4 = (TextView) findViewById(R.id.tv4L);
		tvX10 = (TextView) findViewById(R.id.tv10L);
		tvX15 = (TextView) findViewById(R.id.tv15L);
		tvX20 = (TextView) findViewById(R.id.tv20L);
		Integer intX4 = Integer.parseInt(tvX4.getText().toString());
		Integer intX10 = Integer.parseInt(tvX10.getText().toString());
		Integer intX15 = Integer.parseInt(tvX15.getText().toString());
		Integer intX20 = Integer.parseInt(tvX20.getText().toString());
		if (intX4 > 0) {
			intCanSize = 4;
			intQty = intX4;
		} else if (intX10 > 0) {
			intCanSize = 10;
			intQty = intX10;
		}
		if (intX15 > 0) {
			intCanSize = 15;
			intQty = intX15;
		}
		if (intX20 > 0) {
			intCanSize = 20;
			intQty = intX20;
		}
		mapQtySize.put(TAG_QTY, intQty);
		mapQtySize.put(TAG_CAN_SIZE, intCanSize);
		return mapQtySize;
	}

	// Input Validations:
	private boolean etValidate(String selectedBrand, String selectedFinish,
			String selectedColour, HashMap<String, Integer> mapQtySize) {
		if (selectedBrand.equals("") || !BrandMap.containsKey(selectedBrand)) {
			Toast.makeText(this, "Brand is blank or invalid !",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (selectedFinish.equals("")
				|| !FinishMap.containsKey(selectedFinish)) {
			Toast.makeText(this, "Finish is blank or invalid !",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (selectedColour.equals("")) {
			Toast.makeText(this, "Colour is blank or invalid !",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (selectedColour.equals("")) {
			Toast.makeText(this, "Colour is blank or invalid !",
					Toast.LENGTH_SHORT).show();
			return false;

		} else if (mapQtySize.get(TAG_QTY) == 0) {
			Toast.makeText(this, "Please select the Can Size.",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * Background Async Task to Load all Brands, Finishes, Colours by making
	 * HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ChoosePaintActivity.this);
			pDialog.setMessage("Loading colours. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// // Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_brand_finish_colour,
					"GET", params);

			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					JBrands = json.getJSONArray(TAG_BRANDS);
					JFinishes = json.getJSONArray(TAG_FINISHES);
					JColours = json.getJSONArray(TAG_COLOURS);

					// looping through All Brands
					AllBrandNames = new String[JBrands.length()];
					for (int i = 0; i < JBrands.length(); i++) {
						JSONObject c = JBrands.getJSONObject(i);

						// Storing each json item in variable
						Integer BID = Integer
								.valueOf(c.getString(TAG_BRAND_ID));
						String BName = c.getString(TAG_BRAND_NAME);

						// Array For Autocomplete
						AllBrandNames[i] = BName;

						// adding each child node to HashMap key => value
						BrandMap.put(BName, BID);
					}

					// looping through All Finishes
					AllFinishNames = new String[JFinishes.length()];
					for (int i = 0; i < JFinishes.length(); i++) {
						JSONObject c = JFinishes.getJSONObject(i);

						// Storing each json item in variable
						Integer FID = Integer.valueOf(c
								.getString(TAG_FINISH_ID));
						String FName = c.getString(TAG_FINISH_NAME);

						// Array For Autocomplete
						AllFinishNames[i] = FName;

						// adding each child node to HashMap key => value
						FinishMap.put(FName, FID);
					}

					// looping through All Colours
					AllColourNames = new String[JColours.length()];
					for (int i = 0; i < JColours.length(); i++) {
						JSONObject c = JColours.getJSONObject(i);

						// Storing each json item in variable
						String CName = c.getString(TAG_COLOUR_NAME);
						Integer CC = Integer.valueOf(c
								.getString(TAG_COLOUR_COUNT));

						// Array For Autocomplete
						AllColourNames[i] = CName;

						// adding each child node to HashMap key => value
						ColourMap.put(CName, CC);
					}
				} else {
					// no products found

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
					AutoCompleteTextView etbrand = (AutoCompleteTextView) findViewById(R.id.etBrand);
					ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>(
							ChoosePaintActivity.this,
							R.layout.dropdown_autocomplete, AllBrandNames);
					etbrand.setAdapter(adapterBrand);

					AutoCompleteTextView etfinish = (AutoCompleteTextView) findViewById(R.id.etFinish);
					ArrayAdapter<String> adapterFinish = new ArrayAdapter<String>(
							ChoosePaintActivity.this,
							R.layout.dropdown_autocomplete, AllFinishNames);
					etfinish.setAdapter(adapterFinish);

					AutoCompleteTextView etcolour = (AutoCompleteTextView) findViewById(R.id.etColour);
					ArrayAdapter<String> adapterColour = new ArrayAdapter<String>(
							ChoosePaintActivity.this,
							R.layout.dropdown_autocomplete, AllColourNames);
					etcolour.setAdapter(adapterColour);
				}
			});

		}

	}

	/**
	 * Background Async Task to Create new order
	 * */
	class createNewOrder extends AsyncTask<String, String, String> {
		String strOrderID = null;
		boolean blnNewOrderAdded = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ChoosePaintActivity.this);
			pDialog.setMessage("Creating new order...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating order
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("shop_id", intShopID.toString()));
			params2.add(new BasicNameValuePair("painter_id", Global.PainterID)); // az
																					// Pref
			// gerefte
			// shavad
			// ......********************
			params2.add(new BasicNameValuePair("state", "1"));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json2 = jParser2.makeHttpRequest(url_create_order,
					"GET", params2);

			// check log cat fro response
			Log.d("Create New Order:", json2.toString());

			// check for success tag
			try {
				blnNewOrderAdded = (json2.getInt(TAG_SUCCESS) == 1) ? true
						: false;
				strOrderID = json2.getString("newid");
				// tvOrderID.setText(strOrderID);
				// check success...
				// .......
			} catch (JSONException e) {
				Toast.makeText(ChoosePaintActivity.this, "Error",
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();

			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					tvOrderID.setText(strOrderID);
				}
			});

			// if blnNewOrderAdded {AddNewItem().execute();};
			// ******************************************************************************
			new createNewItems().execute();
		}

	}

	class createNewItems extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ChoosePaintActivity.this);
			pDialog.setMessage("Creating new Item...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Building Parameters
			List<NameValuePair> params3 = new ArrayList<NameValuePair>();
			params3.add(new BasicNameValuePair("order_id", tvOrderID.getText()
					.toString()));
			params3.add(new BasicNameValuePair("qty", strQty));
			params3.add(new BasicNameValuePair("can_size", strCanSize));
			params3.add(new BasicNameValuePair("brand_id", strBID));
			params3.add(new BasicNameValuePair("finish_id", strFID));
			params3.add(new BasicNameValuePair("color", strCName));
			Log.d("Item:", strBID + "  " + strFID + "  " + strCName + "  "
					+ strCanSize + "  " + strQty);
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json2 = jParser2.makeHttpRequest(url_create_item, "GET",
					params3);

			// check log cat fro response
			Log.d("Create New Item:", json2.toString());

			// check for success tag
			// try {
			// blnNewOrderAdded = (json2.getInt(TAG_SUCCESS)==1) ? true : false;
			// strOrderID = json2.getString("newid");
			// //tvOrderID.setText(strOrderID);
			// //check success...
			// //.......
			// } catch (JSONException e) {
			// Toast.makeText(ChoosePaintActivity.this, "Error",
			// Toast.LENGTH_SHORT).show();
			// e.printStackTrace();
			//
			// }

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pDialog.dismiss();

		}

	}

	class loadFandeck extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// Building Parameters
			List<NameValuePair> params1 = new ArrayList<NameValuePair>();
			params1.add(new BasicNameValuePair(TAG_COLOUR, etcolour.getText()
					.toString()));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_fandeck, "GET",
					params1);
			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// fandeck found
					JAllFandecks = json.getJSONArray(TAG_ALL_FANDECKS);
					// looping through All Products
					for (int i = 0; i < JAllFandecks.length(); i++) {
						JSONObject c = JAllFandecks.getJSONObject(i);
						final String FName = c.getString(TAG_FANDECK_NAME);
						runOnUiThread(new Runnable() {
							public void run() {
								tvFandeckDesc.setText(FName);
							}
						});
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}