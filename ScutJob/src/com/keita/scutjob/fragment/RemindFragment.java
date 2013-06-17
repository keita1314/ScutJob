package com.keita.scutjob.fragment;
/*
 *����
 * @authorguan ji da
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import com.keita.scutjob.R;
import com.keita.scutjob.RemindJobActivity.MyAdapter;
import com.keita.scutjob.RemindJobActivity.MyAdapter.ViewHolder;
import com.keita.scutjob.receiver.AlarmReceiver;

import android.support.v4.app.Fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RemindFragment extends Fragment {

	protected SQLiteDatabase mySQLiteDatabase = null;
	
	protected HashMap<String,String> map = null;
	protected List<HashMap<String,String>> data = null;
	protected ImageButton returnButton = null;
	protected TextView titleTv = null;
	protected ListView Lview = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mySQLiteDatabase.close();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mySQLiteDatabase = getActivity().getApplicationContext().openOrCreateDatabase("myRemindJob.db", android.content.Context.MODE_PRIVATE, null);
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
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		data = new ArrayList<HashMap<String,String>>();
		 View view = inflater.inflate(R.layout.listview, container, false);
		 titleTv =(TextView) view.findViewById(R.id.headerTitle);
			returnButton = (ImageButton)view.findViewById(R.id.listViewHeaderImageBtn);
			titleTv.setText("��ע�б�");
			returnButton.setVisibility(View.INVISIBLE);
			Lview = (ListView)view.findViewById(R.id.jobListView);
			getData();
			MyAdapter adapter = new MyAdapter(getActivity().getApplicationContext());
			Lview.setAdapter(adapter);
			returnButton.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//Intent intent = new Intent();
					//intent.setClass(RemindJobActivity.this, JobActivity.class);
					//startActivity(intent);
					mySQLiteDatabase.close();
					getActivity().finish();
				}
				
			});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
public class MyAdapter extends BaseAdapter{
		
		protected LayoutInflater inflater = null;	
		protected ViewHolder viewHolder = null;

		//item�����ݿ��е����� �㲥��receiver
		protected String db_itemId = null;
		protected String topic_broadcast = null;
		protected String location_broadcast = null;
		//protected int pos;
		protected String isRemind = null;
		public MyAdapter(Context context){
			inflater =  LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
			
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int id) {
			// TODO Auto-generated method stub
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int pos = position;
		
			viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.remind_joblist, null);
				//viewHolder.imageView = (ImageView)convertView.findViewById(R.id.alarm);
				viewHolder.topicTv = (TextView)convertView.findViewById(R.id.topic);
				viewHolder.dateTv = (TextView)convertView.findViewById(R.id.Date);
				viewHolder.locationTv = (TextView)convertView.findViewById(R.id.location);
			//	viewHolder.delButton = (ImageButton)convertView.findViewById(R.id.delItemBtn);
				viewHolder.remindButton = (ImageButton)convertView.findViewById(R.id.remindBtn);
				convertView.setTag(viewHolder);
			
			}else{
				viewHolder = (ViewHolder)convertView.getTag();
			}
			
		
	
			
			viewHolder.topicTv.setText((String)data.get(position).get("topic"));
			viewHolder.dateTv.setText((String)data.get(position).get("date"));
			viewHolder.locationTv.setText((String)data.get(position).get("location"));
			
			isRemind = data.get(position).get("isRemind");
			if("0".equals(isRemind))
				viewHolder.remindButton.setImageResource(R.drawable.alarm_0);
			else
				viewHolder.remindButton.setImageResource(R.drawable.alarm_1);
			
			//viewHolder.remindButton.setTag(pos);
			//Ϊɾ������Ӽ����¼�
		//	viewHolder.delButton.setOnClickListener(new ListViewButtonListener(position,db_itemId));
			//Ϊ����ʱ����Ӽ����¼���������
			viewHolder.remindButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Log.v("pos", ""+pos);
					db_itemId = data.get(pos).get("id");
					String isRemind_btn = data.get(pos).get("isRemind");
					Log.v("isremind", ""+isRemind_btn);
					topic_broadcast =data.get(pos).get("topic");
					location_broadcast = data.get(pos).get("location");
					//���������¼��ɾ����ʱ��
					//����һ������intent���ַ��� �Ա��¸�activityȡ������
					if("0".equals(isRemind_btn)){
						String remindDate = "";
						if(viewHolder.dateTv.getText() != null)
						 remindDate = (String) viewHolder.dateTv.getText();
						//���͹㲥
						Intent intent = new Intent(getActivity().getApplicationContext(),AlarmReceiver.class);
						intent.putExtra("id", db_itemId);
						Log.v("remind", topic_broadcast);
						
						intent.putExtra("topic_broadcast", topic_broadcast);
						intent.putExtra("location_broadcast", location_broadcast);
						intent.setAction(db_itemId+topic_broadcast);
						PendingIntent sender = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, 0);
						
						Calendar calendar = Calendar.getInstance();
						//calendar.setTimeInMillis(System.currentTimeMillis());
						//����������ڵĺ���ʱ��
						long ms_reminDate = getTimeMillFromString(remindDate);
						//�趨����ʱ��
						//calendar.setTimeInMillis(ms_reminDate) ;
						calendar.setTimeInMillis(System.currentTimeMillis());
						calendar.add(Calendar.SECOND, 5);
						
						AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(android.content.Context.ALARM_SERVICE);
						alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), sender);
						Toast.makeText(getActivity().getApplicationContext(), "���趨�������� �������԰�5������", Toast.LENGTH_LONG).show();   
						//�������ݿ��е�isRemindѡ��
						String updateSql = "UPDATE remind_job SET isRemind = '1' where _id ="+db_itemId;
						
						mySQLiteDatabase.execSQL(updateSql);
						//���»������
						data.clear();
						getData();
						notifyDataSetChanged();
					
					}
					else if("1".equals(isRemind_btn))
					{		
						//ɾ������
						//db_itemId = data.get(pos).get("id");
						Log.v("del alarm", "del"+pos);
						Intent intent = new Intent(getActivity().getApplicationContext(),AlarmReceiver.class);
						Log.v("alarm id", db_itemId);
						intent.setAction(db_itemId+topic_broadcast);
						PendingIntent sender = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent, 0);
						AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(android.content.Context.ALARM_SERVICE);
						alarmManager.cancel(sender);
						Toast.makeText(getActivity().getApplicationContext(), "��ȡ����������", Toast.LENGTH_LONG).show();  
						//�������ݿ��е�isRemindѡ��
						String updateSql = "UPDATE remind_job SET isRemind = '0' where _id ="+db_itemId;
						
						mySQLiteDatabase.execSQL(updateSql);
						//���»������
						data.clear();
						getData();
						notifyDataSetChanged();

					}
				}
			});
			
			//���ó����¼�����
			convertView.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View arg0) {
					// TODO Auto-generated method stub
					Log.v("long click pos", ""+pos);
					/*new AlertDialog.Builder(getActivity().getApplicationContext())
					.setTitle("��ȷ��Ҫɾ����")
					.setMessage("ɾ��ǰ��ȡ������")
					.setNegativeButton("ȡ��", null)
					.setPositiveButton("ȷ��", new OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							db_itemId = data.get(pos).get("id");
							//��data��ɾ��
							data.remove(pos);
							notifyDataSetChanged();
							//�����ݿ���ɾ��
							String delSql = "delete from remind_job where _id="+db_itemId+";";
							mySQLiteDatabase.execSQL(delSql);
							
							Log.v("db", "del done");
							
							
						}
						
					}).show();*/
					db_itemId = data.get(pos).get("id");
					//��data��ɾ��
					data.remove(pos);
					notifyDataSetChanged();
					//�����ݿ���ɾ��
					String delSql = "delete from remind_job where _id="+db_itemId+";";
					mySQLiteDatabase.execSQL(delSql);
					
					Log.v("db", "del done");
					return false;
				}
				
			});
			return convertView;
			
		}
		
		public  class ViewHolder{
			
			//protected ImageView imageView = null;
			protected  TextView topicTv = null;
			protected  TextView dateTv = null;
			protected  TextView locationTv = null;
			protected  ImageButton delButton = null;
			protected  ImageButton remindButton = null;

		} 
		/*
		class ListViewButtonListener implements View.OnClickListener{
			
			protected int position;
			protected String itemId;
			public ListViewButtonListener(int pos,String id){
					position = pos;
					itemId = id;
			}
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(view.getId() == viewHolder.delButton.getId()){
					//��data��ɾ��
					data.remove(position);
					notifyDataSetChanged();
					//�����ݿ���ɾ��
					String delSql = "delete from remind_job where _id="+itemId+";";
					mySQLiteDatabase.execSQL(delSql);
					//ɾ������
					Intent intent = new Intent(RemindJobActivity.this,AlarmReceiver.class);
					intent.setAction(db_itemId);
					PendingIntent sender = PendingIntent.getBroadcast(RemindJobActivity.this, 0, intent, 0);
					AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
					alarmManager.cancel(sender);
					Log.v("db", "del done");
					
				}
				
			}
			
		}
		*/
		public class ListViewRemindBtnListener implements View.OnClickListener{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}
	}
	public void getData(){
		//�����ݿ���ȡ������
				String query = "SELECT * from remind_job";
				Cursor cur = mySQLiteDatabase.rawQuery(query, null);
				
				
				if(cur != null){
					if(cur.moveToFirst()){
						do{
							map = new HashMap<String,String>();
							int colNum = cur.getColumnIndex("_id");
							String id = cur.getString(colNum);
							map.put("id", id);
							
							colNum = cur.getColumnIndex("topic");
							String topic = cur.getString(colNum);
							map.put("topic", topic);
							
							colNum = cur.getColumnIndex("date");
							String date = cur.getString(colNum);
							map.put("date", date);
							
							colNum = cur.getColumnIndex("location");
							String location = cur.getString(colNum);
							map.put("location", location);
							
							colNum = cur.getColumnIndex("isRemind");
							String isRemind = cur.getString(colNum);
							map.put("isRemind",isRemind);
							
							data.add(map);
							
						}while(cur.moveToNext());
					}
				}
	}
	//�ı�ʱ��תΪ����
public long getTimeMillFromString(String dateStr){
		
		//dateStr = "2013��6��6��14ʱ0��";
		int yearIndex = dateStr.indexOf("��");
		int monthIndex = dateStr.indexOf("��");
		int dayIndex = dateStr.indexOf("��");
		int hourIndex = dateStr.indexOf("ʱ");
		int minIndex = dateStr.indexOf("��");
		
		String year = dateStr.substring(0, yearIndex);
		//System.out.println(year);
		
		String month = dateStr.substring(yearIndex+1, monthIndex);
		if(month.length() == 1)
			month ="0"+month;
		//System.out.println(month);
		
		String day = dateStr.substring(monthIndex+1, dayIndex);
		if(day.length() == 1)
			day ="0"+day;

		//System.out.println(day);
		
		String hour = dateStr.substring(dayIndex+1, hourIndex);
		if(hour.length() == 1)
			hour ="0"+hour;
		//System.out.println(hour);
		
		String minute = dateStr.substring(hourIndex+1, minIndex);
		if(minute.length() == 1)
			minute ="0"+minute;
		//System.out.println(minute);
		
		String second = "00";
		
		String newDate = year+month+day+hour+minute+second;
		
		//System.out.println(newDate);
		
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyMMddHHmmss").parse(newDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long ms = c.getTimeInMillis();
		return ms;
		//System.out.println(ms);
	}
}
