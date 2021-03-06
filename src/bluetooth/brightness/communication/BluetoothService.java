package bluetooth.brightness.communication;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import bluetooth.brightness.R;

/**
 * This is the main Activity that displays operations
 */
public class BluetoothService extends Activity {
	// Debugging
	private static final String TAG = "BluetoothService";
	private static final boolean D = true;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;



	//seek bar
	private SeekBar brightness;

	//massage
	private TextView brightness_status;

	private String mConnectedDeviceName = null,
			mac="",
			receiveMessage="";


	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChatService mChatService = null;

	//default intensity
	private int intensity = 0;

	private boolean checkStatus = false;
	private ArrayList<byte[]> messageByte = new ArrayList<byte[]>();
	
	private float brightness_value = 0.0f;


	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.control_screen);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		Intent intent = getIntent();
		mac = intent.getStringExtra("device.address");

		getVariable();
		setupChat();
		connectDevice();
		
		
	
	}


	/*	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				mChatService.start();
			}
		}
	}*/

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		mChatService = new BluetoothChatService(this, mHandler);

	}


	private void sendMessage(String message) {

		byte[] send = message.getBytes();
		mChatService.write(send);

	}

	/**
	 * Received text is passed to this method for checking 
	 * and operation..  
	 * @param readMessage
	 */
	private void recieveMessage(String readMessage) {

		String reciveMessage = readMessage;

		brightness_status.setText(mConnectedDeviceName + ": "+ readMessage);

		brightness_value = Float.valueOf(reciveMessage);
		setBrightness(brightness_value);
		
		receiveMessage = "";
	}

	@Override
	public void onBackPressed() {
		messageByte.clear();
		super.onBackPressed();
	}



	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String writeMessage = "",readMessage ="";
			switch (msg.what) {


			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				messageByte.add(writeBuf);
				writeMessage = new String(messageByte.get(0));
				brightness_status.setText("");
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;

				readMessage = new String(readBuf, 0, msg.arg1);
				String what = msg.what+"";

				recieveMessage(readMessage);


				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	
	

	private void connectDevice() {

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
		mChatService.connect(device);
	}


	public void getVariable(){

		brightness_status = (TextView) findViewById(R.id.status);


		brightness = (SeekBar)findViewById(R.id.level);

		brightness.setMax(10);

		brightness.setProgress(0);

		brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				intensity = progress;
				sendMessage(intensity+"");
			}
		});
	

	}

	public void Finish(){
		startActivity(new Intent(BluetoothService.this,PairedListActivity.class));
	}
	
	
	public void setBrightness(float progress){

		float backLight = (float) progress/100;

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = backLight;
		getWindow().setAttributes(layoutParams);
	}
	
	public void getOperation(String OpMod){
		
		switch ("") {
		case "":
			
			break;

		default:
			break;
		}
	}

}
