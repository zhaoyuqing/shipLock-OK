package com.notification.progress.util;

import java.io.File;

import com.notification.progress.util.NotificationUtil;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class NotificationDownloadService extends Service {

	private Handler m_myHander = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		m_myHander = new Handler() {
			public void handleMessage(android.os.Message msg) {

				switch (msg.what) {
				case 0: {
					installAPK(msg.obj.toString());
					NotificationUtil.nm.cancelAll();
				}
					break;
				}

			}
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int id = intent.getIntExtra("id", -1);
		NotificationUtil.notifyPtogressNotification(this, id,m_myHander);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void installAPK(String strAPKPath) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(strAPKPath)),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
