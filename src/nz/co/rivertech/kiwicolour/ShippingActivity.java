package nz.co.rivertech.kiwicolour;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShippingActivity extends Activity implements OnClickListener {

	// RadioButton rbPickup,rbDeliverTo;
	// EditText etAdress;
	// Button btnCancel,btnDone;
	// RelativeLayout linearAdress;
	//
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.shipping_activity);
	// setTitle("Shipping Options"); // Set Title for activity
	// // initialazie all widgets
	// rbPickup=(RadioButton) findViewById(R.id.rbIntentToPickUpShippingOption);
	// rbDeliverTo = (RadioButton) findViewById(R.id.rbDeliverToShippingOption);
	// etAdress = (EditText) findViewById(R.id.etDeliveryAddressShippingOption);
	// btnCancel = (Button) findViewById(R.id.btnCancel);
	// btnDone = (Button) findViewById(R.id.btnDone);
	// linearAdress =(RelativeLayout) findViewById(R.id.linearAdressOk);
	// linearAdress.setVisibility(View.INVISIBLE);
	//
	// rbDeliverTo.setOnClickListener(this);
	// rbPickup.setOnClickListener(this);
	// btnDone.setOnClickListener(this);
	// btnCancel.setOnClickListener(this);
	//
	// }
	//
	//
	// @Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.rbDeliverToShippingOption:
		// linearAdress.setVisibility(View.VISIBLE);
		// break;
		// case R.id.rbIntentToPickUpShippingOption:
		// linearAdress.setVisibility(View.INVISIBLE);
		// etAdress.setText("");
		// break;
		// case R.id.btnDone:
		// if(rbDeliverTo.isChecked() &&
		// etAdress.getText().toString().equals("")){
		// Toast.makeText(this, "You forgot to give your delivery address!",
		// Toast.LENGTH_LONG).show();
		// }else{
		// // etelaat ro be form e checkout bargardan
		// Intent in = new Intent();
		// in.putExtra("DeliveryAdressKEY", etAdress.getText().toString());
		// // setResult(1, in);
		// // finish();
		//
		// }
		// break;
		// case R.id.btnCancel:
		// // finish();
		// break;
		//
	}
	//
	// }
}
