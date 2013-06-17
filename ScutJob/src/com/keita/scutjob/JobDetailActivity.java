package com.keita.scutjob;

/*
 * @author Guan Ji Da
 * ��������ϸ����
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
	//Activtiy�еĿؼ�
	protected TextView topicTv = null;
	protected TextView dateTv = null;
	protected TextView locationTv = null;
	protected TextView readNumTv = null;
	protected ProgressBar Pb = null;
	protected ImageButton imageButton = null;
	protected MyImageButton remindButton = null;
	protected MyImageButton shareButton = null;
	//����HTML��Ԫ��
	protected Document doc = null;
	protected Elements tempElement = null;


	protected Element topicElement = null;
	protected Element dateElement = null;
	protected Element locationElement = null;
	protected Element readNumElement = null;
	
	protected String URL = null;
	protected String Date = null;
	//���ݿ���
	protected SQLiteDatabase mySQLiteDatabase = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.job_detail_activity);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.progressbar);
		TextView titleTv = (TextView) findViewById(R.id.progressTitle);
		titleTv.setText("����");
		Pb = (ProgressBar) findViewById(R.id.progressbar);
		imageButton = (ImageButton)findViewById(R.id.progressImageBtn);
		//�������ؼ� ������һ��
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
			//����HTML��ȡ����,ʱ�䣬�ص�Ԫ��
			tempElement = doc.getElementsContainingOwnText("����");
			//Log.v("tag", tempElement.text());
			topicElement = tempElement.first().nextElementSibling();
			
			tempElement.clear();
			
			tempElement = doc.getElementsContainingOwnText("ʱ��");
			dateElement = tempElement.first().nextElementSibling();
			tempElement.clear();
			
			tempElement = doc.getElementsContainingOwnText("�ص�");
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
			remindButton.setText("��ע");
			remindButton.setColor(Color.GRAY);
			remindButton.setBackgroundColor(Color.TRANSPARENT);
			
			shareButton = (MyImageButton)findViewById(R.id.shareButton);
			shareButton.setText("����");
			shareButton.setColor(Color.GRAY);
			shareButton.setBackgroundColor(Color.TRANSPARENT);
			//�������ؼ� ������һ��
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
			//ĳЩ��ҳʱ��ȡ���� ������Ҫ�޸�
			if(dateElement != null)
				dateTv.setText(dateElement.ownText());
			//��Jobactivity��ȡ����date��ֹ����ʱȡ����
			else
				dateTv.setText(Date);
			if(locationElement != null)
				locationTv.setText(locationElement.ownText());
			if(readNumTv != null)
				readNumTv.setText(readNumElement.ownText());
			
			//�������Ѱ�ť �����ݱ�������
			remindButton.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					mySQLiteDatabase = getApplicationContext()
							.openOrCreateDatabase("myRemindJob.db",MODE_PRIVATE, null);
				//�жϱ��Ƿ��Ѵ��� �����ڵĻ��½����
				String isTableExists = "SELECT * FROM sqlite_master WHERE type='table' and name='remind_job' ORDER BY name";
				Cursor cur = mySQLiteDatabase.rawQuery(isTableExists,null);
				if(cur != null){
						//������ �����±�
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
				
				
				/*��ת������ӵ�����ҳ��*/
				Intent intent2 = new Intent();
				intent2.setClass(JobDetailActivity.this, RemindJobActivity.class);
				startActivity(intent2);
				JobDetailActivity.this.finish();
				}
			});
			
			
			
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
	
}
