package spring16.cs442.com.obt_chat;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Divya on 3/11/2016.
 */
public class PairDevices extends Activity {

    /**
     * Tag for Log
     */
    private static final String TAG = "DeviceListActivityPair";

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;

    /**
     * Newly discovered devices
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    ListView lv;
    String[] devices = {
            "Device 1",
            "Device 2",
            "Device 3"
    };
    Integer[] imageId = {
            R.drawable.linkdevice,
            R.drawable.adddevice,
            R.drawable.linkdevice
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_devices);
        lv = (ListView) findViewById(R.id.devices_list);
        DeviceApdapter adapter = new
                DeviceApdapter(PairDevices.this, devices, imageId);
        lv.setAdapter(adapter);
    }

    private class DeviceApdapter extends ArrayAdapter {
        private final Activity context;
        private final String[] devices;
        private final Integer[] imageId;

        public DeviceApdapter(Activity context,
                              String[] devices, Integer[] images) {
            super(context, R.layout.devices_list, devices);
            this.context = context;
            this.devices = devices;
            this.imageId = images;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.devices_list, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.device_name);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.device_status);
            txtTitle.setText(devices[position]);

            imageView.setImageResource(imageId[position]);
            return rowView;
        }
    }
}
