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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.List;

public class Main4Activity extends AppCompatActivity {

    TextView txContent2,txLike,txBad,txComment,txName,txLove;
    EditText edAdd;
    Button btAdd2;
    ListView list3;
    ImageButton btLike,btBad,btLove;





    ArrayList<Chatdata> arrayList = new ArrayList<>();

    MyAdapter mad;



    int like=0;
    int bad = 0;

    String strNum;//글번호
    String strCommNum;

    int pos;
    //받아오는 좋아요수
    int like2;
    int bad2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        setTitle("상세 화면");



        btLike = findViewById(R.id.btLike);
        btBad = findViewById(R.id.btBad);
        txContent2 = findViewById(R.id.txContent2);
        edAdd = findViewById(R.id.edAdd);
        btAdd2 = findViewById(R.id.btAdd2);
        list3 = findViewById(R.id.list3);
        txLike = findViewById(R.id.txLike);
        txBad = findViewById(R.id.txBad);






        mad = new MyAdapter(this);
        list3.setAdapter(mad);

        Intent upit = getIntent();
        String str = upit.getStringExtra("TI");
        txContent2.setText(str);
        pos = upit.getIntExtra("POS",0);
        strNum = String.valueOf(pos);
        like2 = upit.getIntExtra("LIKE",0);
        txLike.setText(String.valueOf(like2));
        bad2 = upit.getIntExtra("BAD",0);
        txBad.setText(String.valueOf(bad2));



        btAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent upit = getIntent();
                pos = upit.getIntExtra("POS",0);
                strNum = String.valueOf(pos);
                String name = upit.getStringExtra("ID");

                HttpCommentTask task1 = new HttpCommentTask();
                String comment =  edAdd.getText().toString();

                task1.execute(strNum,comment,name);
                HttpListTask task = new HttpListTask();
                task.execute("댓글 목록",strNum);
            }
        });





    //좋아요싫어요버튼클릭
        btBad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bad2++;
                txBad.setText(String.valueOf(bad2));
                String like = txLike.getText().toString();
                String.valueOf(like);
                String bad = txBad.getText().toString();
                String.valueOf(bad);
                HttpLikeTask task = new HttpLikeTask();
                task.execute(strNum,like,bad);


            }
        });
        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like2++;
                txLike.setText(String.valueOf(like2));
                String like = txLike.getText().toString();
                String.valueOf(like);
                String bad = txBad.getText().toString();
                String.valueOf(bad);
                HttpLikeTask task = new HttpLikeTask();
                task.execute(strNum,like,bad);

            }
        });//좋아요싫어요버튼클릭



    }//온

    //어뎁터
    class MyAdapter extends BaseAdapter {
        Context con;
        MyAdapter(Context c){
            con = c;
        }

        @Override
        public int getCount() {
            return arrayList.size();
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
                convertView = lif.inflate(R.layout.list_item3,parent,false);
            }
            txComment = convertView.findViewById(R.id.txComment);
            final TextView txLove = convertView.findViewById(R.id.txLove);
            txName = convertView.findViewById(R.id.txName);
            //btLove = convertView.findViewById(R.id.btLove);
            final Chatdata cd = arrayList.get(position);
            btLove = convertView.findViewById(R.id.btLove);




            txName.setText(cd.cName+": ");
            txLove.setText(String.valueOf(cd.cLove));
            txComment.setText(cd.cComment);


            final int index = position;


            btLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //txLove.setText(String.valueOf(love));
                    //strNum = String.valueOf(pos);
                    //strNum = String.valueOf(index);
                    strCommNum = String.valueOf(arrayList.get(index).cNo);

                    int ilove = arrayList.get(index).cLove + 1;

                    String love = String.valueOf(ilove);

                    HttpLoveTask task = new HttpLoveTask();
                    task.execute(strCommNum,love);


                }
            });


            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpListTask task = new HttpListTask();
        Intent upit = getIntent();
        pos = upit.getIntExtra("POS",0);
        strNum = String.valueOf(pos);
        task.execute("댓글 목록",strNum);
    }


    //좋아요싫어요 버튼처리
    class HttpLikeTask extends AsyncTask<String, Void, String> {
        String address;
        String sendMsg, reciveMsg;

        ProgressDialog dlg = new ProgressDialog(Main4Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/NogariLike.jsp";
            dlg.setMessage("");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                 conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "NO=" + strings[0]+"&LIKE="+strings[1]+"&BAD="+strings[2];
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
    }//좋아요~

    //댓글입력
    class HttpCommentTask extends AsyncTask<String, Void, String> {
        String address;
        String sendMsg, reciveMsg;

        ProgressDialog dlg = new ProgressDialog(Main4Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/Comment.jsp";
            dlg.setMessage("댓글쓰는중...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

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

                sendMsg = "COL=" + strings[0]+"&COMMENT="+strings[1]+"&NAME="+strings[2];
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




    //댓글출력
    class HttpListTask extends AsyncTask<String,Void,String> {
        String address;
        String sendMsg,reciveMsg;

        ProgressDialog listdlg = new ProgressDialog(Main4Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/CommentList.jsp";

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

                sendMsg="MSG="+strings[0]+"&COL="+strings[1];

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
                    arrayList.clear();
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
                                }else if (tag.equals("COL")){
                                    String col = parser.nextText();
                                    //숫자 문자열로 전환
                                    cdb.cCol = Integer.parseInt(col);
                                }else if (tag.equals("COMMENT")){
                                    cdb.cComment = parser.nextText();
                                }else if (tag.equals("LOVE")){
                                    String love = parser.nextText();
                                    //숫자 문자열로 전환
                                    cdb.cLove = Integer.parseInt(love);
                                }else if (tag.equals("NAME")){
                                    cdb.cName = parser.nextText();
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                tag = parser.getName();
                                if (tag.equals("CONTENT")){
                                    arrayList.add(cdb);
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

    //댓글하투
    class HttpLoveTask extends AsyncTask<String, Void, String> {
        String address;
        String sendMsg, reciveMsg;

        ProgressDialog dlg = new ProgressDialog(Main4Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/CommentLove.jsp";
            dlg.setMessage("");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

            HttpListTask task = new HttpListTask();
            task.execute("댓글목록", strNum);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "NO=" + strings[0]+"&LOVE="+strings[1];
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




