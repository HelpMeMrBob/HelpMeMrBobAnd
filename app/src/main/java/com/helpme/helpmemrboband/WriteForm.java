package com.helpme.helpmemrboband;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class WriteForm extends AppCompatActivity {

    final private String TAG ="KOSMO";
    ImageView ivPicture;
    ProgressDialog dialog;
    EditText title_et, content_et;
    String filePath1;//파일의 절대경로
    String fileName;
    String id;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_form);

        sharedPreferences = getSharedPreferences("SESSION_INFO", MODE_PRIVATE);
        id = sharedPreferences.getString("id", "empty");
        Toast.makeText(this, "글쓰기 로그인정보있음."+id,Toast.LENGTH_SHORT).show();

        ivPicture = findViewById(R.id.ivPicture);
        title_et = findViewById(R.id.title);
        content_et= findViewById(R.id.content);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        //통신을 위해서는 꼭 필요함
        dialog = new ProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("글쓰기 처리중");
        dialog.setMessage("서버로부터 응답을 기다리고 있습니다.");
    }

    //이미지 선택
    public void onBtnGetPicture(View v) {
        //인텐트를 통해 타입과 데이터를 설정한 후 선택한다.
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    //파일 업로드
    public void onBtnUpload(View v) {
        //텍스트뷰 내용 비우기

        // tvHtml1.setText("");
        //파라미터를 맵에 저장
        HashMap<String, String> param1 = new HashMap<>();
        param1.put("userid", "홍길동");
        param1.put("userpwd", "패스워드");
        HashMap<String, String> param2 = new HashMap<>();
        param2.put("filename", filePath1);
        Toast.makeText(getApplicationContext(), "파일경로: "+filePath1, Toast.LENGTH_SHORT).show();
        //AsyncTask를 통해 생성한 객체를 통해 HTTP 통신 시작
        UploadAsync networkTask = new UploadAsync(getApplicationContext(), param1, param2, title_et, content_et,id);
        networkTask.execute();

        Log.i(TAG, "fileName2는 "+fileName);
        Log.i(TAG, "filename1은 "+networkTask.jsonParser());
        //doInBackground() 호출

    }
    //앱 종료하기
    public void onBtnFinish(View v) {
        finish();
    }

    //갤러리 리스트에서 사진 데이터를 가져오기 위한 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri selPhotoUri = data.getData();
                showCapturedImage(selPhotoUri);
            }
        }
    }
    //사진의 절대경로 구하기(사용자 정의 메소드)
    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }
    /*
    사진의 회전값을 처리해주는 메소드 : 사진의 회전값을 처리하지 않으면 사진을
        찍은 방향대로 이미지뷰에 설정할 수 없게된다.
     */
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    //사진을 정방향대로 회전하기 위한 메소드
    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0,
                src.getWidth(), src.getHeight(), matrix, true);
    }
    //사진의 절대경로를 구한 후 이미지뷰에 선택한 사진을 설정함.
    private void showCapturedImage(Uri imageUri) {
        filePath1 = getRealPathFromURI(imageUri);//사용자정의함수
        Log.d(TAG, "path1:"+filePath1);
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(filePath1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        int exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);//사용자정의함수

        Bitmap bitmap = BitmapFactory.decodeFile(filePath1);
        Bitmap rotatedBitmap = rotate(bitmap, exifDegree);//사용자정의함수
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 800, 800, false);
        bitmap.recycle();

        ivPicture.setImageBitmap(scaledBitmap);
    }
    //서버와 통신을 위한 클래스
    public class UploadAsync extends AsyncTask<Object, Integer, JSONArray> {

        private Context mContext;
        private HashMap<String, String> param;//파라미터
        private HashMap<String, String> files;//사진파일
        private EditText title, content;
        private String saveFileName;
        private String filename;
        private String id;

        public UploadAsync(Context context, HashMap<String, String> param,
                           HashMap<String, String> files, EditText title, EditText content, String id){
            //HashMap<String, String> files
            mContext = context;
            this.param = param;
            this.files = files;
            this.title = title;
            this.content= content;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(Object... objects) {
            JSONArray rtn = null;
            try {
                String sUrl
                        = getString(R.string.server_addr)
                        + "/helpmemrbob/fileUpload/uploadAndroid.do";
                sUrl +="?id="+id;
                sUrl +="&title="+title_et.getText().toString();
                sUrl +="&contents="+content_et.getText().toString();
                Log.i(TAG, "sUrl "+sUrl);
                FileUpload multipartUpload = new FileUpload(sUrl, "UTF-8");
                rtn = multipartUpload.upload(param, files);
                Log.i(TAG, "rtn string value of "+String.valueOf(rtn));
                Log.i(TAG, "rtn toString "+rtn.toString());
                for(int i=0; i<rtn.length(); i++){
                    JSONObject jsonObject = rtn.getJSONObject(i);
                    saveFileName = jsonObject.getString("saveFileName");
                }
                Log.i(TAG, "save toString "+saveFileName);
                filename= getSaveFileName(saveFileName);
                Log.i(TAG, "JsonArray getSaveFileName() "+filename);

                String requestUrl =
                        "http://192.168.35.3:8081/helpmemrbob/android/write.do";
                requestUrl +="?id="+"test7";
                requestUrl +="&title="+title_et.getText().toString();
                requestUrl +="&contents="+content_et.getText().toString();
                requestUrl +="&userfile1="+filename;

                Log.i(TAG, "requestUrl(): "+requestUrl);

            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return rtn;
        }
        public String getSaveFileName(String sFileName){
            Log.i(TAG, "getSaveFileName() "+sFileName);
            return sFileName;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            if (jsonArray != null) {
                //String result = jsonArray.toString();
                String result = saveFileName;
                Toast.makeText(mContext, "파일 업로드 성공!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(mContext, "파일 업로드 실패!", Toast.LENGTH_SHORT).show();
            }
        }
        public String jsonParser(){
            StringBuffer sb = new StringBuffer();
            try{
                sb.append(saveFileName);
            }catch (Exception e){
                e.printStackTrace();
            }
            return sb.toString();
        }
    }

    class AsyncHttpRequest4 extends AsyncTask<String, Void, String>{
        private Context mContext;
        private HashMap<String, String> param;//파라미터
        private HashMap<String, String> files;//사진파일

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            if(!dialog.isShowing())
                dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sBuffer = new StringBuffer();
            JSONArray rtn = null;
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

            }else{
                sb.append("로그인 실패 ㅜㅜ");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
}