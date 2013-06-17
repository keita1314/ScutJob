package com.keita.scutjob;

/*
 * @author Guan Ji Da
 * 宣讲会详细事项
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.keita.scutjob.R;
import com.keita.scutjob.myview.MyImageButton;

public class JobDetailActivity extends Activity {
	
	//protected ProgressDialog pDialog= null;
	//Activtiy中的控件
	protected TextView topicTv = null;
	protected TextView dateTv = null;
	protected TextView locationTv = null;
	protected TextView readNumTv = null;
	protected ProgressBar Pb = null;
	protected ImageButton imageButton = null;
	protected MyImageButton remindButton = null;
	protected MyImageButton shareButton = null;
	//解析HTML的元素
	protected Document doc = null;
	protected Elements tempElement = null;


	protected Element topicElement = null;
	protected Element dateElement = null;
	protected Element locationElement = null;
	protected Element readNumElement = null;
	
	protected String URL = null;
	protected String Date = null;
	//数据库类
	protected SQLiteDatabase mySQLiteDatabase = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.job_detail_activity);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.progressbar);
		TextView titleTv = (TextView) findViewById(R.id.progressTitle);
		titleTv.setText("详情");
		Pb = (ProgressBar) findViewById(R.id.progressbar);
		imageButton = (ImageButton)findViewById(R.id.progressImageBtn);
		//监听返回键 返回上一级
		imageButton.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent();
				//intent.setClass(JobDetailActivity.this, JobActivity.class);
				//startActivity(intent);
				JobDetailActivity.this.finish();
			}
			
		});
		Pb.setIndeterminate(false);
		Pb.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		URL = intent.getStringExtra("url");
		Date = intent.getStringExtra("date");
		
		
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
			//解析HTML获取主题,时间，地点元素
			tempElement = doc.getElementsContainingOwnText("主题");
			//Log.v("tag", tempElement.text());
			topicElement = tempElement.first().nextElementSibling();
			
			tempElement.clear();
			
			tempElement = doc.getElementsContainingOwnText("时间");
			dateElement = tempElement.first().nextElementSibling();
			tempElement.clear();
			
			tempElement = doc.getElementsContainingOwnText("地点");
			locationElement = tempElement.first().nextElementSibling();
			tempElement.clear();
			
			tempElement = doc.getElementsByAttributeValue("color", "blue");
			readNumElement = tempElement.parents().first();
			tempElement.clear();
			
			//pDialog.dismiss();
			Pb.setVisibility(View.GONE);
			setContentView(R.layout.job_detail_activity);
			topicTv = (TextView)findViewById(R.id.topicTv);
			dateTv = (TextView)findViewById(R.id.dateTv);
			locationTv = (TextView)findViewById(R.id.locationTv);
			readNumTv = (TextView)findViewById(R.id.readNumTv);
			
			topicTv.setText("");
			dateTv.setText("");
			locationTv.setText("");
			readNumTv.setText("");
			
			remindButton = (MyImageButton) findViewById(R.id.remindButton);
			imageButton = (ImageButton)findViewById(R.id.jobDetailImageBtn);
			remindButton.setText("关注");
			remindButton.setColor(Color.GRAY);
			remindButton.setBackgroundColor(Color.TRANSPARENT);
			
			shareButton = (MyImageButton)findViewById(R.id.shareButton);
			shareButton.setText("分享");
			shareButton.setColor(Color.GRAY);
			shareButton.setBackgroundColor(Color.TRANSPARENT);
			//监听返回键 返回上一级
			imageButton.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//Intent intent = new Intent();
					//intent.setClass(JobDetailActivity.this, JobActivity.class);
					//startActivity(intent);
					JobDetailActivity.this.finish();
				}
				
			});
			if(topicElement != null)
				topicTv.setText(topicElement.ownText());
			//某些网页时间取不出 可能需要修改
			if(dateElement != null)
				dateTv.setText(dateElement.ownText());
			//从Jobactivity中取出的date防止解析时取不到
			else
				dateTv.setText(Date);
			if(locationElement != null)
				locationTv.setText(locationElement.ownText());
			if(readNumTv != null)
				readNumTv.setText(readNumElement.ownText());
			
			//监听提醒按钮 把数据保存起来
			remindButton.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mySQLiteDatabase = getApplicationContext()
							.openOrCreateDatabase("myRemindJob.db",MODE_PRIVATE, null);
				//判断表是否已存在 不存在的话新建表格
				String isTableExists = "SELECT * FROM sqlite_master WHERE type='table' and name='remind_job' ORDER BY name";
				Cursor cur = mySQLiteDatabase.rawQuery(isTableExists,null);
				if(cur != null){
						//不存在 创建新表
						if(cur.getCount() == 0){
							Log.v("db", "new table");
							String createTable = "CREATE TABLE remind_job(_id INTEGER PRIMARY KEY,topic TEXT," +
												"date DATE,location TEXT,readNum INTEGER,isRemind TEXT)";
							mySQLiteDatabase.execSQL(createTable);
						}
						else
							Log.v("db", "table created");
					}
				ContentValues cv = new ContentValues();
				cv.put("topic", (String)topicTv.getText());
				cv.put("date",(String)dateTv.getText());
				cv.put("location", (String)locationTv.getText());
				cv.put("readNum",(String)readNumTv.getText());
				cv.put("isRemind","0");
				mySQLiteDatabase.insert("remind_job", null, cv);
				Log.v("db", "insert done");
				mySQLiteDatabase.close();
				
				
				/*跳转到已添加的提醒页面*/
				Intent intent2 = new Intent();
				intent2.setClass(JobDetailActivity.this, RemindJobActivity.class);
				startActivity(intent2);
				JobDetailActivity.this.finish();
				}
			});
			
			
			
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
	
}
