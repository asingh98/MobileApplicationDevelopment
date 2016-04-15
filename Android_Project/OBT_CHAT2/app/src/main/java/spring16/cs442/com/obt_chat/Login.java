package spring16.cs442.com.obt_chat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
	final String dbName="otb";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView(R.layout.activity_login);
		OTBDBAdapter otbdbA = new OTBDBAdapter (this, dbName, null, 1);
		SQLiteDatabase db = otbdbA.getWritableDatabase();
		db.close();
		TextView tvCreateUser = (TextView)findViewById (R.id.tvCreateUser);
		if (tvCreateUser!=null) {
			tvCreateUser.setOnClickListener (new View.OnClickListener () {
				public void onClick (View v) {
					Intent intent = new Intent (getBaseContext (), CreateUser.class);
					startActivity (intent);
				}
			});
		}

		TextView tvSkipLogin = (TextView)findViewById (R.id.tvSkipLogin);
		if (tvSkipLogin!=null) {
			tvSkipLogin.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					Intent intent = new Intent (getBaseContext (), MainActivity.class);
					startActivity (intent);
				}
			});
		}

		Button btnLogin = (Button) findViewById (R.id.btnLogin);
		if (btnLogin!=null) {
			btnLogin.setOnClickListener (new View.OnClickListener () {
				@Override
				public void onClick (View v) {
					final EditText etUserName = (EditText) findViewById (R.id.etUserName);
					final EditText etPassword = (EditText) findViewById (R.id.etPassword);

					if ((etUserName.getText ().toString ().equals ("")) || (etUserName.getText ().toString ().isEmpty ())) {
						Toast.makeText (getApplicationContext (), "User Name cannot be blank", Toast.LENGTH_LONG).show ();
						Log.i (this.getClass ().getName (), "User Name cannot be blank");
						etUserName.requestFocus ();
						return;
					}

					if ((etPassword.getText ().toString ().equals ("")) || (etPassword.getText ().toString ().isEmpty ())) {
						Toast.makeText (getApplicationContext (), "Password cannot be blank", Toast.LENGTH_LONG).show ();
						Log.i (this.getClass ().getName (), "Password cannot be blank");
						etPassword.requestFocus ();
						return;
					}

					OTBDBAdapter myDBAdapter = new OTBDBAdapter (getApplicationContext (), dbName, null, 1);
					switch (myDBAdapter.validateUserPassword (etPassword.getText ().toString (), etUserName.getText ().toString ())) {
						case -1:
							Toast.makeText (getApplicationContext (), "User does not exist", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "User does not exist");
							break;
						case -2:
							Toast.makeText (getApplicationContext (), "Invalid Password", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Invalid Password");
							break;
						case 0:
							myDBAdapter.updateUserLastLogin (etUserName.getText ().toString ());
							//code to display the next activity using intent
							Log.i (this.getClass ().getName (), "Login successful");
							Intent intent = new Intent (getBaseContext (), ChatList.class);
							startActivity(intent);
							break;
					}
				}
			});
		}
	}
}
