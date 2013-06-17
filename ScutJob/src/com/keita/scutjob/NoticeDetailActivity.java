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
 * 事务通知的详细内容
 */
public class NoticeDetailActivity extends Activity {
	//protected ProgressDialog pDialog= null;
		//Activtiy中的控件
		protected TextView contentTv = null;
		protected TextView titleTv = null;
		protected ProgressBar Pb = null;
		protected TextView progressTitle = null;
		protected ImageButton imageButton = null;
		protected Button remindButton = null;
		//解析HTML的元素
		protected Document doc = null;
		protected Elements tempElement = null;
		//每一个段落
		protected Element p = null;

		protected Element contentElement = null;
		protected Element titleElement = null;
		
		protected String URL = null;
		protected String Date = null;
		//数据库类
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
			//pDialog = ProgressDialog.show(this, "请稍等","数据更新中。。。",true);
			
			//新开线程异步处理网络加载
			new Thread(runnable).start();

			
		}
		//接收到线程信息后更新UI
		Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(doc != null){
				//解析HTML获取主题,时间，地点元素
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
				
				//加入有米广告
				LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		        AdView adView = new AdView(NoticeDetailActivity.this, AdSize.SIZE_320x50);
		        adLayout.addView(adView);
		        SpotManager.getInstance(NoticeDetailActivity.this).loadSpotAds();
		        SpotManager.getInstance(NoticeDetailActivity.this).showSpotAds(NoticeDetailActivity.this);
				
				imageButton = (ImageButton)findViewById(R.id.noticeDetailImageBtn);
				
				//监听返回键 返回上一级
				imageButton.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						NoticeDetailActivity.this.finish();
					}
					
				});
				
				}else{
					Toast.makeText(getApplicationContext(), "这是下载文件无法解析", Toast.LENGTH_SHORT).show();
					NoticeDetailActivity.this.finish();
				}
				
			}
		};
		//异步访问网页
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
					//发送消息更新UI
					msg.what = 1;
					handler.sendMessage(msg);
					
				}
				
			};
			//进度圈的UI载入
			void progressBarLoad(){
				progressTitle = (TextView) findViewById(R.id.progressTitle);
				progressTitle.setText("详情");
				Pb = (ProgressBar) findViewById(R.id.progressbar);
				imageButton = (ImageButton)findViewById(R.id.progressImageBtn);
				//监听返回键 返回上一级
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

