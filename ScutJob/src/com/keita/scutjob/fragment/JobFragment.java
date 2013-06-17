package com.keita.scutjob.fragment;
/*
 *��Ƹ
 * @authorguan ji da
 */
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

import com.keita.scutjob.JobDetailActivity;
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

public class JobFragment extends Fragment {
	
	protected Document doc = null;
	
	protected String specificHtml = null;
	protected Elements jobElement = null;
	protected Elements releaseDateElement = null;
	protected Elements hrefElement = null;
	protected int pageNO = 1;
	protected String countPerPage	= "20";
	
	
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
	//protected Context myContext = JobActivity.this;
	//protected ProgressDialog pDialog = null;
	protected SimpleAdapter adapter = null;
	protected HashMap<String,String> map = null;
	protected List<HashMap<String,String>> data = null;
	protected TextView progressTitle = null;
	protected ImageView iv = null;
	protected MyHandler handler = null;
	protected boolean isFirstLoad = true;
	//������һҳ����Ϊtrue �����ظ�����
	protected boolean isLoading = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.v("jobfrag", "oncreate");
		handler = new MyHandler();
		new Thread(runnable).start();
		data = new ArrayList<HashMap<String,String>>();
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		Log.v("jobfrag", "oncreateView");
		 View view = inflater.inflate(R.layout.top_fragment, container, false);
		 Lview = (ListView)view.findViewById(R.id.ListView);
		
		 Pb =(ProgressBar)view.findViewById(R.id.progressbar);
		 imageButton = (ImageButton)view.findViewById(R.id.listViewHeaderImageBtn);
		 imageButton_con = (ImageButton)view.findViewById(R.id.headerConsultBtn);
		 tv = (TextView)view.findViewById(R.id.headerTitle);
		 tv.setText("��Ƹ����");
		 imageView = (ImageView)view.findViewById(R.id.img);
		 imageView.setImageResource(R.drawable.annouce);
		 Pb.setIndeterminate(false);
		 footView = inflater.inflate(R.layout.listview_footer, null);
		 Lview.addFooterView(footView, null, false);
		 //��һ�μ��� ��ʾ����Ȧ
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
		 Lview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					// TODO Auto-generated method stub
					
					
					//���ص�textview �����洢������
					TextView tv1 = (TextView) view.findViewById(R.id.href);
					//Toast.makeText(getApplicationContext(), tv.getText(), Toast.LENGTH_SHORT).show();
					TextView tv2 = (TextView)view.findViewById(R.id.releaseDate);
					
					String url = (String) tv1.getText();
					String date = (String)tv2.getText();
					if(url == null)
						Log.v("joburl", "null");
					else
						Log.v("joburl", url);
					//��ת����ϸҳ��
					
					Intent intent = new Intent();
					intent.putExtra("url", url);
					intent.putExtra("date", date);
					intent.setClass(getActivity().getApplicationContext(), JobDetailActivity.class);
					startActivity(intent);
					
					
				
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
		 imageButton_con.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getActivity().getApplicationContext(),MessageConsultActivity.class);
					getActivity().startActivity(intent);
					
				}
				
			});
	//	new Thread(runnable).start();
		isFirstLoad = false;
		return view;
	}
	//fragment is activited
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
		//����UI
		protected class MyHandler extends Handler {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				//��ȡhtml��class����Ϊablue02��Ԫ�� ʵ��Ϊ���±���
			
				if(doc != null){
				jobElement = doc.getElementsByAttributeValue("class", "ablue02");
				releaseDateElement = doc.getElementsByAttributeValue("class", "pl01 line02");
				
				
				for(int i=0;i<jobElement.size();i++){
					map = new HashMap<String,String>();
					map.put("company",jobElement.get(i).ownText());
					map.put("href", jobElement.get(i).attr("abs:href"));
					map.put("releaseDate", releaseDateElement.get(i).ownText());
					data.add(map);
				}
			
			}
			
			//��Ϊ���μ���	������ContentView ��������
				if(msg.what == 1){
//					pDialog.dismiss();
					//Pb.setVisibility(View.GONE);
					if(getActivity()!=null){
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
					//�������
					isLoading = false;
				}
				
			}
			
			
		}
		
		//�첽������ҳ
		Runnable runnable = new Runnable(){

			@Override
			public void run() {
				Message msg = new Message();
				try {
					doc = Jsoup.parse(new URL("http://employ.scut.edu.cn:8880/zpzc/all.jsp"), 20000);
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
		Runnable runnable_SpecificPage = new Runnable(){

			@Override
			public void run() {
				Message msg = new Message();
				isLoading = true;
				pageNO += 1;
				specificHtml = getSpecificPage("http://employ.scut.edu.cn:8880/zpzc/all.jsp",pageNO);
				//������ҳ�ַ���ʱ����Ҫָ��ȡ���ĵ�����վ �Ӷ��ܹ������·��תΪ����·��
				doc = Jsoup.parse(specificHtml,"http://employ.scut.edu.cn:8880/zpzc/all.jsp");	
				msg.what = 2;
				handler.sendMessage(msg);
				
			}
			
		};
		
		//ģ��Http �ύ������ ��ȡ��һҳ�� ��һҳ�������
		/*
		 * urlString ���ύ�ĵ�ַ
		 * pageNO Ҫ�ṩ��ҳ�� ��ȡ�õڼ�ҳ������
		 * ���� html�ı�
		 */
		protected String getSpecificPage(String urlString,int pageNO){
			try{
				Log.v("tag",Integer.toString(pageNO));
				String param = "pageNO="+Integer.toString(pageNO);
				URL url = new URL(urlString);
				HttpURLConnection uc = (HttpURLConnection)url.openConnection();
				uc.setRequestMethod("POST");
				uc.setDoInput(true);
				uc.setInstanceFollowRedirects(true);
				uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				//д��������
				OutputStream os = uc.getOutputStream();
				os.write(param.getBytes());
				os.close();
				//uc.connect();
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
