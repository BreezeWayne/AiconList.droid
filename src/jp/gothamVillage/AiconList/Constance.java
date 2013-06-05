package jp.gothamVillage.AiconList;

public interface Constance {
	final static int ACT_AS_SYSTEM = 0x00;
	final static int ACT_AS_GRAVITY = 0x01;
	final static int ACT_MASK_AUTO = 0xf0;
	final static int ACT_PORTRAIT_UP = 0x10;
	final static int ACT_LANDSCAPE_RIGHT = 0x20;
	final static int ACT_PORTRAIT_DOWN = 0x40;
	final static int ACT_LANDSCAPE_LEFT = 0x80;

	int hotIronTime = 1300;// in milli-seconds
	final static String KEY_SERVICE_RUNNING = "pref_service_running";
}
