package jp.gothamVillage.AiconList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Booter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Log.i("wow", "woe");
		Toast.makeText(context, "wow", Toast.LENGTH_SHORT).show();
//		Intent i = new Intent(context, RoatateService.class);
//		context.startService(i);
	}

}
