package com.helpme.helpmemrboband;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private Animation translateLeftAnim;
    private Animation translateRightAnim;

    private boolean isPage = false;

    private LinearLayout menu_page1;
    private FrameLayout menu_page2;
    private Button button_page2;
    private Button button_page3;
    private Button button_page4;
    private Button button_page5;

    private FrameLayout menu_page3;
    private FrameLayout menu_page4;
    private FrameLayout menu_page5;
    private ImageButton menu_button;

    ListView listView;
    ProgressDialog dialog;//대화상자
    //TextView textResult;
    //안드로이드에서 웹 이미지 가져오쟈
    ImageView imageView;
    Bitmap bitmap;

    private Button write;

    ArrayList<String> item1 = new ArrayList<String>();
    ArrayList<String> item2 = new ArrayList<String>();
    ArrayList<String> item3 = new ArrayList<String>();
    ArrayList<String> item4 = new ArrayList<String>();
    //ArrayList<String> item5 = new ArrayList<String>();
    //ArrayList<String> item6 = new ArrayList<String>();
    //ArrayList<String> item7 = new ArrayList<String>();

    ArrayList<Bitmap> item9 = new ArrayList<Bitmap>();
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        sharedPreferences = getSharedPreferences("SESSION_INFO", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "empty");

        //ProgressDialog객체생성
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setTitle("서버 데이터 수신");
        dialog.setMessage("서버로부터 데이터 수신중입니다.");

        //서버로부터 가져온 JSON을 데이터를 기반으로 출력하기
        String ip= getResources().getString(R.string.ip);
        new AsyncHttpRequest().execute(
                "http://"+ip+":8081/helpmemrbob/android/list.do"
        );

        write = findViewById(R.id.write);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteForm.class);
                startActivity(intent);
            }
        });


        /****************************************************/
        menu_page1 = findViewById(R.id.menu_page1);
        menu_page2 = findViewById(R.id.menu_page2); //  룰렛 페이지
        menu_page3 = findViewById(R.id.menu_page3); //  지도 페이지
        menu_page4 = findViewById(R.id.menu_page4); //  리뷰 페이지
        menu_page5 = findViewById(R.id.menu_page5); //  마이 페이지

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();

        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPage) {
                    menu_page1.startAnimation(translateRightAnim);
                }
                else {
                    menu_page1.setVisibility(View.VISIBLE);
                    //menu_page2.setVisibility(View.INVISIBLE);   //  룰렛 페이지
                    //menu_page3.setVisibility(View.INVISIBLE);   //  지도 페이지
                    menu_page4.setVisibility(View.INVISIBLE);   //  리뷰 페이지
                    //menu_page5.setVisibility(View.INVISIBLE);   //  마이 페이지
                    menu_page1.startAnimation(translateLeftAnim);
                }
            }
        });
/**************************************************************************************************/

/************************************** 버튼 클릭시 페이지 이동 ***************************************/
        //  룰렛 페이지(MainActivity로 설정 중. 나머지 입력 필요
        button_page2 = findViewById(R.id.roulettePage);
        button_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //  지도 페이지 이동
        button_page3 = findViewById(R.id.mapPage);
        button_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Information.class);
                startActivity(intent);
            }
        });

        //  리뷰 페이지 이동
        button_page4 = findViewById(R.id.reviewPage);
        button_page4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                //startActivity(intent);
            }
        });

        //  마이 페이지 이동
        button_page5 = findViewById(R.id.myPage);
        button_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.equals("empty")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    Log.i("check", id + " : if 들어옴");
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyPageWebView.class);
                    startActivity(intent);
                    Log.i("check", id + " : else 들어옴");
                }
            }
        });
    }


    public class CustomList extends ArrayAdapter<String> {

        private final Activity context;

        public CustomList(Activity context){
            super(context, R.layout.item_list, item1);
            this.context = context;
        }

        @Override
        public View getView(int position,View convertView, ViewGroup parent) {

            //레이아웃 전개를 위해 LayoutInflater객체를 생성한다.
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.item_list,null,true);
            //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 원래의 사진 리스트 나오는 곳
            ImageView imageView = (ImageView)rowView.findViewById(R.id.image);
            // TextView item01 = (TextView)rowView.findViewById(R.id.item01); //사진
            TextView item02 = (TextView)rowView.findViewById(R.id.item02); //제목
            TextView item03 = (TextView)rowView.findViewById(R.id.item03); //내용
            TextView item04 = (TextView)rowView.findViewById(R.id.item04); //인덱스
            //TextView item05 = (TextView)rowView.findViewById(R.id.item05);
            //TextView item06 = (TextView)rowView.findViewById(R.id.item06);
            //TextView item07 = (TextView)rowView.findViewById(R.id.item07);


            //영화목록 뷰에 내용 삽입하기
            imageView.setImageBitmap(item9.get(position));
            //item01.setText(item1.get(position));
            item02.setText(item2.get(position));
            item03.setText(item3.get(position));
            item04.setText(item4.get(position));
            //item05.setText(item5.get(position));
            //item06.setText(item6.get(position));
            //item07.setText(item7.get(position));
            return rowView;
        }
    }//CustomList*/
    class AsyncHttpRequest extends AsyncTask<String,Void,String>
    {
        //실행
        @Override
        protected String doInBackground(String... strings) {

            StringBuffer jsonString = new StringBuffer();
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream out = conn.getOutputStream();
                /*out.write(strings[1].getBytes());
                out.write("&".getBytes());
                out.write(strings[2].getBytes());*/
                out.flush();
                out.close();

                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),"UTF-8")
                    );
                    String data;
                    while((data=reader.readLine())!=null){
                        jsonString.append(data+"\r\n");
                    }
                    reader.close();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //JSON Array 파싱
            try {
                //서버에서 가져온 데이터 출력
                Log.i("json성공", jsonString.toString());

                JSONArray jsonArray = new JSONArray(jsonString.toString());
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    /*//getInt(인덱스) : JSON배열에서 정수를 가져올때 사용
                    int tempNum = jsonArray.getInt(i);
                    Log.i("KOSMO37>JSON1", "파싱데이터:" + tempNum);*/

                    JSONObject memberObj = jsonArray.getJSONObject(i);

                    item1.add(memberObj.getString("userfile1"));
                    item2.add(memberObj.getString("title"));
                    item3.add(memberObj.getString("contents"));
                    item4.add(memberObj.getString("id"));
                    //item5.add(memberObj.getString("userfile1"));
                    //item6.add(memberObj.getString("postdate"));
                    //item7.add(memberObj.getString("tag"));


                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //파싱된 모든 내용을 반환한다. 반환된 값은 onPostExecute()로 전달된다.
            return jsonString.toString();
        }

        //doInBackground 메소드 정상종료시 호출되는 메소드
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();//대화창닫기

            Log.i("갖고온 idx", String.valueOf(item1));
            Log.i("갖고온 title", String.valueOf(item2));
            Log.i("갖고온 contents", String.valueOf(item3));
            Log.i("갖고온 id", String.valueOf(item4));
            //Log.i("갖고온 userfile1", String.valueOf(item5));
            //Log.i("갖고온 postdate", String.valueOf(item6));
            //Log.i("갖고온 tag", String.valueOf(item7));

            //textResult.setText(s);
            //커스텀뷰
            CustomList customList = new CustomList(ListActivity.this);
            //Log.i("갖고온 customList", String.valueOf(customList));
            listView = (ListView)findViewById(R.id.list);
            View header = getLayoutInflater().inflate(R.layout.activity_review_bar, null, false);
            listView.addHeaderView(header);
            listView.setAdapter(customList);


            //여기서부터 사진보이는 부분
            imageView = (ImageView)findViewById(R.id.image);
            Thread mThread = new Thread() {

                @Override
                public void run() {
                    try {
                        for (int i = 0; i < item1.size(); i++) {

                            String ip = getResources().getString(R.string.ip);
                            URL url = new URL("http://" + ip + ":8081/helpmemrbob/resources/upload/" + item1.get(i));
                            // Web에서 이미지를 가져온 뒤
                            Log.i("check", item1.get(i));
                            Log.i("check", String.valueOf(url));

                            // ImageView에 지정할 Bitmap을 만든다

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                            conn.setDoInput(true); // 서버로 부터 응답 수신
                            conn.connect();
                            InputStream is = conn.getInputStream(); // InputStream 값 가져오기

                            bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
                            //bitmap = BitmapFactory.decodeByteArray(is);
                         //   imageView.setImageBitmap(bitmap);
                            item9.add(bitmap);
                            Log.i("check 비트맵배열",String.valueOf(item9));
                            //mThread.start();
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start(); // Thread 실행

            try {
                // 메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야한다
                // join()를 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리게 한다
                mThread.join();

                // 작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
                // UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지를 지정한다
               // imageView.setImageBitmap(bitmap);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }////AsyncHttpRequest class



    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            if (isPage) {
                menu_page1.setVisibility(View.INVISIBLE);
                //menu_page2.setVisibility(View.VISIBLE); //  룰렛 페이지
                //menu_page3.setVisibility(View.VISIBLE); //  지도 페이지
                menu_page4.setVisibility(View.VISIBLE); //  리뷰 페이지
                //menu_page5.setVisibility(View.VISIBLE); //  마이 페이지
                isPage = false;
            }
            else {
                isPage = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}