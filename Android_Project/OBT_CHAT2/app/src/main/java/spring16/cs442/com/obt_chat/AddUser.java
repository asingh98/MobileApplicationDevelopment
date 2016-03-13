package spring16.cs442.com.obt_chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;



class CustomAdapter extends ArrayAdapter<Status> {
    ArrayList<Status> lstResult;
    ListView lstStatus;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Activity actSelected, int resource, ArrayList<Status> arrlstAllStatuss, ListView lstStatus) {
        super(actSelected, resource);
        this.lstStatus = lstStatus;
        lstResult=arrlstAllStatuss;
        context=actSelected;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return lstResult.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        ImageView img;
        TextView lblName;
        RadioButton radStatus;
    }
    @Override
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
    }
}

public class AddUser extends AppCompatActivity {

    ListView lvStatus;
    Context context;
    CustomAdapter customAdaptor;

    public static ArrayList<Status> arrlstAllStatus = new ArrayList<>();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_user);
        this.setTitle ("Create User");

        lvStatus=(ListView) findViewById(R.id.lvStatus);
        populateStatusListView ();
        customAdaptor = new CustomAdapter(this, R.layout.activity_ind_status, arrlstAllStatus, lvStatus);
        lvStatus.setChoiceMode (ListView.CHOICE_MODE_MULTIPLE);
        lvStatus.setAdapter (customAdaptor);
    }

    private void populateStatusListView()  {
        String [] arrStatusText = {"Available","Busy",
                "Offline"};

        int [] imgNames = {R.drawable.available2, R.drawable.busy2,
                R.drawable.unavailable2};

        for(int i=0; i<arrStatusText.length;i++) {
            setStatusDetails (arrStatusText[i], imgNames[i]);
        }
    }
    public void goToPairDevices(View view){
        Intent intent = new Intent(this, PairDevices.class);
        startActivity(intent);
    }
    public void setStatusDetails (String name, int imgName) {
        Status curStatus = new Status();
        curStatus.strStatusText = name;
        curStatus.strImgName = imgName;
        curStatus.isSelected = false;

        arrlstAllStatus.add (curStatus);
    }
}

class Status implements Serializable {
    public int strImgName;
    public String strStatusText;
    public boolean isSelected=false;
}