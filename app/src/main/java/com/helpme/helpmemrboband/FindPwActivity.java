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
        dialog.setTitle("???????????? ?????? ???");
        dialog.setMessage("??????????????? ????????? ???????????? ????????????.");

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
        menu_page3 = findViewById(R.id.menu_page3); //  ?????? ?????????
        menu_page4 = findViewById(R.id.menu_page4); //  ?????? ?????????
        menu_page5 = findViewById(R.id.menu_page5); //  ?????? ?????????
        menu_page6 = findViewById(R.id.menu_page6); //  ????????? ?????????

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
                    //menu_page3.setVisibility(View.INVISIBLE);   //  ?????? ?????????
                    //menu_page4.setVisibility(View.INVISIBLE);   //  ?????? ?????????
                    menu_page5.setVisibility(View.INVISIBLE);   //  ?????? ?????????
                    //menu_page6.setVisibility(View.INVISIBLE);   //  ????????? ?????????
                    menu_page1.startAnimation(translateLeftAnim);
                }
            }
        });
/**************************************************************************************************/

/************************************** ?????? ????????? ????????? ?????? ***************************************/
        //  ?????? ?????????(MainActivity??? ?????? ???. ????????? ?????? ??????
        button_page2 = findViewById(R.id.roulettePage);
        button_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //  ?????? ????????? ??????
        button_page3 = findViewById(R.id.mapPage);
        button_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Information.class);
                startActivity(intent);
            }
        });

        //  ?????? ????????? ??????
        button_page4 = findViewById(R.id.reviewPage);
        button_page4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //    startActivity(intent);
            }
        });

        //  ?????? ????????? ??????
        button_page5 = findViewById(R.id.myPage);
        button_page5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), MyPageWebView.class);
                //startActivity(intent);
            }
        });


        //  ????????? ????????? ??????
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
                connection.setDoOutput(true); //?????? ?????? ????????? POST??? ??????.
                OutputStream out = connection.getOutputStream();
                out.flush();
                out.close();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i(TAG, "HTTP OK ??????");

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
                    Log.i(TAG, " HTTP OK??????");
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

    //JSON???????????? ?????? ????????? ?????????
    public String jsonParser(String data){
        StringBuffer sb = new StringBuffer();

        try{
            JSONObject jsonObject = new JSONObject(data);
            int success = Integer.parseInt(jsonObject.getString("findId"));
            if(success ==1){
                sb.append("???????????? ?????? ??????^^\n");
                JSONObject memberInfo = jsonObject.getJSONObject("memberInfo");
                String name = memberInfo.getString("name").toString();
                String email = memberInfo.getString("email").toString();
                String id = memberInfo.getString("id").toString();
                String pass = memberInfo.getString("pass").toString();

                //???????????? ????????? ???????????? ??????
                SharedPreferences sf = getSharedPreferences(sessionKey,0);
                String sid = sf.getString("id","");

                if(!(sid.equals("")|| sid == null)){
                    Toast.makeText(this, "?????? ?????? ??????."+sid,Toast.LENGTH_SHORT).show();

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            GmailSender gMailSender = new GmailSender("yoooomi030@gmail.com", "ymiumi88@@");
                            //GMailSender.sendMail(??????, ????????????, ????????????);
                            try {
                                gMailSender.sendMail( name +"?????? ??????????????? ??????????????????.", "????????????:" + pass , "uandme1010@naver.com" );
                            }catch(SendFailedException e) {

                                //?????????????????? Toast??? ????????? ????????? runOnUiThread??? ???????????? ??????.
                                FindPwActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }catch(MessagingException e){
                                System.out.println("????????? ??????"+e);

                                //?????????????????? Toast??? ????????? ????????? runOnUiThread??? ???????????? ??????.
                                FindPwActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"????????? ????????? ?????? ??? ????????????", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //?????????????????? Toast??? ????????? ????????? runOnUiThread??? ???????????? ??????.
                            FindPwActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }.start();
                }

                Toast.makeText(this, "??????????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();

                sb.append("===================\n");
                sb.append("??????: "+ name +"\n");
                sb.append("?????????: "+ email +"\n");
                sb.append("?????????: "+ id +"\n");
                sb.append("??????????????? ????????? ????????????????????????. \n");

            }else{
                sb.append("???????????? ?????? ?????? ??????");
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
                //menu_page3.setVisibility(View.VISIBLE); //  ?????? ?????????
                //menu_page4.setVisibility(View.VISIBLE); //  ?????? ?????????
                menu_page5.setVisibility(View.VISIBLE); //  ?????? ?????????
                //menu_page6.setVisibility(View.VISIBLE); //  ????????? ?????????
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