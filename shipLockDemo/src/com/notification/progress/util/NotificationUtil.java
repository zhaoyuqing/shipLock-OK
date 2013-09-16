package com.notification.progress.util;

import java.io.File;

import com.yinda.shipLock.MainActivity;
import com.yinda.shipLock.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.RemoteViews;

public class NotificationUtil {

	public static final int NOTIFY_ID_PROGRESS = 1;
	public static NotificationManager nm;
	private static final String URL = "http://webim.120ask.com/apk/AndroidIM.apk";
	private static final String TITLE = "更新提示";
//	private String m_strURLAPK = null;

	// private static final String url =
	// "http://192.168.1.18:8080/FileDownload/test.avi";

	public static void notifyPtogressNotification(Context context, int notifyId,Handler handler) {
		if (nm == null) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		final Builder mBuilder = new NotificationCompat.Builder(context);

		String savePath = getSavePath(context, "/apks");
		if (TextUtils.isEmpty(savePath)) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("market://details?id="
					+ context.getPackageName()));
			mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, i,
					PendingIntent.FLAG_UPDATE_CURRENT));
			mBuilder.setContentText("您的SD卡不可用,建议到市场更新本程序");
			mBuilder.setContentTitle("更新提示");
			setNotificationIcon(context, mBuilder);
			nm.notify(notifyId, mBuilder.build());
			return;
		}

		String strAPKPath = savePath + "/"
				+ URL.substring(URL.lastIndexOf("/") + 1);
		Intent intent = new Intent(context, MainActivity.class);
		// 当前手机版本>=4.0
		if (Utils.hasIceCreamSandwich()) {
			
			PendingIntent pi = PendingIntent.getActivity(context, 0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(pi);
			mBuilder.setContentTitle(TITLE).setContentText("下载进度：");
			setNotificationIcon(context, mBuilder);
			new ProgressAsyncTask(handler,URL, savePath, null, true, nm, mBuilder,
					notifyId).execute();
		} else {// 4.0以前版本
			// // 一定要设置Icon,否则不显示
			setNotificationIcon(context, mBuilder);
			new ProgressAsyncTask(handler,URL, savePath, null, false, nm, notifyId,
					getNotification(context, TITLE, intent))
					.execute();
		}
	}

	/**
	 * 设置Notification的icon,<br>
	 * 如果不设置图标则无法显示Notification,并且LargeIcon,SmallIcon都需要设置
	 * 
	 * @param context
	 * @param mBuilder
	 */
	private static void setNotificationIcon(Context context,
			final Builder mBuilder) {
		// 如果不设置小图标则无法显示Notification
		mBuilder.setLargeIcon(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.src_status_bar_large));
		mBuilder.setSmallIcon(R.drawable.src_status_bar_small);
	}

	/**
	 * 兼容4.0以下设备
	 * 
	 * @param context
	 * @param title
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Notification getNotification(Context context, String title,
			Intent installAPKInt) {

		 Notification notification = new Notification(
		 R.drawable.src_status_bar_large, "下载提醒",
		 System.currentTimeMillis());
//		 notification.flags =
//		 Notification.FLAG_AUTO_CANCEL;
		 notification.flags = Notification.FLAG_AUTO_CANCEL;
		 notification.contentView = new RemoteViews(context.getPackageName(),
		 R.layout.notification_progress_layout);
		 notification.contentView.setProgressBar(
		 R.id.notification_progress_layout_pb, 100, 0, false);
		 notification.contentView.setTextViewText(
		 R.id.notification_progress_layout_tv_title, title);
		 notification.contentIntent = PendingIntent.getActivity(context, 0,
		 installAPKInt,
		 PendingIntent.FLAG_UPDATE_CURRENT);
		return notification;
	}

	/**
	 * 获取文件保存目录
	 * 
	 * @return
	 */
	private static String getSavePath(Context context, String subDir) {
		// 判断SD卡是否存在
		boolean sdCardExist = StorageUtil.isExternalStorageAvailable();
		File file = null;
		if (sdCardExist) {
			file = Environment.getExternalStorageDirectory();
		} else {// 内存存储空间
			file = context.getFilesDir();
			return getPath(file, subDir);
		}
		return getPath(file, subDir);
	}

	private static String getPath(File f, String subDir) {
		File file = new File(f.getAbsolutePath() + subDir);
		if (!file.exists()) {
			boolean isSuccess = file.mkdirs();
			System.out.println(isSuccess);
		}
		return file.getAbsolutePath();
	}

	private static Intent getInstallAPK(String strURLAPK) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(strURLAPK)),
				"application/vnd.android.package-archive");
		return intent;
	}
}
