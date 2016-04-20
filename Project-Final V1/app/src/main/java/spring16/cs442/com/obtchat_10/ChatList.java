package spring16.cs442.com.obtchat_10;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ChatList extends ListActivity {
    private BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> list;
    public ArrayList<String> DeviceList = new ArrayList<String>();
    /*String[] itemname ={
            "Adam",
            "Jane",
            "User1",
            "Friend",
            "Alice",
            "Peter",
            "Ricky",
            "John"
    };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chat_list);


        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
        /*if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1000);
        }*/

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices == null || pairedDevices.size() == 0) {
            showToast("No Paired Devices Found");
        } else {
            list = new ArrayList<BluetoothDevice>();
            list.addAll(pairedDevices);
            for(int i=0;i<list.size();i++){
                DeviceList.add(list.get(i).getName());
            }

        }

        /*this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.chat_list_view,
                R.id.Itemname, DeviceList));
        */

        ArrayAdapter<String> DeviceAdapter = new ArrayAdapter <String>(this, R.layout.chat_list_view,
                R.id.Itemname, DeviceList);

        setListAdapter(DeviceAdapter);

    }
    public void goToPairDevices(View view){
        mBluetoothAdapter.startDiscovery();

        //Intent intent = new Intent(this, BlueToothSetting.class);
        //startActivity(intent);
    }
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        Log.i("clck", "onListItemClick:clicked ");
        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);
        //text.setText("You clicked " + selectedItem + " at position " + position);
        showToast(selectedItem+" "+position);
    }



    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void goToChatWindow(View view){
        //Intent intent = new Intent(this, MainChatWindow.class);
        //startActivity(intent);
    }

}
