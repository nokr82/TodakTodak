package com.hdu.testby0618;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    View logview;
    Boolean flag;
    EditText edid,edpass;
    Button btJoin;
    ImageButton btToday;



   // Intent it = new Intent(MainActivity.this,Main2Activity.class);

   // startActivity(it);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("로그인 화면");

        btnStart =  findViewById(R.id.btnStart);
        btToday = findViewById(R.id.btToday);




        btToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(),GalleryViewActivity.class);
                startActivity(it);

            }
        });


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logview = (View)View.inflate(MainActivity.this,R.layout.logdialog,null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("로그인창");
                dlg.setIcon(R.drawable.images);
                dlg.setView(logview);
                btJoin = logview.findViewById(R.id.btJoin);

                btJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag = false;
                        edid = logview.findViewById(R.id.edid);
                        edpass = logview.findViewById(R.id.edpass);

                        String id = edid.getText().toString();
                        String pass = edpass.getText().toString();

                        HttpLogintask task = new HttpLogintask();
                        task.execute(id,pass);

                    }
                });

                dlg.setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent it = new Intent(getApplicationContext(),JoinActivity.class);
                        startActivity(it);

                    }
                });
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent it = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(it);

                    }
                });
                dlg.show();
            }
        });


    }//on
    class HttpLogintask extends AsyncTask<String,Void,String> {
        String address;
        String sendMsg,reciveMsg;

        ProgressDialog dlg = new ProgressDialog(MainActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            address = "http://192.168.0.18:8080/NogariServer/Login.jsp";
            dlg.setMessage("접속중...");
            dlg.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dlg.dismiss();

            //로그인 성공시 다음 화면 실행
            if (reciveMsg.equals("성공")){
                Toast.makeText(getApplicationContext(),"관리자님 반가워요!~",Toast.LENGTH_SHORT).show();

                //인텐트 생성 및 startActivity실행
                Intent it2 = new Intent(MainActivity.this,Main2Activity.class);
                String id =   edid.getText().toString();
                it2.putExtra("ID",id);
                startActivity(it2);
            }else if (reciveMsg.equals("고객성공")){
                Toast.makeText(getApplicationContext(),"반가워!~",Toast.LENGTH_SHORT).show();
                Intent it2 = new Intent(MainActivity.this,Main2Activity.class);
                String id =   edid.getText().toString();
                it2.putExtra("ID",id);
                startActivity(it2);
            }
            else if (reciveMsg.equals("실패")) {
                Toast.makeText(getApplicationContext(), "패스워드오류", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getApplicationContext(),reciveMsg,Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
                conn.setRequestMethod("POST");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

                sendMsg = "ID="+strings[0]+"&PASS="+strings[1];

                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK){
                    //들어온 데이터 xml을 파싱하여 처리
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                    XmlPullParser parser = factory.newPullParser();

                    InputStreamReader isr = new InputStreamReader(conn.getInputStream(),"UTF-8");

                    parser.setInput(isr);

                    int eventType = parser.getEventType();

                    String tag;
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                //태그 이름 추출
                                tag = parser.getName();
                                if (tag.equals("LOGIN")){
                                    //태그 다음의 TEXT추출
                                    reciveMsg = parser.nextText();
                                }else if (tag.equals("STATE")){
                                    reciveMsg = parser.nextText();
                                }

                                break;
                        }
                        eventType = parser.next();
                    }//while
                }


            }catch (Exception e){
                e.printStackTrace();
            }


            return reciveMsg;
        }
    }







}//class
