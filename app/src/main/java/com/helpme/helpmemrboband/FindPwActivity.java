package com.helpme.helpmemrboband;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class FindPwActivity extends AppCompatActivity {

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

    SharedPreferences sharedPreferences;

    public static final String TAG = "MrBOB";

    String sessionKey = "SESSION_INFO";
    EditText etName, etEmail, etId;
    TextView ResultPwd;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        sharedPreferences = getSharedPreferences("SESSION_INFO", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "empty");

        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("비밀번호 찾는 중");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");

        ResultPwd = findViewById(R.id.ResultPwd);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etId = findViewById(R.id.etId);

        Button btn_ResultPwd = findViewById(R.id.btn_ResultPwd);
        Button btn_GoLogin = findViewById(R.id.btn_GoLogin);

        btn_ResultPwd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String requestUrl =
                        "http://192.168.219.105:8081/helpmemrbob/android/findId.do";

                requestUrl +="?name="+ etName.getText().toString();
                requestUrl +="&email="+ etEmail.getText().toString();

                new AsyncHttpRequest().execute(requestUrl);
            }
        });
        btn_GoLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });


        /****************************************************/
        menu_page1 = findViewById(R.id.menu_page1);
        menu_page2 = findViewById(R.id.menu_page2);
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
                    //menu_page2.setVisibility(View.INVISIBLE);
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
                //    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //    startActivity(intent);
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

    class AsyncHttpRequest extends AsyncTask<String, Void, String> {

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
            ResultPwd.setText(result);
        }
    } //AsyncHttpRequest

    //JSON데이터에 따라 내용은 달라짐
    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();

        try{
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("findId"));
            if(success ==1){
                sb.append("비밀번호 찾기 성공^^\n");
                JSONObject memberInfo = jsonObject.getJSONObject("memberInfo");
                String name = memberInfo.getString("name").toString();
                String email = memberInfo.getString("email").toString();
                String id = memberInfo.getString("id").toString();
                String pass = memberInfo.getString("pass").toString();

                //로그인에 성공한 입력한값 입력
                SharedPreferences sf = getSharedPreferences(sessionKey,0);
                String sid = sf.getString("id","");

                if(!(sid.equals("")|| sid == null)){
                    Toast.makeText(this, "회원 정보 있음."+sid,Toast.LENGTH_SHORT).show();

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            GmailSender gMailSender = new GmailSender("yoooomi030@gmail.com", "ymiumi88@@");
                            //GMailSender.sendMail(제목, 본문내용, 받는사람);
                            try {
                                gMailSender.sendMail( name +"님의 비밀번호를 알려드립니다.", "비밀번호:" + pass , "uandme1010@naver.com" );
                            }catch(SendFailedException e) {

                                //쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                                FindPwActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }catch(MessagingException e){
                                System.out.println("인터넷 문제"+e);

                                //쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                                FindPwActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"인터넷 연결을 확인 해 주십시오", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //쓰레드에서는 Toast를 띄우지 못하여 runOnUiThread를 호출해야 한다.
                            FindPwActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "송신 완료", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }.start();
                }

                Toast.makeText(this, "비밀번호를 메일로 발송했습니다.", Toast.LENGTH_LONG).show();

                sb.append("===================\n");
                sb.append("이름: "+ name +"\n");
                sb.append("이메일: "+ email +"\n");
                sb.append("아이디: "+ id +"\n");
                sb.append("비밀번호를 메일로 안내해드렸습니다. \n");

            }else{
                sb.append("비밀번호 찾기 실패 ㅜㅜ");
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
                //menu_page2.setVisibility(View.VISIBLE);
                //menu_page3.setVisibility(View.VISIBLE); //  리뷰 페이지
                //menu_page4.setVisibility(View.VISIBLE); //  지도 페이지
                menu_page5.setVisibility(View.VISIBLE); //  마이 페이지
                //menu_page6.setVisibility(View.VISIBLE); //  글쓰기 페이지
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