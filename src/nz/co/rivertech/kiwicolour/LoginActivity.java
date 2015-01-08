package nz.co.rivertech.kiwicolour;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERID = "id";
	private static final String TAG_MESSAGE = "message";
	private static String url_find_user_id = "http://rivertech.co.nz/kiwicolour/androidphp/login.php";
	private static String url_get_version = "http://rivertech.co.nz/kiwicolour/androidphp/get_version.php";
	private static final String TAG_VERSION = "version";
	JSONParser jParser = new JSONParser();
	TextView tvNewVersion;
	EditText etUser, etPass;
	Button btnLogin, btnLost, btnRegister;
	String CurrentVersion, strUserId, strERR = "";
	ProgressBar progressLogin;
	Boolean LoginDone = false;
	CheckBox CheckBoxRememberPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		setTitle("Login");

		CheckBoxRememberPassword = (CheckBox) findViewById(R.id.checkBoxRememberPass);
		tvNewVersion = (TextView) findViewById(R.id.tvNewVersion);
		tvNewVersion.setOnClickListener(this);
		try {
			CurrentVersion = String
					.valueOf(
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName).trim();
			new LoadVersion().execute(); // to check in server version

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		progressLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

		etUser = (EditText) findViewById(R.id.etUserName);
		etPass = (EditText) findViewById(R.id.etPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLost = (Button) findViewById(R.id.btnLostPass);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		btnLogin.setOnClickListener(this);
		btnLost.setOnClickListener(this);
		btnRegister.setOnClickListener(this);

		SharedPreferences set = PreferenceManager.getDefaultSharedPreferences(this);
		boolean boolEmail = set.getBoolean("emailchk", true);
		boolean boolPass = set.getBoolean("passwordchk", false);
		if(boolPass){
			CheckBoxRememberPassword.setChecked(true);
		}else{
			CheckBoxRememberPassword.setChecked(false);
		}
		saveEmailPass(boolEmail, boolPass);

		if (etUser.getText().equals("")) {
			etUser.setFocusable(true);
			etUser.requestFocus();
		} else {
			etPass.setFocusable(true);
			etPass.requestFocus();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			// CHECK INTERNET CONNECTION
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());
			Boolean internetIsPresent = cd.isConnectingToInternet();
			if (!internetIsPresent) {
				showAlertDialog(this, "No Internet Connection !",
						"You need internet connection to start.", false);
				// END OF CHECK INTERNET CONNECTION
			} else {
				// LOGIN CHECK

				// sent data to server to check
				if (!etUser.getText().toString().equals("") && !etPass.getText().toString().equals("")) {
					new getUserId().execute();
				} else if (etUser.getText().toString().equals("") || etPass.getText().toString().equals("")) {
					Toast.makeText(this, "Please insert email and password.",Toast.LENGTH_LONG).show();
					if (etUser.getText().toString().equals("")) {
						etUser.setFocusable(true);
						etUser.requestFocus();
					} else {
						etPass.setFocusable(true);
						etPass.requestFocus();
					}
				} else {
					Toast.makeText(this, "Email or password is wrong!",Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.btnLostPass:
			Toast.makeText(this, "Not available in this version...",
					Toast.LENGTH_SHORT).show();
			break;

		case R.id.btnRegister:
			Toast.makeText(this, "Not available in this version...",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.tvNewVersion:
			Log.i("tvnewversion", tvNewVersion.getText().toString());
			if (!(tvNewVersion.getText().toString() == "")) {
				Intent i = new Intent(this, AboutActivity.class);
				startActivity(i);

			}
			break;
		}

	}

	private void saveEmailPass(boolean be, boolean bp) {
		if (be) {
			SharedPreferences settings = getSharedPreferences("MYPREFS", 0);
			etUser.setText(settings.getString("emailvalue", ""));
		} else {
			etUser.setText("");
		}

		if (bp) {
			SharedPreferences settings = getSharedPreferences("MYPREFS", 0);
			etPass.setText(settings.getString("passvalue", ""));
			CheckBoxRememberPassword.setChecked(true);
		} else {
			etPass.setText("");
			CheckBoxRememberPassword.setChecked(false);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (LoginDone) {
			SharedPreferences settings = getSharedPreferences("MYPREFS", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("emailvalue", etUser.getText().toString());
			editor.putString("passvalue", etPass.getText().toString());
			editor.putString("painterid", strUserId);
			// editor.putString("orderQuantity", "");
			editor.apply();
			editor.commit();
			Global.PainterID = strUserId;
			// Log.i("painterid: on stop login:",Global.PainterID);
			if(CheckBoxRememberPassword.isChecked()){
				SharedPreferences setpref = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor edit = setpref.edit();
				edit.putBoolean("passwordchk", true);
				edit.commit();
			}else{
				SharedPreferences setpref = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor edit = setpref.edit();
				edit.putBoolean("passwordchk", false);
				edit.commit();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting alert dialog icon
		alertDialog.setIcon((status) ? R.drawable.green : R.drawable.red);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	};

	/**
	 * Background Async Task to get user id
	 * */
	
class getUserId extends AsyncTask<String, String, String> {
		boolean blnUserIdFounded = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// waiting process ON...
			btnLogin.setEnabled(false);
			etUser.setEnabled(false);
			etPass.setEnabled(false);
			btnLogin.setText("Please Wait...");
			progressLogin.setVisibility(View.VISIBLE);
		}

		/**
		 * finding userid
		 * */
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", etUser.getText()
					.toString()));
			params.add(new BasicNameValuePair("password", etPass.getText()
					.toString()));
			strERR = "";
			strUserId = "";
			// getting JSON Object
			JSONObject json = null;
			try {
				json = jParser.makeHttpRequest(url_find_user_id, "GET", params);
				if (json.getInt(TAG_SUCCESS) == 1
						|| json.getInt(TAG_SUCCESS) == 0) {
					// check log cat fro response
					Log.d("Login info:", json.toString());
					// check for success tag
					try {
						blnUserIdFounded = (json.getInt(TAG_SUCCESS) == 1) ? true
								: false;
						if (blnUserIdFounded) {
							strUserId = json.getString(TAG_USERID);
							strERR = "";
						} else {
							Log.d("Login info:", json.getString(TAG_MESSAGE));
							strERR = "Invalid email or password";
							strUserId = "";
						}
					} catch (JSONException e) {
						Log.e("NETWORK ERROR", "json error");
						return "0";

					}
				}
			} catch (Exception e1) {
				Log.e("NETWORK ERROR", "e1");
				return "0";
			}
			return "1";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == "1") {
				runOnUiThread(new Runnable() {
					public void run() {
						if (strERR.equals("")) {
							if (!strUserId.equals("")) {
								LoginDone = true;
								finish();
								Global.globalVariable = "LOGIN";
								Global.PainterID = strUserId;
								Global.Email = etUser.getText().toString();
								Intent in = new Intent(getApplicationContext(),
										HomeActivity.class);
								in.putExtra("ACTIVITY", "LOGIN");
								startActivity(in);
								overridePendingTransition(R.anim.push_left_in,
										R.anim.push_up_out);
							}
						} else {
							Toast.makeText(getApplicationContext(), strERR,
									Toast.LENGTH_LONG).show();
							etPass.requestFocus();
							// waiting process OFF...
							btnLogin.setText("Login");
							btnLogin.setEnabled(true);
							etUser.setEnabled(true);
							etPass.setEnabled(true);
							progressLogin.setVisibility(View.INVISIBLE);
						}
					}
				});
			} else if (result == "0") {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Something is wrong with network",
								Toast.LENGTH_LONG).show();
						btnLogin.setText("Login");
						btnLogin.setEnabled(true);
						etUser.setEnabled(true);
						etPass.setEnabled(true);
						progressLogin.setVisibility(View.INVISIBLE);
					}
				});
			}
		}
	}

	class LoadVersion extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNewVersion.setVisibility(View.VISIBLE);
			tvNewVersion.setText("");
		}

		protected String doInBackground(String... args) {
			// Building Parameters
			String strVersion = "";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = null;
			try {
				json = jParser.makeHttpRequest(url_get_version, "GET", params);
				if (!(json.length() == 0)) {
					try {
						int success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							strVersion = json.getString(TAG_VERSION);
						}
					} catch (Exception e) {
						return "";
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (json == null) {
				try {
					throw new JSONException("");
				} catch (JSONException e) {
					return "";
				}
			}

			return strVersion;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(final String result) {
			if (!result.equals("")) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (Float.valueOf(CurrentVersion) < Float
								.valueOf(result)) {
							tvNewVersion
									.setText("Tap to update to new version "
											+ String.valueOf(result));
						}
					}
				});
			}
		}
	}// payan e asynctask

}
