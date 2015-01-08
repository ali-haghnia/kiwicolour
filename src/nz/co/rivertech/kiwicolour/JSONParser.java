package nz.co.rivertech.kiwicolour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class JSONParser extends Activity {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		// Making HTTP request
		try {

			// check for request method
			if (method == "POST") {
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();

			} else if (method == "GET") {
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}

		} catch (UnsupportedEncodingException e) {
			Log.e("JSON PARSER", "GET OR POST");
			Toast.makeText(getApplicationContext(),
					"Something is wrong with network!", Toast.LENGTH_LONG)
					.show();
			json = "";
			create_JSon_Object();
		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(),
					"Something is wrong with network!", Toast.LENGTH_LONG)
					.show();
			Log.e("JSON PARSER", "GET OR POST");
			json = "";
			return create_JSon_Object();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),
					"Something is wrong with network!", Toast.LENGTH_LONG)
					.show();
			Log.e("JSON PARSER IO ERROR", "GET OR POST");
			json = "";
			return create_JSon_Object();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"Something is wrong with network!", Toast.LENGTH_LONG)
					.show();
			Log.e("JSON PARSER Buffer Error", "Error converting result ");
			json = "";
			return create_JSon_Object();
		}
		return create_JSon_Object();

	}

	private JSONObject create_JSon_Object() {
		// try parse the string to a JSON object
		try {
			// jObj = new JSONObject(json);
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),
					"Something is wrong with network!", Toast.LENGTH_LONG)
					.show();
			Log.e("JSON Parser", "Error parsing data " + e.toString());
			return jObj; // Jadid ezaf kardam ta maloom beshe error daade
							// ............................................
		}

		// return JSON String
		return jObj;
	}
}
