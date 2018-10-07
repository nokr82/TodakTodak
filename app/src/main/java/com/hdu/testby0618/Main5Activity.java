package com.hdu.testby0618;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main5Activity extends AppCompatActivity {

    EditText edContent;
    Button btSave;

    TextView txName;
    Spinner sp;

    String[] cate2 = {"고민상담","연애상담","맛집추천","재밌는썰","심쿵썰","공포썰","오늘하루","노래추천"};
    ArrayAdapter<String> arrayAdapter;

    String strcate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        setTitle("고민입력창");


        sp = findViewById(R.id.sp);
        edContent = findViewById(R.id.edContent);
        btSave = findViewById(R.id.btSave);
        txName = findViewById(R.id.txName);

        Intent newit = getIntent();
        String strid = newit.getStringExtra("ID");

        txName.setText("작성자: "+strid);


        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cate2);

        sp.setAdapter(arrayAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strcate = cate2[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpAddTask task = new HttpAddTask();
                String content = edContent.getText().toString();
                String cate= strcate;
                String id = txName.getText().toString();
                task.execute(content,cate,id);
                finish();
            }
        });



    }
    class HttpAddTask extends AsyncTask<String, Void, String> {
    String address;
    String sendMsg, reciveMsg;

    ProgressDialog dlg = new ProgressDialog(Main5Activity.this);

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        address = "http://192.168.0.18:8080/NogariServer/NogariAdd.jsp";
        dlg.setMessage("고민말하는중...");
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

            sendMsg = "TITLE=" + strings[0]+"&CATE="+strings[1]+"&NAME="+strings[2];
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
}