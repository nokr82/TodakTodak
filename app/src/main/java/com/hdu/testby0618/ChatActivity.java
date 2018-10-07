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
import android.widget.EditText;
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

public class ChatActivity extends AppCompatActivity {

    ListView Chatlist;
    EditText edChat;
    Button btChat;

    TextView txName,txChat;

    ArrayList<Chatdata> arrdata = new ArrayList<>();


    MyAdapter mad;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("토닥토닥 채팅방");

        Chatlist = findViewById(R.id.Chatlist);
        edChat = findViewById(R.id.edChat);
        btChat = findViewById(R.id.btChat);

        mad = new MyAdapter(this);
        Chatlist.setAdapter(mad);


        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAddTask task = new HttpAddTask();
                String chat = edChat.getText().toString();
                Intent chit = getIntent();
                String strid = chit.getStringExtra("ID");
                task.execute(strid,chat);
                HttpListTask task2 = new HttpListTask();

                task2.execute(chat);

            }
        });


        Chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chit = getIntent();
                String strid = chit.getStringExtra("ID");

                final int pos = position;
                String strnum;
                if (strid.equals("111")){
                    HttpDelTask task3 =  new HttpDelTask();

                     strnum = String.valueOf(arrdata.get(pos).cNo);
                     task3.execute(strnum);
                     HttpListTask task = new HttpListTask();
                     task.execute(strnum);

                }
            }
        });





    }//온
    protected void onResume() {
        super.onResume();
        HttpListTask task = new HttpListTask();
       Chatdata cd = new Chatdata();
       String chat = cd.cChat;

        task.execute(chat);
    }


    class MyAdapter extends BaseAdapter {
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
                convertView = lif.inflate(R.layout.list_item2,parent,false);
            }
            txName = convertView.findViewById(R.id.txName);
            txChat = convertView.findViewById(R.id.txChat);



            Chatdata cd = arrdata.get(position);


            txName.setText(cd.cName+": " );
            txChat.setText(cd.cChat);

            return convertView;
        }
    }


    class HttpAddTask extends AsyncTask<String, Void, String> {
        String address;
        String sendMsg, reciveMsg;

        ProgressDialog dlg = new ProgressDialog(ChatActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/NogariChat.jsp";
            dlg.setMessage("고민말하는중...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

            if (reciveMsg.equals("입력성공")) {
                Toast.makeText(getApplicationContext(), reciveMsg, Toast.LENGTH_SHORT).show();

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

                sendMsg = "NAME=" + strings[0]+"&CHAT="+strings[1];
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

    class HttpListTask extends AsyncTask<String,Void,String> {
        String address;
        String sendMsg,reciveMsg;

        ProgressDialog listdlg = new ProgressDialog(ChatActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/Chatlist.jsp";

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

                sendMsg="CHAT="+strings[0];

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
                    Chatdata cdb=null;
                    arrdata.clear();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    cdb = new Chatdata();
                                }else if (tag.equals("NO")){
                                    String no = parser.nextText();
                                    //숫자 문자열로 전환
                                    cdb.cNo = Integer.parseInt(no);
                                }else if (tag.equals("NAME")){
                                    cdb.cName = parser.nextText();
                                }else if(tag.equals("CHAT")){
                                    cdb.cChat = parser.nextText();
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    arrdata.add(cdb);
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

        ProgressDialog dlg = new ProgressDialog(ChatActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/Delete.jsp";
            dlg.setMessage("진열중...");
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
}//클래스
