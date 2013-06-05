package jp.gothamVillage.AiconList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Booter extends BroadcastReceiver {
	final String KEY_SERVICE = "pref_service_running";

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.i(getClass().getSimpleName(), "broadcast received");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean runService = prefs.getBoolean(KEY_SERVICE, false);
		if (runService) {
			Intent i = new Intent(context, RotateService.class);
			context.startService(i);
		}
	}

}
