package com.keita.scutjob.fragment;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.keita.scutjob.InterDetailActivity;
import com.keita.scutjob.MessageConsultActivity;
import com.keita.scutjob.R;


import android.support.v4.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class InterFragment extends Fragment {
	protected Document doc = null;
	protected String specificHtml = null;
	protected Elements noticeElement = null;
	protected Elements releaseDateElement = null;
	protected Elements hrefElement = null;
	protected int pageNum = 1;
	protected String countPerPage	= "15";
	
	
	//protected String htmlString = null;
	//protected RelativeLayout Rl = null;
	protected ListView  Lview = null;
	protected View footView = null;
	protected LinearLayout Ll = null;
	protected ProgressBar Pb = null;
	protected ImageButton imageButton = null;
	protected ImageButton imageButton_con = null;
	protected LayoutInflater inflater = null;
	protected ImageView imageView = null;
	protected TextView tv = null;
	//protected Context myContext = InterActivity.this;
	//protected ProgressDialog pDialog = null;
	protected ImageView iv = null;
	protected HashMap<String,String> map = null;
	protected List<HashMap<String,String>> data = null;
	protected SimpleAdapter adapter = null;
	protected TextView progressTitle = null;
	protected MyHandler handler = null;
	protected boolean isFirstLoad = true;
	//加载下一页面是为true 避免重复加载
	protected boolean isLoading = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		data = new ArrayList<HashMap<String,String>>();
		handler = new MyHandler();
		new Thread(runnable).start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		 View view = inflater.inflate(R.layout.top_fragment, container, false);
		 Lview = (ListView)view.findViewById(R.id.ListView);
		 Pb =(ProgressBar)view.findViewById(R.id.progressbar);
		 imageButton = (ImageButton)view.findViewById(R.id.listViewHeaderImageBtn);
		 imageButton_con = (ImageButton)view.findViewById(R.id.headerConsultBtn);
		 tv = (TextView)view.findViewById(R.id.headerTitle);
		 tv.setText("实习");
		 imageView = (ImageView)view.findViewById(R.id.img);
		 imageView.setImageResource(R.drawable.inter_top);
		 Pb.setIndeterminate(false);
		 footView = inflater.inflate(R.layout.listview_footer, null);
		 Lview.addFooterView(footView, null, false);
		 //第一次加载 显示进度圈
		 if(isFirstLoad == true)
			 Pb.setVisibility(View.VISIBLE);
		 else{
			 Pb.setVisibility(View.INVISIBLE);
			 Lview.setAdapter(adapter);
		 }
		 Lview.setOnScrollListener(new OnScrollListener(){
				
				boolean isScrollToBottom = false;
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && 
							(Lview.getLastVisiblePosition() == (Lview.getCount()-1) )){
						if(isLoading != true)
							new Thread(runnable_SpecificPage).start();
					
					}
				}
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub
					
						
				}

			});
		//点击ListView中选项
			Lview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					// TODO Auto-generated method stub
					
					
					//隐藏的textview 用来存储超链接
					TextView tv1 = (TextView) view.findViewById(R.id.href);
					//Toast.makeText(getApplicationContext(), tv.getText(), Toast.LENGTH_SHORT).show();
					//TextView tv2 = (TextView)view.findViewById(R.id.releaseDate);
					
					String url = (String) tv1.getText();
					//String date = (String)tv2.getText();
					//跳转到详细页面
					if(url != null){
					Intent intent = new Intent();
					intent.putExtra("url", url);
					//intent.putExtra("date", date);
					intent.setClass(getActivity().getApplicationContext(), InterDetailActivity.class);
					startActivity(intent);
					//JobActivity.this.finish();
			
					}
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
			//new Thread(runnable).start();
				imageButton_con.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(getActivity().getApplicationContext(),MessageConsultActivity.class);
						getActivity().startActivity(intent);
						
					}
					
				});
			isFirstLoad = false;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	//更新UI
		protected class MyHandler extends Handler {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				//获取html中class属性为ablue02的元素 实际为文章标题
			
				/*
				if(noticeElement != null)
					noticeElement = null;
				if(releaseDateElement != null)
					releaseDateElement = null;
				*/
				if(doc != null){
				noticeElement = doc.getElementsByAttributeValue("class", "ablue02");
			//	releaseDateElement = doc.getElementsByAttributeValue("class", "pl01 line02");
				String notice = "";
				String notice_new = "";
				String date_new ="";
				//(括号位置 )括号位置
				int start_index =0;
				int end_index=0;
				for(int i=0;i<noticeElement.size();i++){
					map = new HashMap<String,String>();
					notice = noticeElement.get(i).ownText();
					start_index = notice.lastIndexOf("(");
					end_index = notice.lastIndexOf(")");
					notice_new = notice.substring(0, start_index);
					date_new = notice.substring(start_index+1,end_index);
					
					//company的位置对应着事务题目 不用改xml布局
					map.put("company",notice_new);				
					map.put("href", noticeElement.get(i).attr("abs:href"));
					map.put("releaseDate", date_new);
					data.add(map);
				}
			}
			//	pDialog.dismiss();
				
			//若为初次加载	则设置ContentView 否则不设置
				if(msg.what == 1){
					if(getActivity()!= null){
					adapter = new SimpleAdapter(getActivity().getApplicationContext(),data,R.layout.job_list,
							new String[]{"company","releaseDate","href"},
							new int[]{R.id.company,R.id.releaseDate,R.id.href});
					Pb.setVisibility(View.INVISIBLE);
					Lview.setAdapter(adapter);
					//setContentView(Ll);
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
					doc = Jsoup.parse(new URL("http://employ.scut.edu.cn:8880/shixi/index.jsp"), 10000);
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
		//特定页面
		Runnable runnable_SpecificPage = new Runnable(){

			@Override
			public void run() {
				Message msg = new Message();
				isLoading = true;
				pageNum += 1;
				specificHtml = getSpecificPage("http://employ.scut.edu.cn:8880/shixi/index.jsp",pageNum);
				doc = Jsoup.parse(specificHtml,"http://employ.scut.edu.cn:8880/shixi/index.jsp")	;
				msg.what = 2;
				handler.sendMessage(msg);
				
			}
			
		};
		
		//模拟Http 提交表单请求 获取下一页面 上一页面的数据
		/*
		 * urlString 表单提交的地址
		 * pageNum 要提供的页码 即取得第几页面数据
		 * 返回 html文本
		 */
		protected String getSpecificPage(String urlString,int pageNum){
			try{
				Log.v("tag",Integer.toString(pageNum));
				String param = "pageNum="+Integer.toString(pageNum)+"&countPerPage="+countPerPage;
				URL url = new URL(urlString);
				HttpURLConnection uc = (HttpURLConnection)url.openConnection();
				uc.setRequestMethod("POST");
				uc.setDoInput(true);
				uc.setInstanceFollowRedirects(true);
				uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				//写入请求中
				OutputStream os = uc.getOutputStream();
				os.write(param.getBytes());
				os.close();
				
				InputStream is = uc.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer bab = new ByteArrayBuffer(5000);
				
				int cur = 0;
				while((cur = bis.read()) != -1){
					
					bab.append((byte) cur);
				}
				is.close();
				bis.close();
				return EncodingUtils.getString(bab.toByteArray(), "GBK");
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			
		}


}
