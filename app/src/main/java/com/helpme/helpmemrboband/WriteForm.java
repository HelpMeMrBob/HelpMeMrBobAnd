package com.helpme.helpmemrboband;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WriteForm extends AppCompatActivity {

    final private String TAG ="KOSMO";
    ProgressDialog dialog;
    TextView textResult;
    EditText title_et, content_et, user_id;
    Button reg_button;
    TextView textResultId,textResultContent, textResultTitle;
    private ListViewAdapter adapter;
    private ListView listview;

    ListActivity list;
    ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_form);

        items =new ArrayList<String>();

        adapter = new ListViewAdapter();

        title_et = findViewById(R.id.title);
        content_et= findViewById(R.id.content);
        reg_button=findViewById(R.id.reg_button);




        reg_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //listview = (ListView) findViewById(R.id.listview);
                listview.setAdapter(adapter);
                String ip= getResources().getString(R.string.ip);
                String requestUrl =
                        "http://"+ip+":8081/helpmemrbob/android/writeBoard.do";
                requestUrl +="?id="+user_id.getText().toString();
                requestUrl +="&title="+title_et.getText().toString();
                requestUrl +="&content="+content_et.getText().toString();

                //new AsyncHttpRequest().execute(requestUrl);

                //게시물 등록함수

               // adapter.addItem("test1",title_et.getText().toString(),R.drawable.gorokae,content_et.getText().toString());
                adapter.addItem("아이디6","제목6", R.drawable.gorokae,"내용6");
                adapter.notifyDataSetChanged();
                // RegBoard regBoard = new RegBoard();
                //regBoard.execute("test", title_et.getText().toString(),content_et.getText().toString());
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
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
            String resultId = jsonParserId(s);
            String resultTitle = jsonParserTitle(s);
            String resultContent = jsonParserContent(s);
            textResultId.setText(resultId);
            textResultTitle.setText(resultTitle);
            textResultContent.setText(resultContent);

        }
    } //AsyncHttpRequest

    //JSON데이터에 따라 내용은 달라짐
    public String jsonParserId(String data){
        StringBuffer sb = new StringBuffer();

        try{
            JSONObject jsonObject = new JSONObject(data);
            int success1 = Integer.parseInt(jsonObject.getString("isRegist"));

            if(success1 ==1){
                sb.append("회원가입 멤버등록 성공^^\n");
                JSONObject board = jsonObject.getJSONObject("board");
                String id = board.getString("id").toString();
                String title = board.getString("title").toString();
                String content = board.getString("content").toString();

                sb.append("===================\n");
                sb.append("아이디: "+id+"\n");

            }else{
                sb.append("회원가입 실패 ㅜㅜ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String jsonParserTitle(String data){
        StringBuffer sb = new StringBuffer();

        try{
            JSONObject jsonObject = new JSONObject(data);
            int success1 = Integer.parseInt(jsonObject.getString("isRegist"));

            if(success1 ==1){
                sb.append("회원가입 멤버등록 성공^^\n");
                JSONObject board = jsonObject.getJSONObject("board");
                String id = board.getString("id").toString();
                String title = board.getString("title").toString();
                String content = board.getString("content").toString();

                sb.append("===================\n");
                sb.append("제목: "+title+"\n");

            }else{
                sb.append("회원가입 실패 ㅜㅜ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String jsonParserContent(String data){
        StringBuffer sb = new StringBuffer();

        try{
            JSONObject jsonObject = new JSONObject(data);
            int success1 = Integer.parseInt(jsonObject.getString("isRegist"));

            if(success1 ==1){
                sb.append("회원가입 멤버등록 성공^^\n");
                JSONObject board = jsonObject.getJSONObject("board");
                String id = board.getString("id").toString();
                String title = board.getString("title").toString();
                String content = board.getString("content").toString();

                sb.append("===================\n");
                sb.append("내용: "+content+"\n");

            }else{
                sb.append("회원가입 실패 ㅜㅜ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }


}