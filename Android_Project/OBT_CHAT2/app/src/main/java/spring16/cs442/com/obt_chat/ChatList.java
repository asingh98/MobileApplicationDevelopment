package spring16.cs442.com.obt_chat;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ChatList extends ListActivity {
    private ArrayList<BluetoothDevice> mDeviceList;
    ListView dList;
    ArrayList<String> Devices =new ArrayList<String>();
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    Context self = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDeviceList		= getIntent().getExtras().getParcelableArrayList("device.list");

        for(int i=0;i<mDeviceList.size();i++){
            Devices.add(mDeviceList.get(i).getName().toString());
        }

        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.chat_list_view,
                R.id.Itemname, Devices));

        dList = getListView();

        dList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v,
                                    int arg2, long arg3) {
                // When clicked, show a toast with the TextView text
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length() - 17);

                // Create the result Intent and include the MAC address
                Intent intent = new Intent(self, MainChatWindow.class);
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

                // Set result and finish this Activity

            }
        });


    }


    public void goToPairDevices(View view){
        Intent intent = new Intent(this, BlueToothSetting.class);
        startActivity(intent);
    }

    public void goToChatWindow(View view){
        Intent intent = new Intent(this, MainChatWindow.class);
        startActivity(intent);
    }



}
