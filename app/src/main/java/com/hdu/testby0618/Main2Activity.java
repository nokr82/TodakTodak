package com.hdu.testby0618;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Main2Activity extends AppCompatActivity {





    ListView list;
    Button btnAdd;
    String[] Title = {"고민상담","연애상담","맛집추천","재밌는썰","심쿵썰","공포썰"
            ,"오늘하루"};

    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("메인리스트");

        list = findViewById(R.id.list);
        btnAdd = findViewById(R.id.btnAdd);
        adapter  = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Title);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it2 = getIntent();
                String strid = it2.getStringExtra("ID");


                Intent newit = new Intent(getApplicationContext(),Main5Activity.class);
                newit.putExtra("ID",strid);

                startActivity(newit);



            }
        });



        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it2 = getIntent();
                String strid = it2.getStringExtra("ID");
                switch (position){
                    case 0:
                        Intent it = new Intent(getApplicationContext(),Main3Activity.class);
                        String cate = "고민상담";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;
                    case 1:
                        it = new Intent(getApplicationContext(),Main3Activity.class);
                        cate = "연애상담";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;
                    case 2:
                        it = new Intent(getApplicationContext(),Main3Activity.class);
                        cate = "맛집추천";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;
                    case 3:
                        it = new Intent(getApplicationContext(),Main3Activity.class);
                        cate = "재밌는썰";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;
                    case 4:
                        it = new Intent(getApplicationContext(),Main3Activity.class);
                        cate = "심쿵썰";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;
                    case 5:
                        it = new Intent(getApplicationContext(),Main3Activity.class);
                        cate = "공포썰";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;
                    case 6:
                        it = new Intent(getApplicationContext(),Main3Activity.class);
                        cate = "오늘하루";

                        it.putExtra("ID",strid);
                        it.putExtra("CATE",cate);

                        startActivity(it);

                        break;

                }
            }
        });
    }//온

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //기본적인 옵션메뉴처리는 부모크래스에서 해주기떄문에
        //super.onCreateOptionsMenu(menu)를 호출한다.
        super.onCreateOptionsMenu(menu);


        MenuInflater minf = getMenuInflater();

        minf.inflate(R.menu.menu1,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.itemChat:
                Intent it = getIntent();
                String strid = it.getStringExtra("ID");
                Intent chit = new Intent(getApplicationContext(),ChatActivity.class);
                chit.putExtra("ID",strid);
                startActivity(chit);

               // Intent newit = new Intent(getApplicationContext(),Main5Activity.class);
               // newit.putExtra("ID",strid);

               // startActivity(newit);




                break;
        }

        return true;
    }


}//클래스
