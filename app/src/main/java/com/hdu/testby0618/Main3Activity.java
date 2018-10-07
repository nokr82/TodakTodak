package com.hdu.testby0618;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {

    ListView list2;

    TextView txTitle,txLike,txBad;


    ArrayList<Mydata> arrdata = new ArrayList<>();


    MyAdapter mad;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        setTitle("고민목록");

        list2 = findViewById(R.id.list2);


        mad = new MyAdapter(this);
        list2.setAdapter(mad);


        list2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent upit = new Intent(getApplicationContext(),Main4Activity.class);

                Intent it = getIntent();
                String name = it.getStringExtra("ID");

                int pos =  arrdata.get(position).mNo;
                String ti = arrdata.get(position).mTitle;
                int like  = arrdata.get(position).mLike;
                int bad = arrdata.get(position).mBad;



                upit.putExtra("POS",pos);
                upit.putExtra("TI",ti);
                upit.putExtra("LIKE",like);
                upit.putExtra("BAD",bad);
                upit.putExtra("ID",name);


                startActivity(upit);

            }
        });

        list2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = getIntent();
                String name = it.getStringExtra("ID");
                final int pos = position;
                if (name.equals("111")){
                    HttpDelTask task = new HttpDelTask();
                    int strNum = arrdata.get(pos).mNo;
                    task.execute(String.valueOf(strNum));
                    HttpListTask task2 = new HttpListTask();
                    String str = it.getStringExtra("CATE");
                    task2.execute("고민목록",str);
                }


                return false;
            }
        });




    }//온


    @Override
    protected void onResume() {
        super.onResume();
        HttpListTask task = new HttpListTask();
        Intent it = getIntent();
        String str = it.getStringExtra("CATE");
        task.execute("고민목록",str);
    }

    class MyAdapter extends BaseAdapter{
        Context con;
        MyAdapter(Context c){
            con = c;
        }

        @Override
        public int getCount() {
            return arrdata.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                LayoutInflater lif = LayoutInflater.from(con);
                convertView = lif.inflate(R.layout.list_item1,parent,false);
            }
            txTitle = convertView.findViewById(R.id.txTitle);
            txLike = convertView.findViewById(R.id.txLike);
            txBad = convertView.findViewById(R.id.txBad);


            Mydata md = arrdata.get(position);


            txTitle.setText(md.mTitle);
            txBad.setText(String.valueOf(md.mBad));
            txLike.setText(String.valueOf(md.mLike));

            return convertView;
        }
}
    class HttpListTask extends AsyncTask<String,Void,String> {
        String address;
        String sendMsg,reciveMsg;

        ProgressDialog listdlg = new ProgressDialog(Main3Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/Nogari1.jsp";

            listdlg.setMessage("불러오는중");
            listdlg.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            mad.notifyDataSetChanged();
            listdlg.dismiss();

            if (reciveMsg != null){
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg="MSG="+strings[0]+"&CATE="+strings[1];

                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode()==conn.HTTP_OK){
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = factory.newPullParser();


                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");
                    parser.setInput(isr);

                    int eventType = parser.getEventType();
                    String tag = null;
                    //DB에서 가져오는 상품 정보저장
                    Mydata tdb=null;
                    arrdata.clear();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    tdb = new Mydata();
                                }else if (tag.equals("NO")){
                                    String no = parser.nextText();
                                    //숫자 문자열로 전환
                                    tdb.mNo = Integer.parseInt(no);
                                }else if (tag.equals("TITLE")){
                                    tdb.mTitle = parser.nextText();
                                }else if(tag.equals("CATE")){
                                    tdb.mCate = parser.nextText();
                                }else if(tag.equals("LIKE")){
                                    String like = parser.nextText();
                                    tdb.mLike = Integer.parseInt(like);
                                }else if(tag.equals("BAD")){
                                    String bad = parser.nextText();
                                    tdb.mBad = Integer.parseInt(bad);
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    arrdata.add(tdb);
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return reciveMsg;
        }
    }
    class HttpDelTask extends AsyncTask<String, Void, String> {
        String address;
        String sendMsg, reciveMsg;

        ProgressDialog dlg = new ProgressDialog(Main3Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/NogariDelete.jsp";
            dlg.setMessage("삭제중...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

            //reciveMsg에 들어오는 내용에 따라서 실패시 토스트만띄움 성공시는 토스트 띄우고 액티비티 종료

            if (reciveMsg.equals("추가성공")) {
                Toast.makeText(getApplicationContext(), reciveMsg, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), reciveMsg, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "NO=" + strings[0];
                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    //들어온 데이터 xml을 파싱하여 처리
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    parser.setInput(isr);

                    int eventType = parser.getEventType();

                    String tag = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            tag = parser.getName();
                            if (tag.equals("STATE")) {
                                reciveMsg = parser.nextText();
                            }
                        }
                        eventType = parser.next();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return reciveMsg;
        }
    }




}//메인
