package info.ss12.audioalertsystem;

import info.ss12.audioalertsystem.notification.CameraLightNotification;
import info.ss12.audioalertsystem.notification.FlashNotification;
import info.ss12.audioalertsystem.notification.NotificationBarNotification;
import info.ss12.audioalertsystem.notification.VibrateNotification;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Switch;
import android.content.DialogInterface;
import android.content.Intent;

public class MainActivity extends Activity
{
	private final String TAG = "Main Activity";
	private boolean alarmActivated = false;

	
	private Switch micSwitch;
	private Button testAlert;
	
	private ButtonController buttonControl;
	
	private VibrateNotification vibrate;
	private FlashNotification flash;
	private NotificationBarNotification bar;
	private CameraLightNotification cameraLight;
	private Intent intent; //Used for Service
	
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		intent = new Intent(this, LocalService.class);
		buttonControl = new ButtonController(this, intent);
		micSwitch = (Switch )findViewById(R.id.mic_switch);
		micSwitch.setOnClickListener(buttonControl);
		micSwitch.setOnTouchListener(buttonControl);

		testAlert = (Button)findViewById(R.id.test_alert);
		testAlert.setOnClickListener(buttonControl);
		
		vibrate = new VibrateNotification(this);
		flash = new FlashNotification(this);
		bar = new NotificationBarNotification();
		cameraLight = new CameraLightNotification();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) 
		{
			
			if(msg.arg1 == 1 && !alarmActivated) // Turn On
			{
				bar.startNotify();
				flash.startNotify();
				vibrate.startNotify();
				cameraLight.startNotify();
				alarmActivated = true;
				Notification("SS12 Audio Alert","FIRE ALARM DETECTED");
			}
			else if(msg.arg1 == 0 && alarmActivated)
			{
				bar.stopNotify();
				flash.stopNotify();
				vibrate.stopNotify();
				cameraLight.stopNotify();
				alarmActivated = false;
			}
			Log.d(TAG, "FIRE ALARM DETECTED");	
		}
		
	};

	public Handler getHandler() 
	{
		return handler;
	}
	
	public void setHandler(Handler handler) 
	{
		this.handler = handler;
	}
	
	
	/**
	 * Notification Bar messaging.
	 * @param notificationTitle
	 * @param notificationMessage
	 */
    private void Notification(String notificationTitle, String notificationMessage)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.audioalert_icon, "ALERT!!!", System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
 
        notification.setLatestEventInfo(MainActivity.this, notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(10001, notification);
    }

	public boolean isAlarmActivated() 
	{
		return alarmActivated;
	}

	public void setAlarmActivated(boolean alarmActivated) 
	{
		this.alarmActivated = alarmActivated;
	}

	

	@Override
	public void onBackPressed() 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exit");
		builder.setMessage("Exit application and disable service monitor? (You can press home to move to background)");
		builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}



	@Override
	protected void onDestroy() 
	{
		stopService(intent);
		super.onDestroy();
	}

    
}
