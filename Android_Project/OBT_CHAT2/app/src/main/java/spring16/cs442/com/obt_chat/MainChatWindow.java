package spring16.cs442.com.obt_chat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainChatWindow extends AppCompatActivity {
    ListView ls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat_window);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.ChatFragment, fragment);
            transaction.commit();
        }
        /*ls = (ListView)findViewById(R.id.myList);
        ArrayList<String> my_array =new ArrayList<String>();
        my_array.add("Hi..");
        my_array.add("How are you??");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_row_layout_even,R.id.text, my_array);
        ls.setAdapter(adapter);*/
    }

}
