package com.helpme.helpmemrboband;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Animation translateLeftAnim;
    private Animation translateRightAnim;

    private boolean isPage = false;

    private LinearLayout menu_page1;
    private FrameLayout menu_page2;
    private Button button_page2;
    private Button button_page3;
    private Button button_page4;
    private Button button_page5;
    private Button button_page6;

    private FrameLayout menu_page3;
    private FrameLayout menu_page4;
    private FrameLayout menu_page5;
    private FrameLayout menu_page6;
    private ImageButton menu_button;

    public static final String TAG = "kosmoLogin";
    String sessionKey = "SESSION_INFO";
    EditText user_id, user_pw;
    TextView textResult;
    ProgressDialog dialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("로그인 처리중");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");

        textResult = findViewById(R.id.text_result);
        user_id = findViewById(R.id.user_id);
        user_pw = findViewById(R.id.user_pw);

        Button btn_login = findViewById(R.id.btn_login);
        Button btn_RegistPage = findViewById(R.id.btn_RegistPage);
        Button btn_GoList = findViewById(R.id.btn_Golist);
        Button btn_findIdPw = findViewById(R.id.btn_findIdPw);

        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String requestUrl = "http://" + getString(R.string.server_addr)+
                        ":8081/helpmemrbob/android/memberLogin.do";
                requestUrl +="?id="+user_id.getText().toString();
                requestUrl +="&pass="+user_pw.getText().toString();

                new AsyncHttpRequest().execute(requestUrl);

                sharedPreferences = getSharedPreferences(sessionKey, MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit(); //sharedPreferences를 제어할 editor를 선언
                editor.putString("id",user_id.getText().toString()); // key,value 형식으로 저장
                editor.putString("pass", user_pw.getText().toString());
                editor.commit();    //최종 커밋. 커밋을 해야 저장이 된다.
            }
        });
        btn_RegistPage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemberRegistActivity.class);
                startActivity(intent);
            }
        });
        btn_GoList.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btn_findIdPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindActivity.class);
                startActivity(intent);
            }
        });



        /****************************************************/
        menu_page1 = findViewById(R.id.menu_page1);
        menu_page2 = findViewById(R.id.menu_page2); //  룰렛 페이지
        menu_page3 = findViewById(R.id.menu_page3); //  지도 페이지
        menu_page4 = findViewById(R.id.menu_page4); //  리뷰 페이지
        menu_page5 = findViewById(R.id.menu_page5); //  마이 페이지
        menu_page6 = findViewById(R.id.menu_page6); //  글쓰기 페이지

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
                    //menu_page4.setVisibility(View.INVISIBLE);   //  리뷰 페이지
                    menu_page5.setVisibility(View.INVISIBLE);   //  마이 페이지
                    //menu_page6.setVisibility(View.INVISIBLE);   //  글쓰기 페이지
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
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });

        //  마이 페이지 이동
        button_page5 = findViewById(R.id.myPage);
        button_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), MyPageWebView.class);
                //startActivity(intent);
            }
        });

        //  글쓰기 페이지 이동
        button_page6 = findViewById(R.id.write_form);
        button_page6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferences = getSharedPreferences(sessionKey, MODE_PRIVATE);
                String id = sharedPreferences.getString("id", "empty");

                if (id.equals("empty")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), WriteForm.class);
                    startActivity(intent);
                }
            }
        });
    }

    class AsyncHttpRequest extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            if(!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();

            try{
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true); //요게 있음 무조건 POST가 된다.
                OutputStream out = connection.getOutputStream();
                out.flush();
                out.close();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "HTTP OK 성공");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8")
                    );
                    String responseData;

                    while((responseData = reader.readLine()) != null){
                        sBuffer.append(responseData+"\r\n");
                    }
                    reader.close();
                }
                else{
                    Log.i(TAG, " HTTP OK안됨");
                }
                Log.i(TAG, sBuffer.toString());


            }catch (Exception e){
                e.printStackTrace();
            }
            return sBuffer.toString();
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            Log.i(TAG,s);
            dialog.dismiss();
            String result = jsonParser(s);
            textResult.setText(result);
        }
    } //AsyncHttpRequest

    //JSON데이터에 따라 내용은 달라짐
    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();

        try{
            JSONObject jsonObject = new JSONObject(data);
           int success = Integer.parseInt(jsonObject.getString("isLogin"));
           if(success ==1){
               sb.append("로그인 성공^^\n");
               JSONObject memberInfo = jsonObject.getJSONObject("memberInfo");
               String id = memberInfo.getString("id").toString();
               String pass = memberInfo.getString("pass").toString();
               String name = memberInfo.getString("name").toString();

               sb.append("===================\n");
               sb.append("아이디: "+id+"\n");
               sb.append("패스워드: "+pass+"\n");
               sb.append("이름: "+name+"\n");

               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
               startActivity(intent);

           }else{
               sb.append("로그인 실패 ㅜㅜ");
           }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }



    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            if (isPage) {
                menu_page1.setVisibility(View.INVISIBLE);
                //menu_page2.setVisibility(View.VISIBLE); //  룰렛 페이지
                //menu_page3.setVisibility(View.VISIBLE); //  리뷰 페이지
                //menu_page4.setVisibility(View.VISIBLE); //  지도 페이지
                menu_page5.setVisibility(View.VISIBLE); //  마이 페이지
                //menu_page6.setVisibility(View.VISIBLE); //  마이 페이지
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

