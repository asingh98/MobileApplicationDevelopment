package spring16.cs442.com.obt_chat;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

public class ChatList extends ListActivity {

    String[] itemname ={
            "Adam",
            "Jane",
            "User1",
            "Friend",
            "Alice",
            "Peter",
            "Ricky",
            "John"
    };
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
        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.chat_list_view,
                R.id.Itemname, itemname));
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
