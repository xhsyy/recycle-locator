package com.net.finditnow;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FINLogin extends FINActivity {

	private ProgressDialog myDialog;
	private String result;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.app_name) + " > SuperUser Login");

		// load up the layout
		setContentView(R.layout.login);

		// Add link to help info:
		TextView link = (TextView) findViewById(R.id.superuser_link);
		link.setText(Html.fromHtml("<a href=\"http://www.fincdn.org/superuser.php\">FIN SuperUser Application</a>"));
		link.setMovementMethod(LinkMovementMethod.getInstance()); 

		// get the button resource in the xml file and assign it to a local variable of type Button
		Button launch = (Button)findViewById(R.id.login_button);
		Button launch2 = (Button)findViewById(R.id.cancel_button);

		// this is the action listener
		OnClickListener listener = new OnClickListener() {

			public void onClick(View viewParam) {
				myDialog = ProgressDialog.show(FINLogin.this, "" , "Logging in...", true);
				Thread thread = new Thread() {
					@Override
					public void run() {
						// this gets the resources in the xml file and assigns it to a local variable of type EditText
						EditText usernameEditText = (EditText) findViewById(R.id.txt_username);
						EditText passwordEditText = (EditText) findViewById(R.id.txt_password);

						// the getText() gets the current value of the text box
						// the toString() converts the value to String data type
						// then assigns it to a variable of type String
						String userName = usernameEditText.getText().toString();
						String userPass = passwordEditText.getText().toString();
						final String phone_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID); 

						// Check connection of Android
						ConnectionChecker conCheck = new ConnectionChecker(getBaseContext(), FINLogin.this);

						result = DBCommunicator.login(phone_id, userName, userPass, getBaseContext());
						myDialog.dismiss();
						if (result.equals(getString(R.string.timeout))) {
							conCheck.connectionError();
						} else {
							handler.sendEmptyMessage(0);
						}
					}
				};
				thread.start();
			}
		};

		OnClickListener listener2 = new OnClickListener() {

			public void onClick(View viewParam) {
				setResult(RESULT_CANCELED);
				finish();
			}
		};

		launch.setOnClickListener(listener);
		launch2.setOnClickListener(listener2);
	}

	private Handler handler = new Handler() {
		@Override
		public void  handleMessage(Message msg) {
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

			if (result.equals(getString(R.string.login_success)) || result.contains(getString(R.string.login_already))) {
				FINHome.setLoggedIn(true);

				setResult(RESULT_OK);
				finish();
			} 
		}
	};
}