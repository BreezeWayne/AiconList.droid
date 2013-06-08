package jp.gothamVillage.AiconList;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RotateService extends Service implements Constance {
	private String TAG;
	private Runnable mRunnable;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getClass().getSimpleName();
		mRunnable = new Runnable() {
			public void run() {
				serviceRunning();
			}
		};
		Log.v(TAG, "create Serv");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Service on the run");
		Toast.makeText(getApplicationContext(), "ONTHERUN", Toast.LENGTH_SHORT).show();
		serviceRunning();
		return START_STICKY;
	}

	protected void serviceRunning() {
		Log.v(TAG, "Service still on the run");
		new Handler().postDelayed(mRunnable, 5000);
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG, "Service on the bed");
		Toast.makeText(getApplicationContext(), "ONTHEBED", Toast.LENGTH_SHORT).show();
		new Handler().removeCallbacks(mRunnable);
		super.onDestroy();
	}
}
