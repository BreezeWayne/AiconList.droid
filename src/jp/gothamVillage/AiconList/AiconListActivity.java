package jp.gothamVillage.AiconList;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class AiconListActivity extends VariableWrapper implements Constance,
		OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = getClass().getSimpleName();
		initVars();
		setContentView(R.layout.main);
		viewsSetup();
	}

	private void prepareUtilities() {
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mPackageManager = (PackageManager) getPackageManager();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	private void initVars() {
		mContext = this;
		mHandler = new Handler();
		// mIconNum = 7;
		prepareUtilities();
		mIconSize = 77;// mWindowManager.getDefaultDisplay().getWidth() /
						// mIconNum;
		mIconNum = (mWindowManager.getDefaultDisplay().getWidth() - 5 * 2)
				/ mIconSize;
		mIconSizeLarge = (int) (mIconSize * 1.8);
		mService = new Intent(mContext, RotateService.class);
		mServiceName = RotateService.class.getName();
		mHotIconView = null;
		mEditor = mPreferences.edit();
	}

	private void viewsSetup() {
		mParentLayout = (LinearLayout) findViewById(R.id.p);
		mTextView1 = (TextView) findViewById(R.id.selectedAppInfo1);
		mTextView2 = (TextView) findViewById(R.id.selectedAppInfo2);
		setUpAiconList();
	}

	private void setUpAiconList() {
		int appCnt = 0;
		LinearLayout ll = null;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appInfos = mPackageManager.queryIntentActivities(
				intent, 0);
		// ÉAÉvÉäèÓïÒÇéÊìæ
		// List<ApplicationInfo> apps = mPackageManager
		// .getInstalledApplications(PackageManager.GET_META_DATA);
		for (ResolveInfo ai : appInfos) {
			// int icon = ai.icon;
			// if (icon == 0 /*|| (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0
			// */)
			// continue;
			if (appCnt % mIconNum == 0) {
				ll = new LinearLayout(mContext);
				ll.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				ll.setOrientation(LinearLayout.HORIZONTAL);
				mParentLayout.addView(ll);
			}
			ImageView iv = new ImageView(mContext);
			iv.setLayoutParams(new LinearLayout.LayoutParams(mIconSize,
					mIconSize));
			Drawable d = ai.loadIcon(mPackageManager);
			String pckName = ai.activityInfo.packageName;
			setRotateDrawable(iv, d, getNowRotateDegree(getActFlag(pckName)));
			iv.setOnClickListener(this);
			iv.setTag(ai);
			ll.addView(iv);
			appCnt++;
		}
	}

	private int getNowRotateDegree(int actFlag) {
		int degree;
		switch (actFlag) {
		case ACT_LANDSCAPE_RIGHT:
			degree = (90);
			break;
		case ACT_PORTRAIT_DOWN:
			degree = (180);
			break;
		case ACT_LANDSCAPE_LEFT:
			degree = (270);
			break;
		case ACT_AS_GRAVITY:
		case ACT_AS_SYSTEM:
		case ACT_PORTRAIT_UP:
		default:
			degree = 0;
			break;
		}
		return degree;
	}

	private void setRotateDrawable(ImageView iv, Drawable d, int degree) {
		// ResolveInfo ri = (ResolveInfo) iv.getTag();
		Bitmap bmp = ((BitmapDrawable) d).getBitmap();
		Matrix max = new Matrix();
		max.setRotate(degree);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
				bmp.getHeight(), max, false);
		// Save Rotate-Policy
		iv.setImageBitmap(rotatedBitmap);
	}

	public void onClick(View v) {
		doonIfNotDoon(v);
	}

	// 1 Is v hot ?
	// yes -> then rotate its drawable
	// no -> then cool the v
	private void doonIfNotDoon(View v) {
		// Log.i("##", "aa bb cc dd " + duringDoon);

		if (duringDoonCnt == 0) {
			heatIcon(v);
		} else {
			if (mHotIconView == v) {
				// Rotate during doon
				rotateViewDrawable(v);
			} else {
				// cool the Icon
				dislargeIcon();
			}
		}
		manageHotTimer();
	}

	private void heatIcon(View v) {
		enlargeIcon(v);
		showAppInfo(v);
	}

	private void manageHotTimer() {
		duringDoonCnt++;
		mHandler.postDelayed(new Runnable() {
			public void run() {
				checkExpiredOfTouch();
			}
		}, hotIronTime);
	}

	private void checkExpiredOfTouch() {
		duringDoonCnt--;
		if (duringDoonCnt == 0) {
			dislargeIcon();
		}
	}

	private void dislargeIcon() {
		if (mHotIconView != null) {
			android.view.ViewGroup.LayoutParams lllp = mHotIconView
					.getLayoutParams();
			lllp.width = mIconSize;
			lllp.height = mIconSize;
			mHotIconView.setLayoutParams(lllp);
			mHotIconView = null;
		}
	}

	private void rotateViewDrawable(View v) {
		ResolveInfo ri = (ResolveInfo) v.getTag();
		String pckName = ri.activityInfo.packageName;
		int nextFlag = getActNextFlag(getActFlag(pckName));
		int nextDegree = getNowRotateDegree(nextFlag);
		Drawable d = ((ImageView) v).getDrawable();
		setRotateDrawable((ImageView) v, d, nextDegree);
		showCurrentRotatePlanFromFlag(nextFlag);
		// save in sharedPreference
		setActFlag(pckName, nextFlag);
	}

	private int getActNextFlag(int actFlag) {
		int nextFlag = actFlag;
		switch (actFlag) {
		case ACT_PORTRAIT_UP:
			nextFlag = ACT_LANDSCAPE_RIGHT;
			break;
		case ACT_LANDSCAPE_RIGHT:
			nextFlag = ACT_PORTRAIT_DOWN;
			break;
		case ACT_PORTRAIT_DOWN:
			nextFlag = ACT_LANDSCAPE_LEFT;
			break;
		case ACT_LANDSCAPE_LEFT:
			nextFlag = ACT_AS_GRAVITY;
			break;
		case ACT_AS_GRAVITY:
			nextFlag = ACT_AS_SYSTEM;
			break;
		case ACT_AS_SYSTEM:
		default:
			nextFlag = ACT_PORTRAIT_UP;
			break;
		}
		return nextFlag;
	}

	/**
	 * Save actFlag in SharedPreferences
	 * 
	 * @param pckName
	 *            key for preference
	 * @param actFlag
	 *            object to store
	 */
	private void setActFlag(String pckName, int actFlag) {
		mEditor.putInt(pckName, actFlag);
		mEditor.commit();
	}

	private int getActFlag(String pckName) {
		return mPreferences.getInt(pckName, ACT_AS_SYSTEM);
	}

	private void enlargeIcon(View v) {
		android.view.ViewGroup.LayoutParams lllp = v.getLayoutParams();
		lllp.width = mIconSizeLarge;
		lllp.height = mIconSizeLarge;
		v.setLayoutParams(lllp);
	}

	private void showAppInfo(View v) {
		ResolveInfo ai = (ResolveInfo) v.getTag();
		String appName = (String) ai.loadLabel(mPackageManager);
		String pckName = ai.activityInfo.packageName;
		int plan = getActFlag(pckName);
		mTextView1.setText(appName);
		showCurrentRotatePlanFromFlag(plan);
		mHotIconView = v;
	}

	private void showCurrentRotatePlanFromFlag(int plan) {
		mTextView2.setText("Rotate: " + getDescriptionFromFlag(plan));
	}

	@Override
	public void onBackPressed() {
		if (!asking_quit_ok) {
			asking_quit_ok = true;
			mToast = Toast.makeText(mContext, R.string.quit_direction,
					Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, -20);
			mToast.show();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					asking_quit_ok = false;
				}
			}, 2000);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onPause() {
		if (mToast != null)
			mToast.cancel();
		super.onPause();
	}

	private String getDescriptionFromFlag(int flag) {
		String planString;
		switch (flag) {
		default:
		case ACT_AS_SYSTEM:
			planString = "AS_SYSTEM" + getString(R.string.roll_system);
			break;
		case ACT_AS_GRAVITY:
			planString = "AS_GRAVITY" + getString(R.string.roll_gravity);
			break;
		case ACT_LANDSCAPE_LEFT:
			planString = "LANDSCAPE_LEFT" + getString(R.string.roll_left);
			break;
		case ACT_PORTRAIT_DOWN:
			planString = "PORTRAIT_DOWN" + getString(R.string.roll_down);
			break;
		case ACT_LANDSCAPE_RIGHT:
			planString = "LANDSCAPE_RIGHT" + getString(R.string.roll_right);
			break;
		case ACT_PORTRAIT_UP:
			planString = "PORTRAIT_UPÅ@" + getString(R.string.roll_up);
			break;
		}
		return planString;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isServiceRunning = isServiceRunnning(mContext);
		if (isServiceRunning) {
			menu.getItem(0).setTitle(R.string.sleep_plan);
		} else {
			menu.getItem(0).setTitle(R.string.activate_plan);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private boolean isServiceRunnning(Context context) {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo s : services) {
			if (s.service.getClassName().equals(mServiceName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.activate_plan);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(getString(R.string.activate_plan))) {
			Log.v(TAG, "start Srvice");
			startService(new Intent(mContext, RotateService.class));
			item.setTitle(getString(R.string.sleep_plan));
		} else {
			stopService(new Intent(mContext, RotateService.class));
			Log.v(TAG, "stop Srvice");
			item.setTitle(getString(R.string.activate_plan));
		}
		return super.onOptionsItemSelected(item);
	}
}