package com.keita.scutjob.fragment;
/*
 *留言
 * @authorguan ji da
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.keita.scutjob.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class MessageFragment extends Fragment {
	protected Document doc = null;
	
	protected Elements askTextElements = null;
	protected Element replyElement = null;
	protected Element consultElement = null;
	protected Elements fontElements = null;
	protected int pageNO = 1;
	protected String countPerPage	= "20";
	
	
	//protected String htmlString = null;
	//protected RelativeLayout Rl = null;
	protected View view = null;
	protected ListView  Lview = null;
	protected View footView = null;
	protected LinearLayout Ll = null;
	protected ProgressBar Pb = null;
	protected ImageButton imageButton = null;
	protected LayoutInflater inflater = null;
	//protected ProgressDialog pDialog = null;
	protected SimpleAdapter adapter = null;
	protected HashMap<String,String> map = null;
	protected List<HashMap<String,String>> data = null;
	protected TextView progressTitle = null;
	//加载下一页面是为true 避免重复加载
	protected boolean isLoading = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		data = new ArrayList<HashMap<String,String>>();
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*if(isLoading == false){
		 view = inflater.inflate(R.layout.progressbar, container, false);
		 
		 
		}
		else{*/
		 view = inflater.inflate(R.layout.message_activity, container, false);
		 Lview = (ListView)view.findViewById(R.id.ListView);
		 Pb =(ProgressBar)view.findViewById(R.id.progressbar);
		 imageButton = (ImageButton)view.findViewById(R.id.listViewHeaderImageBtn);
		 Pb.setIndeterminate(false);
		 Pb.setVisibility(View.VISIBLE);
		 footView = inflater.inflate(R.layout.listview_footer, null);
		 Lview.addFooterView(footView, null, false);
		 Lview.setOnScrollListener(new OnScrollListener(){
				
				boolean isScrollToBottom = false;
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && 
							(Lview.getLastVisiblePosition() == (Lview.getCount()-1) )){
						if(isLoading != true)
							new Thread(runnableNextPage).start();
					
					}
				}
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					
						
				}

			});
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
		 new Thread(runnable).start();
		 
		/*}
		 * */
		 
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	//更新UI
		protected Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if(doc != null){
					askTextElements = doc.getElementsByAttributeValue("height", "94");
					
					for(int i =0;i<askTextElements.size();i++){
						//Log.v("mf", askTextElements.get(i).text());
						map = new HashMap<String,String>();
						fontElements = askTextElements.get(i).getElementsByAttributeValue("color", "#003366");
						consultElement = fontElements.first();
						fontElements = askTextElements.get(i).getElementsByAttributeValue("color", "#FF0000");
						//Log.v("mfask", consultElement.ownText());
						map.put("message", "问:"+consultElement.ownText());
						if(fontElements.isEmpty() != true){
							replyElement = fontElements.first();
							if(replyElement.ownText() != null)
								map.put("reply", "回复:"+replyElement.ownText());
						}
						else
							map.put("reply", "");
						data.add(map);
					}
				
				}
				if(msg.what == 1){
					if(getActivity() != null)	{
					adapter = new SimpleAdapter(getActivity().getApplicationContext(),data,R.layout.message_list,
						new String[]{"message","reply"},
						new int[]{R.id.message,R.id.reply});
					Pb.setVisibility(View.INVISIBLE);
				
					Lview.setAdapter(adapter);
					}
				}
				else{
					if(adapter != null)
						adapter.notifyDataSetChanged();
					//更新完毕
					isLoading = false;
				}
				
				
			}
			
			
		};
	//异步访问网页
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				Message msg = new Message();
				try {
					doc = Jsoup.parse(new URL("http://202.38.193.246:8880/mess/index.asp?page="+pageNO), 10000);
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
		Runnable runnableNextPage = new Runnable(){

			@Override
			public void run() {
				Message msg = new Message();
				try {
					pageNO++;
					doc = Jsoup.parse(new URL("http://202.38.193.246:8880/mess/index.asp?page="+pageNO), 10000);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//发送消息更新UI
				msg.what = 2;
				
				handler.sendMessage(msg);
				
			}
			
		};
}
