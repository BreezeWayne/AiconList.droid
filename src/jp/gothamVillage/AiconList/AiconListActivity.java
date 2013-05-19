package jp.gothamVillage.AiconList;

import java.util.List;

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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
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
		mService = new Intent(mContext, RotateService.class);
		mLastView = null;
		mEditor = mPreferences.edit();
	}

	private void viewsSetup() {
		mParentLayout = (LinearLayout) findViewById(R.id.p);
		mTextView1 = (TextView) findViewById(R.id.selectedAppInfo1);
		mTextView2 = (TextView) findViewById(R.id.selectedAppInfo2);
		mStartService = (Button) findViewById(R.id.button1);
		mStopService = (Button) findViewById(R.id.button2);
		mStartService.setOnClickListener(serviceEnabler);
		mStopService.setOnClickListener(serviceEnabler);
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
			setRotateDrawable(iv, ai, getNowRotateDegree(getActFlagFromRi(ai)));
			iv.setOnClickListener(this);
			iv.setTag(ai);
			ll.addView(iv);
			appCnt++;
		}
	}

	private int getNowRotateDegree(int actFlag) {
		int degree;
		switch (actFlag) {
		case ACT_AS_GRAVITY:
		case ACT_AS_SYSTEM:
		case ACT_PORTRAIT_UP:
		default:
			degree = 0;
			break;
		case ACT_LANDSCAPE_RIGHT:
			degree = (90);
			break;
		case ACT_PORTRAIT_DOWN:
			degree = (180);
			break;
		case ACT_LANDSCAPE_LEFT:
			degree = (270);
			break;
		}
		return degree;
	}

	private void setRotateDrawable(ImageView iv, ResolveInfo ai, int degree) {
		Drawable d = ai.loadIcon(mPackageManager);
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
		fireTouchTimer();
	}

	private void doonIfNotDoon(View v) {
		// Log.i("##", "aa bb cc dd " + duringDoon);
		if (duringDoon == 0) {
			enlargeIcon(v);
		} else {
			if (mLastView != v) {
				mHandler.removeCallbacks(mRunnableOfCalmDown);
				mHandler.postAtFrontOfQueue(mRunnableOfCalmDown);
				return;
			}
			// Rotate during doon
			ResolveInfo ai = (ResolveInfo) v.getTag();
			getActFlagFromRi(ai);
			setRotateDrawable((ImageView) v, ai, getNextRotateDegree(ai));
		}
	}

	private int getActFlagFromRi(ResolveInfo ai) {
		String pckName = ai.activityInfo.packageName;
		return mPreferences.getInt(pckName, ACT_AS_SYSTEM);
	}

	private void fireTouchTimer() {
		duringDoon++;
		mHandler.postDelayed(mRunnableOfCalmDown = new Runnable() {
			public void run() {
				duringDoon--;
				checkExpiredOfTouch();
			}
		}, concreteTime);
	}

	private void checkExpiredOfTouch() {
		if (duringDoon == 0) {
			dislargeIcon();
		}
	}

	private void dislargeIcon() {
		if (mLastView != null) {
			mLastView.setLayoutParams(new LinearLayout.LayoutParams(mIconSize,
					mIconSize));
		}
	}

	private void enlargeIcon(View v) {
		int highSize = (int) (mIconSize * 1.8);
		v.setLayoutParams(new LinearLayout.LayoutParams(highSize, highSize));
		ResolveInfo ai = (ResolveInfo) v.getTag();
		String appName = (String) ai.loadLabel(mPackageManager);
		String pckName = ai.activityInfo.packageName;
		int plan = mPreferences.getInt(pckName, ACT_AS_SYSTEM);
		mTextView1.setText(appName);
		tellCurrentRotatePlanFromFlag(plan);
		mLastView = v;
	}

	private void tellCurrentRotatePlanFromFlag(int plan) {
		mTextView2.setText("Rotate: " + getPlanFromFlag(plan));
	}

	private int getNextRotateDegree(ResolveInfo ai) {
		int rotateDegree = 0;
		String pckName = ai.activityInfo.packageName;
		int actFlag = mPreferences.getInt(pckName, ACT_AS_SYSTEM);
		switch (actFlag) {
		case ACT_AS_GRAVITY:
			rotateDegree = 0;
			actFlag = ACT_AS_SYSTEM;
			break;
		case ACT_PORTRAIT_UP:
			rotateDegree = 90;
			actFlag = ACT_LANDSCAPE_RIGHT;
			break;
		case ACT_LANDSCAPE_RIGHT:
			rotateDegree = 180;
			actFlag = ACT_PORTRAIT_DOWN;
			break;
		case ACT_PORTRAIT_DOWN:
			rotateDegree = 270;
			actFlag = ACT_LANDSCAPE_LEFT;
			break;
		case ACT_LANDSCAPE_LEFT:
			rotateDegree = 0;
			actFlag = ACT_AS_GRAVITY;
			break;
		case ACT_AS_SYSTEM:
		default:
			rotateDegree = 0;
			actFlag = ACT_PORTRAIT_UP;
			break;
		}
		mEditor.putInt(pckName, actFlag);
		mEditor.commit();
		tellCurrentRotatePlanFromFlag(actFlag);
		return rotateDegree;
	}

	@Override
	public void onBackPressed() {
		if (!asking_quit_ok) {
			asking_quit_ok = true;
			mToast = Toast.makeText(mContext, R.string.quit_direction,
					Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, -20);
			mToast.show();
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

	private String getPlanFromFlag(int flag) {
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
}