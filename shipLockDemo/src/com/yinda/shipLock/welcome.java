package com.yinda.shipLock;

import org.json.JSONException;
import org.json.JSONObject;

import com.igexin.slavesdk.MessageManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.notification.progress.util.NotificationDownloadService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

public class welcome extends Activity {

	private AsyncHttpClient m_AsyncHttpClient = new AsyncHttpClient();
	private AsyncHttpResponseHandler m_responseHandlerGet = null;
	private String m_strURL = "http://ip.mobikey.cn/shipLock/shipLock.json";
	private Handler m_handle;
	private final int m_MSGStart = 0x01;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		
		Long lstart = System.currentTimeMillis();

//		initResponseHandler();

		// for (int i = 0; i < 1; i++) {
		// Intent intent = new Intent(welcome.this,
		// NotificationDownloadService.class);
		// intent.putExtra("id", i);
		// intent.putExtra("url","");
		// startService(intent);
		// }

		initHandler();

		
		
		Message msg = new Message();
		msg.what = m_MSGStart;
		Long lLast =System.currentTimeMillis()-lstart;
		m_handle.sendMessageDelayed(msg,lLast>3000?0:3000-lLast);
	}

	private void initHandler() {
		// TODO Auto-generated method stub
		m_handle = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case m_MSGStart: {
					
					Intent intent = new Intent(welcome.this,MainActivity.class);
					startActivity(intent);
					finish();
				}
					break;
				}
			}

		};
	}

	private void initResponseHandler() {
		// TODO Auto-generated method stub

		m_responseHandlerGet = new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				Toast.makeText(getApplicationContext(), "onStart()", 5000)
						.show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				Toast.makeText(getApplicationContext(), "onFinish()", 5000)
						.show();
				m_AsyncHttpClient.delete(m_strURL, m_responseHandlerGet);
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				if (statusCode == 200 && content != null
						&& content.length() != 0) {
					try {
						JSONObject object = new JSONObject(content);
						String m_strInfo = object.getString("Info");
						String m_strType = object.getString("type");
						String m_strVer = object.getString("ver");
						String m_strURL = object.getString("url");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(getApplicationContext(), content,
						Toast.LENGTH_LONG).show();
			}

			@Override
			protected void sendFailureMessage(Throwable e, String responseBody) {
				// TODO Auto-generated method stub
				super.sendFailureMessage(e, responseBody);
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
				m_AsyncHttpClient.delete(m_strURL, m_responseHandlerGet);
			}

		};

		m_AsyncHttpClient.setTimeout(10000);
		m_AsyncHttpClient.get(m_strURL, m_responseHandlerGet);
	}

}
