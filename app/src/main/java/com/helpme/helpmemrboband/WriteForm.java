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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        if (!id.equals("empty")) {
            Toast.makeText(this, "글쓰기 로그인정보있음."+id,Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "로그인 정보 없음.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


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
                    //menu_page5.setVisibility(View.INVISIBLE);   //  마이 페이지
                    menu_page6.setVisibility(View.INVISIBLE);   //  글쓰기 페이지
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
                if (id.equals("empty")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyPageWebView.class);
                    startActivity(intent);
                }
            }
        });

        button_page6 = findViewById(R.id.write_form);
        button_page6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (id.equals("empty")) {
//                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    Intent intent = new Intent(getApplicationContext(), WriteForm.class);
//                    startActivity(intent);
//                }
            }
        });

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
                        = "http://" + getString(R.string.server_addr)
                        + ":8081/helpmemrbob/fileUpload/uploadAndroid.do";
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
                requestUrl +="?id="+id;
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

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
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



    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            if (isPage) {
                menu_page1.setVisibility(View.INVISIBLE);
                //menu_page2.setVisibility(View.VISIBLE); //  룰렛 페이지
                //menu_page3.setVisibility(View.VISIBLE); //  지도 페이지
                //menu_page4.setVisibility(View.VISIBLE); //  리뷰 페이지
                //menu_page5.setVisibility(View.VISIBLE); //  마이 페이지
                menu_page6.setVisibility(View.VISIBLE); //  글쓰기 페이지
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