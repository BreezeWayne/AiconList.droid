package jp.gothamVillage.AiconList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RotateService extends Service {
	private String TAG;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TAG = getClass().getSimpleName();
		Log.v(TAG, "create Serv");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Service on the run");
		Toast.makeText(getApplicationContext(), "ONTHERUN", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "Service on the bed");
		Toast.makeText(getApplicationContext(), "ONTHEBED", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
}
