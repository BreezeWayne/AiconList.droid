package jp.gothamVillage.AiconList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

abstract class VariableWrapper extends Activity {
	protected String TAG;
	protected Context mContext;
	protected WindowManager mWindowManager;
	protected PackageManager mPackageManager;
	protected SharedPreferences mPreferences;
	protected Editor mEditor;
	protected LinearLayout mParentLayout;
	protected TextView mTextView1;
	protected TextView mTextView2;
	protected Button mStartService;
	protected Button mStopService;
	protected View mLastView;
	int mIconNum;
	int mIconSize;
	boolean asking_quit_ok = false;
	protected Toast mToast;
	protected Handler mHandler;
	protected int duringDoon = 0;
	protected Runnable mRunnableOfCalmDown;
	protected Intent mService;
	protected View.OnClickListener serviceEnabler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				Log.v(TAG, "start Srvice");
				startService(new Intent(VariableWrapper.this, RotateService.class));
				break;
			case R.id.button2:
			default:
				Log.v(TAG, "stop Srvice");
				stopService(new Intent(VariableWrapper.this, RotateService.class));
			}
		}
	};
}
