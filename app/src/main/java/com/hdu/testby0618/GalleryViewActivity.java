package com.hdu.testby0618;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryViewActivity extends AppCompatActivity {

    Gallery gal;
    ImageView img;

    //포스터 이미지 아이디 배열
    int[] imgid = {
            R.drawable.img1,R.drawable.img2,
            R.drawable.img3,R.drawable.img4,
            R.drawable.img5,R.drawable.img6,
            R.drawable.img7,R.drawable.img8
            ,R.drawable.img9,R.drawable.img10
            ,R.drawable.img14,R.drawable.img11
            ,R.drawable.img13,R.drawable.img12

    };

    //어댑터 선언
    Myadt myadt ;

    TextView txtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);
        setTitle("위로 갤러리");

        img = (ImageView)findViewById(R.id.img);
        gal = (Gallery)findViewById(R.id.gal);

        //갤러리용 어댑터 생성및 추가

        myadt = new Myadt(this);
        gal.setAdapter(myadt);

    }
    class Myadt extends BaseAdapter{
        Context context;

        Myadt(Context c){
            context = c;
        }

        @Override
        public int getCount() {
            return imgid.length;
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
        public View getView(final int position, final View convertView, ViewGroup parent) {
            //이미지뷰하나생성
            final ImageView iview = new ImageView(context);
            //가로 세로 지정
            iview.setLayoutParams(new Gallery.LayoutParams(100,150));
            iview.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iview.setImageResource(imgid[position]);

            //작은포스터 클릭시 이벤트처리
            iview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    img.setImageResource(imgid[position]);
                }
            });

            return iview;
        }
    }

}


