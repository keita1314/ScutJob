package com.keita.scutjob.fragment;
/*
 *咨询 
 * @authorguan ji da
 */
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.keita.scutjob.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultFragment extends Fragment {
	protected static final String Submit_en = null;
	protected EditText nameText = null;
	protected EditText mailText = null;
	protected EditText contentText = null;
	protected Button submitButton = null;
	protected ImageButton imageButton;
	String name = null;
	String mail = null;
	String content = null;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = null;
		view = inflater.inflate(R.layout.consult_activity,container,false);
		
		nameText = (EditText)view.findViewById(R.id.nameText);
		mailText = (EditText)view.findViewById(R.id.mailText);
		contentText = (EditText)view.findViewById(R.id.contentText);
		submitButton = (Button)view.findViewById(R.id.submit);
		
		imageButton = (ImageButton)view.findViewById(R.id.listViewHeaderImageBtn);
		//返回按钮响应事件
		imageButton.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					//Intent intent = new Intent();
					//intent.setClass(JobActivity.this, MainActivity.class);
					//startActivity(intent);
					getActivity().finish();
					
					
					
					
				}
				
			});
		//提交按钮响应事件
		submitButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				
				
				
				if(nameText.getText()!= null && mailText.getText()!=null
						&&contentText.getText() != null ){
					new Thread(runnable).start();
					Toast.makeText(getActivity().getApplicationContext(), "已提交", Toast.LENGTH_SHORT).show();

				}
				else
					Toast.makeText(getActivity().getApplicationContext(), "请完整输入", Toast.LENGTH_SHORT).show();
		
			}
			
		});
		return view;
	}
	Runnable runnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			name = nameText.getText().toString();
			mail = mailText.getText().toString();
			content = contentText.getText().toString();

			String txtname="";
			String txtemail="";
			String selectcome = "本科生";
			String txthomepage = "http://";
			String txtoicq="";
			String sex = "1";
			String txthead ="1.gif";
			String txtcontent = "";
			String Submit = "留言";
			
			txtname = name;
			txtemail = mail;
			txtcontent = content;
			//post请求中 URL参数中文转码
			
			
			String txtname_en = "";
			String txtemail_en = "";
			String selectcome_en= "";
			String txthomepage_en="";
			String txtcontent_en="";
			String Submit_en="";
			
			try {
				txtname_en = URLEncoder.encode(txtname,"gb2312");
				txtemail_en = URLEncoder.encode(txtemail,"gb2312");	
				selectcome_en = URLEncoder.encode(selectcome,"gb2312");
				txthomepage_en = URLEncoder.encode(txthomepage,"gb2312");
				txtcontent_en = URLEncoder.encode(txtcontent,"gb2312");
				Submit_en = URLEncoder.encode(Submit,"gb2312");
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			//请求参数
			String param = "txtname="+txtname_en+"&"+"txtemail="+txtemail_en+
							"&"+"selectcome="+selectcome_en+"&"+"txthomepage="+
							txthomepage_en+"&"+"txtoicq="+"&"+"sex="+sex+"&"+
							"txthead="+txthead+"&"+"txtcontent="+txtcontent_en+
							"&"+"Submit="+Submit_en;
			try{
			URL url = new URL("http://202.38.193.246:8880/mess/save.asp");
			HttpURLConnection uc = (HttpURLConnection)url.openConnection();
			uc.setRequestMethod("POST");
			uc.setDoInput(true);
			//服务端会跳转
			uc.setInstanceFollowRedirects(false);
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//写入请求中
			uc.connect();
			OutputStream os = uc.getOutputStream();
			os.write(param.getBytes());
			os.close();
			Log.v("test", "ss");
			Log.v("code",""+uc.getResponseCode());
			}catch(Exception e){
				e.printStackTrace();
			}
			Log.v("urlencode", param);
		
		}
		
	};
}
