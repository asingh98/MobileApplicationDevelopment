package spring16.cs442.com.obtchat_10;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
class CustomAdapter extends ArrayAdapter<Status> {
	//ArrayList<Status> lstResult;
	//ListView lstStatus;
	Context context;
	private static LayoutInflater inflater=null;
	public CustomAdapter(Activity actSelected, int resource, ArrayList<Status> arrlstAllStatuss, ListView lstStatus) {
		super(actSelected, resource);
		//this.lstStatus = lstStatus;
		//lstResult=arrlstAllStatuss;
		context=actSelected;
		inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	*/
/*@Override
	public int getCount() {
		return lstResult.size();
	}*//*


	*/
/*@Override
	public long getItemId(int position) {
		return position;
	}*//*


	*/
/*public class Holder
	{
		ImageView img;
		TextView lblName;
		RadioButton radStatus;
	}*//*


	*/
/*@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder=new Holder();
		final View rowView;
		final Status curStatus = lstResult.get(position);
		rowView = inflater.inflate(R.layout.activity_ind_status, null);
		holder.img=(ImageView) rowView.findViewById (R.id.ivStatus);
		holder.lblName=(TextView) rowView.findViewById(R.id.tvStatusText);
		holder.radStatus=(RadioButton) rowView.findViewById(R.id.radStatus);
		holder.lblName.setText (curStatus.strStatusText);
		holder.img.setImageResource (curStatus.strImgName);
		return rowView;
	}*//*

}
*/

public class CreateUser extends AppCompatActivity {

	//ListView lvStatus;
	Context context;
	//CustomAdapter customAdaptor;
	final String dbName="otb";

	//public static ArrayList<Status> arrlstAllStatus = new ArrayList<>();

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_create_user);
		this.setTitle ("Create User");

		/*lvStatus=(ListView) findViewById(R.id.lvStatus);
		populateStatusListView ();
		customAdaptor = new CustomAdapter(this, R.layout.activity_ind_status, arrlstAllStatus, lvStatus);
		lvStatus.setChoiceMode (ListView.CHOICE_MODE_MULTIPLE);
		lvStatus.setAdapter (customAdaptor);*/

		Button btnCancel = (Button) findViewById (R.id.btnCancel);
		if (btnCancel!=null) {
			btnCancel.setOnClickListener (new View.OnClickListener () {
				@Override
				public void onClick (View v) {
					onBackPressed ();
				}
			});
		}

        Button btnCreateUser = (Button) getWindow().findViewById(R.id.btnCreateUser);
        if (btnCreateUser != null) {
            btnCreateUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText etUserName = (EditText) getWindow().findViewById(R.id.etUserName);
                    if (etUserName != null) {
                        if ((etUserName.getText().toString().equals("")) || (etUserName.getText().toString().isEmpty())) {
                            Toast.makeText(getApplicationContext(), "User Name cannot be blank", Toast.LENGTH_LONG).show();
                            Log.i(this.getClass().getName(), "User Name cannot be blank");
                            etUserName.requestFocus();
                            return;
                        }
                    }

					final EditText etPassword = (EditText) findViewById (R.id.etPassword);
					if (etPassword!=null) {
						if ((etPassword.getText ().toString ().equals ("")) || (etPassword.getText ().toString ().isEmpty ())) {
							Toast.makeText (getApplicationContext (), "Password cannot be blank", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Password cannot be blank");
							etPassword.requestFocus ();
							return;
						}
					}

					final EditText etConfirmPassword = (EditText) findViewById (R.id.etConfirmPassword);
					if (etConfirmPassword!=null) {
						if ((etConfirmPassword.getText ().toString ().equals ("")) || (etConfirmPassword.getText ().toString ().isEmpty ())) {
							Toast.makeText (getApplicationContext (), "Confirm Password cannot be blank", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Confirm Password cannot be blank");
							etConfirmPassword.requestFocus ();
							return;
						}
					}

					if ((etPassword!=null) && (etConfirmPassword!=null)) {
						if (!(etConfirmPassword.getText ().toString ().equals (etPassword.getText ().toString ()))) {
							Toast.makeText (getApplicationContext (), "Confirm Password and Password are not same", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Confirm Password and Password are not same");
							return;
						}
					}

					final EditText etUserFullName = (EditText) findViewById (R.id.etUserFullName);
					if (etUserFullName!=null) {
						if ((etUserFullName.getText ().toString ().equals ("")) || (etUserFullName.getText ().toString ().isEmpty ())) {
							Toast.makeText (getApplicationContext (), "User Full Name cannot be blank", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "User Full Name cannot be blank");
							etUserFullName.requestFocus ();
							return;
						}
					}

					final EditText etEmailID = (EditText) findViewById (R.id.etEmailID);
					if (etEmailID!=null) {
						if ((etEmailID.getText ().toString ().equals ("")) || (etEmailID.getText ().toString ().isEmpty ())) {
							Toast.makeText (getApplicationContext (), "Email ID cannot be blank", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Email ID cannot be blank");
							etEmailID.requestFocus ();
							return;
						}

						if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(etEmailID.getText ().toString ()).matches()))
						{
							Toast.makeText (getApplicationContext (), "Email ID is in incorrect format", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Email ID is in incorrect format");
							etEmailID.requestFocus ();
							return;
						}
					}

					if ((etUserFullName!=null) && (etPassword!=null) && (etUserName!=null) && (etEmailID!=null)) {
						OTBDBAdapter myDBAdapter = new OTBDBAdapter (getApplicationContext (), dbName, null , 1);
						long lngReturn = myDBAdapter.insertUser (etUserFullName.getText ().toString (), etPassword.getText ().toString (),
								etUserName.getText ().toString (), etEmailID.getText ().toString ());
						Log.i("*******************","Value is "+(int) lngReturn);

						if(((int) lngReturn)>0){
							Toast.makeText (getApplicationContext (), "User create succesfully", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "User create succesfully");
							onBackPressed ();
						}else{
							Toast.makeText (getApplicationContext (), "Duplicate User Display Name already exists", Toast.LENGTH_LONG).show ();
							Log.i (this.getClass ().getName (), "Duplicate User Display Name already exists");
						}

					}

				}
			});
		}
	}

/*	private void populateStatusListView()  {
		String [] arrStatusText = {"Available","Busy",
				"Offline"};

		int [] imgNames = {R.drawable.available2, R.drawable.busy2,
				R.drawable.unavailable2};

		for(int i=0; i<arrStatusText.length;i++) {
			setStatusDetails (arrStatusText[i], imgNames[i]);
		}
	}*/

	/*public void setStatusDetails (String name, int imgName) {
		Status curStatus = new Status();
		curStatus.strStatusText = name;
		curStatus.strImgName = imgName;
		curStatus.isSelected = false;

		arrlstAllStatus.add (curStatus);
	}*/
}

/*
class Status implements Serializable {
	public int strImgName;
	public String strStatusText;
	public boolean isSelected=false;
}*/
