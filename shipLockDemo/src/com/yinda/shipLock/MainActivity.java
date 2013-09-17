package com.yinda.shipLock;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import com.igexin.slavesdk.MessageManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	AsyncHttpClient m_AsyncHttpClient = new AsyncHttpClient();
	private AsyncHttpResponseHandler m_responseHandlerPost = null;
	private RequestParams m_RequestParams = new RequestParams();
	// private String m_strURL = "http://ip.mobikey.cn:8082";
	private String m_strURL = "http://ip.mbpay.cn:8082";
	private boolean m_bCanExit = true;
	private ProgressBar m_PB;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
		m_PB = (ProgressBar)findViewById(R.id.ProgressBar);

		initResponseHandler();
		
		
//		m_AsyncHttpClient.post(m_strURL, m_RequestParams, m_responseHandlerGet);
		MessageManager.getInstance().initialize(this.getApplicationContext());
	}

	private void initResponseHandler() {
		// TODO Auto-generated method stub
		m_responseHandlerPost = new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				m_PB.setVisibility(View.VISIBLE);
				m_bCanExit = false;
				Toast.makeText(getApplicationContext(), "onStart()", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				m_PB.setVisibility(View.GONE);
				m_bCanExit = true;
				Toast.makeText(getApplicationContext(), "onFinish()", Toast.LENGTH_SHORT).show();
				m_AsyncHttpClient.delete(m_strURL, m_responseHandlerPost);
			}
			
			

			@Override
			public void onSuccess(String content) {
				// TODO Auto-generated method stub
				super.onSuccess(content);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, content);
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, content);
				Toast.makeText(getApplicationContext(),content, Toast.LENGTH_LONG).show();
				Log.i("onSuccess:", content);
			}

			@Override
			protected void sendFailureMessage(Throwable e, String responseBody) {
				// TODO Auto-generated method stub
				super.sendFailureMessage(e, responseBody);
				m_PB.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
				m_AsyncHttpClient.delete(m_strURL, m_responseHandlerPost);
				
			}

		};
		
		

		
	}

	private void postDataToService() throws UnsupportedEncodingException {
		
		

		m_RequestParams.put("username", "发工资");
		m_RequestParams.put("pwd", "按时发工资");
		

		m_AsyncHttpClient.setTimeout(2000);

		String strData = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
				+ "<root>" + "<head cid=\"01000004\" sver=\"" + "B 5.00.00.1"
				+ "\" mac=\"1234567890123456\">" + "<goods_type></goods_type>"
				+ "<merc_id></merc_id>" + "<areaid>" + "AreaID" + "</areaid>"
				+ "<zip_type></zip_type>" + "<userid>" + "发工资"
				+ "</userid>" + "<sessionid></sessionid>" + "<actid></actid>"
				+ "<sdid></sdid>" + "<simid>null</simid>"
				+ "<clientid></clientid>" + "</head>" + "<body>"
				+ "<upass enc_type=\"md5\">" + "按时发工资" + "</upass>"
				+ "</body>" + "</root>";

		m_AsyncHttpClient.post("http://192.168.27.118:8080/shiplock/loginAction", m_RequestParams, m_responseHandlerPost);
//		m_AsyncHttpClient.postMine(m_strURL, new StringEntity(strData),m_responseHandlerPost);	
		
		


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && !m_bCanExit) {
			m_AsyncHttpClient.cancelRequests(getApplicationContext(), true);
			m_bCanExit = true;
			m_PB.setVisibility(View.GONE);
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_submit: {
			try {
				postDataToService();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			break;
		}

	}

}
