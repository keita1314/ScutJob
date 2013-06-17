package com.keita.scutjob;

import com.keita.scutjob.R;
import com.keita.scutjob.fragment.IndexFragment;
import com.keita.scutjob.fragment.InterFragment;
import com.keita.scutjob.fragment.JobFragment;
import com.keita.scutjob.fragment.RemindFragment;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

public class IndexActivity extends FragmentActivity {
	protected TabHost tabhost = null;
	protected static int currentlayout = 0;
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		OffersManager.getInstance(this).onAppExit();
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle saveInstanceBundle) {
		// TODO Auto-generated method stub
		super.onCreate(saveInstanceBundle);
		setContentView(R.layout.index_activity);
		AdManager.getInstance(this).init("966c6abe22f9268d", "f04e50cd11c259d4", false);
		//setFragment();
		OffersManager.getInstance(this).onAppLaunch();
		tabhost = (TabHost)findViewById(R.id.tabhost);
	
		tabhost.setup();
		changeLayout();
		
		tabhost.setOnTabChangedListener(new OnTabChangeListener(){
			
			
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				//Fragment管理器
				FragmentManager fm = getSupportFragmentManager();
				IndexFragment indexFragment = (IndexFragment) fm.findFragmentByTag("index");
				JobFragment jobFragment = (JobFragment) fm.findFragmentByTag("job");
				InterFragment interFragment = (InterFragment)fm.findFragmentByTag("inter");
				RemindFragment remindFragment = (RemindFragment)fm.findFragmentByTag("remind");
				FragmentTransaction ft = fm.beginTransaction();
				//若fragment非空
				if(indexFragment != null)
					ft.detach(indexFragment);
				if(jobFragment != null)
					ft.detach(jobFragment);
				if(interFragment != null)
					ft.detach(interFragment);
				if(remindFragment != null)
					ft.detach(remindFragment);
				
				if(tabId.equalsIgnoreCase("index")){
					
					if(indexFragment == null){
						ft.add(android.R.id.tabcontent, new IndexFragment(), "index");
					}
					else{
						ft.attach(indexFragment);
					}
				}
				else if(tabId.equalsIgnoreCase("job")){
				
					if(jobFragment == null){
						if(jobFragment == null){
							ft.add(android.R.id.tabcontent, new JobFragment(), "job");
						}
					}else{
						Log.v("job", "attach");
						ft.attach(jobFragment);
					}
				}
				else if(tabId.equalsIgnoreCase("inter")){
					if(interFragment == null){
						ft.add(android.R.id.tabcontent, new InterFragment(), "inter");
					}else{
						ft.attach(interFragment);
					}
				}
				else if(tabId.equalsIgnoreCase("remind")){
					if(remindFragment == null){
						ft.add(android.R.id.tabcontent, new RemindFragment(), "remind");
					}else{
						ft.attach(remindFragment);
					}
				}
				ft.commit();
			}
			
		});
		TabWidget tabWidget = tabhost.getTabWidget();
		//tabWidget.setStripEnabled(false);
		
		//加载tabIndicator
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_info, tabWidget, false);
		TextView tv = (TextView) tabIndicator.findViewById(R.id.my_tab_title);
		ImageView iv = (ImageView)tabIndicator.findViewById(R.id.my_tab_icon);
		tv.setText("首页");
		iv.setImageResource(R.drawable.home);
		
		TabHost.TabSpec spec_index = tabhost.newTabSpec("index");
		spec_index.setIndicator(tabIndicator);
		spec_index.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_index);
		
		View tabIndicator2 = LayoutInflater.from(this).inflate(R.layout.tab_info, tabWidget, false);
		TextView tv2 = (TextView) tabIndicator2.findViewById(R.id.my_tab_title);
		ImageView iv2 = (ImageView)tabIndicator2.findViewById(R.id.my_tab_icon);
		tv2.setText("招聘");
		iv2.setImageResource(R.drawable.job);
		TabHost.TabSpec spec_job = tabhost.newTabSpec("job");
		spec_job.setIndicator(tabIndicator2);
		spec_job.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_job);
		
		View tabIndicator3 = LayoutInflater.from(this).inflate(R.layout.tab_info, tabWidget, false);
		TextView tv3 = (TextView) tabIndicator3.findViewById(R.id.my_tab_title);
		ImageView iv3 = (ImageView)tabIndicator3.findViewById(R.id.my_tab_icon);
		tv3.setText("实习");
		iv3.setImageResource(R.drawable.inter);
		TabHost.TabSpec spec_inter = tabhost.newTabSpec("inter");
		spec_inter.setIndicator(tabIndicator3);
		spec_inter.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_inter);
		
		View tabIndicator4 = LayoutInflater.from(this).inflate(R.layout.tab_info, tabWidget, false);
		TextView tv4 = (TextView) tabIndicator4.findViewById(R.id.my_tab_title);
		ImageView iv4 = (ImageView)tabIndicator4.findViewById(R.id.my_tab_icon);
		tv4.setText("提醒");
		iv4.setImageResource(R.drawable.alarm);
		TabHost.TabSpec spec_remind = tabhost.newTabSpec("remind");
		spec_remind.setIndicator(tabIndicator4);
		spec_remind.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_remind);
		
		/*TabWidget tabWidget = tabhost.getTabWidget();
		for(int i =0; i<tabWidget.getChildCount();i++){
			TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(Color.WHITE);
			
		}*/
		
		
		tabhost.setCurrentTab(0);
		
	}
	protected void changeLayout(){
		tabhost.setCurrentTab(currentlayout);
	}
	protected void setFragment(){
		tabhost = (TabHost)findViewById(R.id.tabhost);
		tabhost.setup();
		
		//设置tabIndicator
		TabHost.TabSpec spec_index = tabhost.newTabSpec("index");
		spec_index.setIndicator("首页");
		spec_index.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_index);
		
		TabHost.TabSpec spec_job = tabhost.newTabSpec("job");
		spec_job.setIndicator("招聘");
		spec_job.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_job);
		
		TabHost.TabSpec spec_inter = tabhost.newTabSpec("inter");
		spec_inter.setIndicator("实习");
		spec_inter.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_inter);
		
		TabHost.TabSpec spec_remind = tabhost.newTabSpec("remind");
		spec_remind.setIndicator("提醒");
		spec_remind.setContent(new DummyTabContent(getBaseContext()));
		tabhost.addTab(spec_remind);
		
		
		
		tabhost.setCurrentTab(0);
	}
 class DummyTabContent implements TabContentFactory{
	 private Context mContext;
	 public DummyTabContent(Context context){
		 mContext = context;
	 }
	@Override
	public View createTabContent(String tag) {
		// TODO Auto-generated method stub
		View v = new View(mContext);
		return v;
	} 
	 
 }
}


