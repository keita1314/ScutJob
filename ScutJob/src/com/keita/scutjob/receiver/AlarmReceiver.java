package com.keita.scutjob.receiver;
/*
 * @author Guan Ji Da
 * 事件提醒接收类
 */
import com.keita.scutjob.R;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
	protected Context context;
	protected static final int NOTIFICAITON_ID = 1001;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;
		String idStr = intent.getStringExtra("id");
		String topic = intent.getStringExtra("topic_broadcast");
		String location = intent.getStringExtra("location_broadcast");
		Log.v("receiver", "match");    
		Log.v("receiver", idStr);
		Log.v("receiver", topic);
		Log.v("receiver", location);

				if(intent.getAction().equals(idStr+topic)){
			
					showNotification(topic,location);
			
			}
	}
	public void showNotification(String topic,String location){
		Log.v("no", topic);
		Log.v("no", location);
		//生成状态栏通知
		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon,topic,System.currentTimeMillis());
		//添加声音
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, topic, location, pi);
		notificationManager.notify(NOTIFICAITON_ID,notification);
	}
}
