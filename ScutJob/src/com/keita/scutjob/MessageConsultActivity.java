package com.keita.scutjob;

import com.keita.scutjob.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;

public class MessageConsultActivity extends FragmentActivity {
	protected TabHost tabhost = null;
	protected static int currentlayout = 0;
	
	@Override
	protected void onCreate(Bundle saveInstanceBundle) {
		// TODO Auto-generated method stub
		super.onCreate(saveInstanceBundle);
		setContentView(R.layout.message_consult_activity);
		setFragment();
		changeLayout();
		
	}
	protected void changeLayout(){
		tabhost.setCurrentTab(currentlayout);
	}
	protected void setFragment(){
		tabhost = (TabHost)findViewById(R.id.tabhost);
		tabhost.setup();
		tabhost.addTab(tabhost.newTabSpec("message").setIndicator("查看留言",null).setContent(R.id.frag_message));
		tabhost.addTab(tabhost.newTabSpec("consult").setIndicator("我要咨询", null).setContent(R.id.frag_consult));
		tabhost.setCurrentTab(0);
	}
}
