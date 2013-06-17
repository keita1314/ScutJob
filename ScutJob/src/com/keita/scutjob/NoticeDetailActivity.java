package com.keita.scutjob;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.spot.SpotManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.keita.scutjob.R;



import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/*
 * @author Guan Ji Da
 * ����֪ͨ����ϸ����
 */
public class NoticeDetailActivity extends Activity {
	//protected ProgressDialog pDialog= null;
		//Activtiy�еĿؼ�
		protected TextView contentTv = null;
		protected TextView titleTv = null;
		protected ProgressBar Pb = null;
		protected TextView progressTitle = null;
		protected ImageButton imageButton = null;
		protected Button remindButton = null;
		//����HTML��Ԫ��
		protected Document doc = null;
		protected Elements tempElement = null;
		//ÿһ������
		protected Element p = null;

		protected Element contentElement = null;
		protected Element titleElement = null;
		
		protected String URL = null;
		protected String Date = null;
		//���ݿ���
		protected SQLiteDatabase mySQLiteDatabase = null;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.progressbar);
			
			progressBarLoad();
			Intent intent = getIntent();
			URL = intent.getStringExtra("url");
			//Date = intent.getStringExtra("date");
			
			
			Log.v("tag2", "hello");
			//pDialog = ProgressDialog.show(this, "���Ե�","���ݸ����С�����",true);
			
			//�¿��߳��첽�����������
			new Thread(runnable).start();

			
		}
		//���յ��߳���Ϣ�����UI
		Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(doc != null){
				//����HTML��ȡ����,ʱ�䣬�ص�Ԫ��
				titleElement = doc.getElementsByAttributeValue("class", "pl04").first();
				contentElement = doc.getElementsByAttributeValue("width", "90%").first();
				//Log.v("noticetitle", titleElement.ownText());
				//Log.v("noticedetail", contentElement.text());
				//pDialog.dismiss();
				tempElement = contentElement.getElementsByTag("p");
				String text ="";
				for(int i =0;i<tempElement.size();i++){
					text +=tempElement.get(i).text()+"\n";
				}
				Pb.setVisibility(View.GONE);
				setContentView(R.layout.notice_detail_activity);
				titleTv = (TextView)findViewById(R.id.notice_title);
				contentTv = (TextView)findViewById(R.id.notice_content);
				
				TextPaint tp = titleTv.getPaint();
				tp.setFakeBoldText(true);
				titleTv.setText((String)titleElement.ownText());
				if("".equals(text))
					contentTv.setText(contentElement.ownText());
				else
				contentTv.setText(text);
				contentTv.setMovementMethod(ScrollingMovementMethod.getInstance());
				
				//�������׹��
				LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		        AdView adView = new AdView(NoticeDetailActivity.this, AdSize.SIZE_320x50);
		        adLayout.addView(adView);
		        SpotManager.getInstance(NoticeDetailActivity.this).loadSpotAds();
		        SpotManager.getInstance(NoticeDetailActivity.this).showSpotAds(NoticeDetailActivity.this);
				
				imageButton = (ImageButton)findViewById(R.id.noticeDetailImageBtn);
				
				//�������ؼ� ������һ��
				imageButton.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						NoticeDetailActivity.this.finish();
					}
					
				});
				
				}else{
					Toast.makeText(getApplicationContext(), "���������ļ��޷�����", Toast.LENGTH_SHORT).show();
					NoticeDetailActivity.this.finish();
				}
				
			}
		};
		//�첽������ҳ
		Runnable runnable = new Runnable(){

				@Override
				public void run() {
					Message msg = new Message();
					try {
						doc = Jsoup.parse(new URL(URL), 10000);
						Log.v("tag", "doc done");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//������Ϣ����UI
					msg.what = 1;
					handler.sendMessage(msg);
					
				}
				
			};
			//����Ȧ��UI����
			void progressBarLoad(){
				progressTitle = (TextView) findViewById(R.id.progressTitle);
				progressTitle.setText("����");
				Pb = (ProgressBar) findViewById(R.id.progressbar);
				imageButton = (ImageButton)findViewById(R.id.progressImageBtn);
				//�������ؼ� ������һ��
				imageButton.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						//Intent intent = new Intent();
						//intent.setClass(JobActivity.this, MainActivity.class);
						//startActivity(intent);
						NoticeDetailActivity.this.finish();
					}
					
				});
				Pb.setIndeterminate(false);
				Pb.setVisibility(View.VISIBLE);
			}
}

