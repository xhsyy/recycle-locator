package com.net.finditnow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FINLogin extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load up the layout
		setContentView(R.layout.login);

		// get the button resource in the xml file and assign it to a local variable of type Button
		Button launch = (Button)findViewById(R.id.login_button);

		// this is the action listener
		OnClickListener listener = new OnClickListener() {

			public void onClick(View viewParam) {
				// this gets the resources in the xml file and assigns it to a local variable of type EditText
				EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
				EditText passwordEditText = (EditText) findViewById(R.id.txt_password);

				// the getText() gets the current value of the text box
				// the toString() converts the value to String data type
				// then assigns it to a variable of type String
				String userName = usernameEditText.getText().toString();
				String userPass = passwordEditText.getText().toString();
				final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID); 

				String result = Login.login(phone_id, userName, userPass, getBaseContext());
				Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

				if (result.equals(getString(R.string.login_success))) {
					Intent myIntent = new Intent(getBaseContext(), FINHome.class);
		            startActivity(myIntent);
				}
			}
		};

		launch.setOnClickListener(listener);
	}
}