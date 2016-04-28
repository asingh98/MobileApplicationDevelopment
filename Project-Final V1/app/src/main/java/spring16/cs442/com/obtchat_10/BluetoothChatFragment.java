/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package spring16.cs442.com.obtchat_10;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends AppCompatActivity {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public ColorPicker cp;
    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button mSendFileButton;
    public LinearLayout wrapper;
    TextView con_dev;
    public static TextView mesg;
    public String dev_name = null;
    Context self;

    private String mConnectedDeviceName = null;
    private DiscussArrayAdapter mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;
    public static HashMap<String, String> deviceAddressMap = new HashMap<String, String>();
    public Set<BluetoothDevice> pairedDevices;
    static ArrayList<BluetoothDevice> list;

    String address;
    String name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bluetooth_chat);
        cp = new ColorPicker(this, 0, 0, 0);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();

        }
        ensureDiscoverable();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            list = new ArrayList<BluetoothDevice>();
            list.addAll(pairedDevices);
            for (int i = 0; i < list.size(); i++) {
                //DeviceList.add(list.get(i).getName());
                deviceAddressMap.put(list.get(i).getName(), list.get(i).getAddress());
            }
        }
        mConversationView = (ListView) findViewById(R.id.in);
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendFileButton = (Button) findViewById(R.id.send_file);
        self = this;

    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        //Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new DiscussArrayAdapter(self, R.layout.message_row);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.edit_text_out);
                String message = textView.getText().toString();
                sendMessage(message);

            }
        });

        mSendFileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != deviceAddressMap.get(dev_name)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 44);
                } else {
                    Toast.makeText(self, "Not connected to any device",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(self, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(self, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));

                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //wrapper.setGravity(Gravity.RIGHT);
                    //mesg.setBackgroundResource(R.drawable.bubble_green);
                    mConversationArrayAdapter.add(new MessageObj(false, "Me:  " + writeMessage));
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //wrapper.setGravity(Gravity.LEFT);
                    //mesg.setBackgroundResource(R.drawable.bubble_yellow);

                    mConversationArrayAdapter.add(new MessageObj(true, mConnectedDeviceName + ":  " + readMessage));

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
					 dev_name = mConnectedDeviceName;
                    if (null != self) {
                        Toast.makeText(self, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        if(new Login().blnEnableHistory) {
                            OTBDBAdapter objOTBDBAdapter = new OTBDBAdapter(self, new Login().dbName, null, 1);
                            objOTBDBAdapter.insertUserListTable(address,new Login().logInUser,name);
                        }
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    //if (null != activity) {
                    Toast.makeText(self, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    //}
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    //Log.d(TAG, "BT not enabled");
                    Toast.makeText(self, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
                break;
            case 44:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String realPath;
                    // SDK < API11
                    if (Build.VERSION.SDK_INT < 11)
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                        // SDK >= 11 && SDK < 19
                    else if (Build.VERSION.SDK_INT < 19)
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                        // SDK > 19 (Android 4.4)
                    else
                        realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());


                    //setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(),realPath);


                    //Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_LONG).show();


                    Uri uritoSend = Uri.fromFile(new File(realPath));
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");


                    //String uri = uritoSend;
                    intent.putExtra(Intent.EXTRA_STREAM, uritoSend);
                    startActivity(intent);

                }
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.chatFragment);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1888) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Drawable d = Drawable.createFromPath(destination.getPath());
                layout.setBackgroundDrawable(d);


                //ivImage.setImageBitmap(thumbnail);
            } else if (requestCode == 79) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                Drawable d = Drawable.createFromPath(selectedImagePath);
                layout.setBackgroundDrawable(d);

            }
        }
    }

    /*
      Establish connection with other divice

      @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
      @param secure Socket Security type - Secure (true) , Insecure (false)
     */

    public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        //String address = data.getExtras()
        //       .getString(Device_List.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object

        //String address = adr;    //Get it from chat list
        //setTitle(data.getStringExtra("Name"));
        //ActionBar ab = MainActivity.class.getActionBar();
        //MainActivity.getActionBar().setTitle(data.getStringExtra("Name"));
        //ab.setTitle("My Title");
        //ab.setSubtitle("sub-title");
        String dev_address = data.getStringExtra("addr");
        name = data.getStringExtra("Name");

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(dev_address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflater.inflate(R.menu.device_list_menu, menu);
        getMenuInflater().inflate(R.menu.device_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_search:
                //Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT)
                //        .show();
                Intent serverIntent = new Intent(self, Device_List.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                selectImage();
                break;
            default:
                break;
        }

        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Choose Color","Take Photo", "Choose from Library", "Blue", "Colours", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothChatFragment.this);
        builder.setTitle("Change Background!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(items[item].equals("Choose Color")) {
                    cp.show();

/* On Click listener for the dialog, when the user select the color */
                    Button okColor = (Button)cp.findViewById(R.id.okColorButton);

                    okColor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

        /* You can get single channel (value 0-255) */
                            //selectedColorR = cp.getRed();
                            //selectedColorG = cp.getGreen();
                            //selectedColorB = cp.getBlue();

        /* Or the android RGB Color (see the android Color class reference) */
                            //selectedColorRGB = cp.getColor();
                            LinearLayout layout = (LinearLayout) findViewById(R.id.chatFragment);
                            layout.setBackgroundColor(cp.getColor());

                            cp.dismiss();
                        }
                    });
                }
                else if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1888);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"), 79);
                } else if (items[item].equals("Blue")) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.chatFragment);
                    layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
                } else if (items[item].equals("Colours")) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.chatFragment);
                        layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.background3));
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LinearLayout layout = (LinearLayout) findViewById(R.id.chatFragment);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1888) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Drawable d = Drawable.createFromPath(destination.getPath());
                layout.setBackgroundDrawable(d);


                //ivImage.setImageBitmap(thumbnail);
            } else if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                Drawable d = Drawable.createFromPath(selectedImagePath);
                layout.setBackgroundDrawable(d);

            }
        }
    }*/


    //Trying new adapter

    public class DiscussArrayAdapter extends ArrayAdapter<MessageObj> {

        //private TextView countryName;
        private List<MessageObj> countries = new ArrayList<MessageObj>();
        //private LinearLayout wrapper;
        ViewHolder viewHolder;

        @Override
        public void add(MessageObj object) {
            countries.add(object);
            super.add(object);
        }

        private class ViewHolder
        {
            TextView countryName;
            LinearLayout wrapper;
        }

        public DiscussArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public int getCount() {
            return this.countries.size();
        }

        public MessageObj getItem(int index) {
            return this.countries.get(index);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.message_row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.countryName = (TextView) row.findViewById(R.id.comment);
                viewHolder.wrapper = (LinearLayout) row.findViewById(R.id.wrapper);
                row.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) row.getTag();
            }

            //viewHolder.wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

            MessageObj coment = getItem(position);

            //countryName = (TextView) row.findViewById(R.id.comment);

            viewHolder.countryName.setText(coment.comment);

            viewHolder.countryName.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
            viewHolder.wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);

            return row;
        }

        public Bitmap decodeToBitmap(byte[] decodedByte) {
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }

    }


    public class MessageObj {
        public boolean left;
        public String comment;

        public MessageObj(boolean left, String comment) {
            super();
            this.left = left;
            this.comment = comment;
        }

    }

}
