package nz.co.rivertech.kiwicolour;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity {
	Button btnUpdate;
	String CurrentVersion = "";
	String strVersion = "";
	JSONParser jParser = new JSONParser();
	private static String url_get_version = "http://rivertech.co.nz/kiwicolour/androidphp/get_version.php";
	JSONArray latestVersion = null;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_VERSION = "version";
	ProgressBar wheel;
	private ProgressDialog pDialog;
	// Progress dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;
	private static String file_url = "http://rivertech.co.nz/kiwicolour/dl/kiwicolour.apk";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		setTitle("About");
		Global.globalVariable = "ABOUT";
		wheel = (ProgressBar) findViewById(R.id.progressBarCheckNewVersionLogin);

		TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
		try {
			tvVersion.setText(String
					.valueOf(
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName).trim());
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		TextView tvDesigned = (TextView) findViewById(R.id.tvDesigned);
		String sourceString = "Designed and developed for River City Decorators Limited";
		tvDesigned.setText(Html.fromHtml(sourceString));

		final TextView tvHttpDesign = (TextView) findViewById(R.id.tvHttpRiverTech);
		sourceString = "<U> KiwiColour.RiverTech.co.nz <U>";
		tvHttpDesign.setText(Html.fromHtml(sourceString));

		TextView tvProjectManager = (TextView) findViewById(R.id.tvProjectManager);
		sourceString = "Android Developer: Ali R. Haghnia";
		tvProjectManager.setText(Html.fromHtml(sourceString));

		sourceString = "Shopping Cart Icons: DryIcons";

		final TextView tvUserManual = (TextView) findViewById(R.id.tvUserManual);
		sourceString = "User Manual...";
		tvUserManual.setText(Html.fromHtml(sourceString));

		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// check for new version
				try {
					CurrentVersion = String.valueOf(
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName).trim();
					Boolean internetIsPresent = null;
					try {
						ConnectionDetector cd = new ConnectionDetector(
								getApplicationContext());
						internetIsPresent = cd.isConnectingToInternet();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (internetIsPresent) {
						new LoadVersion().execute(); // to check in server
														// version
					} else {
						Toast.makeText(getApplicationContext(),
								"Connection Lost!", Toast.LENGTH_LONG).show();
					}
					// }
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		Button btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tvHttpDesign.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;

				case MotionEvent.ACTION_MOVE:
					// touch move code
					break;

				case MotionEvent.ACTION_UP:
					String url = "http://rivertech.co.nz/web/kiwicolour/painters/";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
					break;
				}
				return true;
			}
		});

		tvUserManual.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		tvUserManual.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					tvUserManual.setTextColor(Color.YELLOW);
					break;

				case MotionEvent.ACTION_MOVE:
					// touch move code
					break;

				case MotionEvent.ACTION_UP:

					tvUserManual.setTextColor(Color.DKGRAY);
					String url = "http://rivertech.co.nz/web/kiwicolour/painters/";
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
					break;
				}
				return true;
			}
		});
	}

	// code e asynctask marboot be load shodane listview
	class LoadVersion extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			wheel.setVisibility(View.VISIBLE);
			btnUpdate.setEnabled(false);
		}

		/**
		 * getting my orders from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_get_version, "GET",
					params);
			if (json == null) {
				try {
					throw new JSONException("");
				} catch (JSONException e) {
					return "";
				}
			}
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					strVersion = json.getString(TAG_VERSION);
				}
			} catch (Exception e) {
				return "";
			}
			return strVersion;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(final String result) {
			wheel.setVisibility(View.INVISIBLE);
			btnUpdate.setEnabled(true);
			if (!result.equals("")) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (Float.valueOf(CurrentVersion) < Float
								.valueOf(result)) {
							String PATH = Environment
									.getExternalStorageDirectory()
									+ "/KiwiColour/";
							File file = new File(PATH + "kiwicolour-v" + result
									+ ".apk");
							if (file.exists()) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setDataAndType(
										Uri.fromFile(new File(PATH
												+ "kiwicolour-v" + result
												+ ".apk")),
										"application/vnd.android.package-archive");
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);

							} else {
								showAlertDialog(
										AboutActivity.this,
										"New version",
										"New version is available.\nDo you want to download it now?",
										false);
							}
						} else {
							Toast.makeText(getApplicationContext(),
									"You already have the latest version",
									Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		}
	}// payan e asynctask

	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				new DownloadFileFromURL().execute(file_url);

			}
		});

		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	};

	/**
	 * Showing Dialog
	 * */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case progress_bar_type:
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}

	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				// getting file length
				int lenghtOfFile = conection.getContentLength();

				// input stream to read file - with 8k buffer
				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				String PATH = Environment.getExternalStorageDirectory()
						+ "/KiwiColour/";
				File file = new File(PATH);
				file.mkdirs();
				// Output stream to write file
				OutputStream output = new FileOutputStream(PATH
						+ "kiwicolour-v" + strVersion + ".apk");

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));

					// writing data to file
					output.write(data, 0, count);
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

			return null;
		}

		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			pDialog.setProgress(Integer.parseInt(progress[0]));
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			dismissDialog(progress_bar_type);

			// Displaying downloaded image into image view
			// Reading image path from sdcard
			String PATH = Environment.getExternalStorageDirectory()
					+ "/KiwiColour/";
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.fromFile(new File(PATH + "kiwicolour-v" + strVersion
							+ ".apk")),
					"application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

	}

}