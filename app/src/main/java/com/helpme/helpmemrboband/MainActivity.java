package com.helpme.helpmemrboband;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String TAG = "Roulette";

    private CircleManager circleManager;
    private RelativeLayout layoutRoulette;

    private Button btnDrawRoulette15;
    private Spinner btnDrawRouletteFav;
    private Button btnRotate;

    private String[] items = {"선호목록 추가","탭1","탭2","탭3",
            "탭4","탭5"};
    private String spinnerSelected = "선호목록 추가";

    private Animation translateLeftAnim;
    private Animation translateRightAnim;

    private ArrayList<String> STRINGS;
    private float initAngle = 0.0f;
    private int num_roulette = 15;

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

    // 로그인 정보 가져오기
    String inId;

    ProgressDialog dialog;
    // ***** 포트번호 변경할것
    String requestFoodUrl = "http://192.168.219.105:8081/helpmemrbob/allFood.do";
    ArrayList<String> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRotate = findViewById(R.id.btnRotate);
        btnDrawRoulette15 = findViewById(R.id.btnDrawRoulette15);
        btnDrawRouletteFav = findViewById(R.id.btnDrawRouletteFav);
        layoutRoulette = findViewById(R.id.layoutRoulette);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        R.layout.spinner_item,
                        items);
        btnDrawRouletteFav.setAdapter(adapter);
        btnDrawRouletteFav.setSelection(0);

        // 음식목록 가져오기
        foodList.add("된장찌개");
        foodList.add("막국수");
        foodList.add("유린기");
        foodList.add("회덮밥");
        foodList.add("울면");
        foodList.add("장어덮밥");
        foodList.add("전복죽");
        foodList.add("물냉면");
        foodList.add("모듬회");
        foodList.add("칼국수");
        foodList.add("리조또");
        foodList.add("김밥");
        foodList.add("삼계탕");
        foodList.add("찜닭");
        foodList.add("차돌박이");
        foodList.add("토스트");
        foodList.add("곰탕");
        foodList.add("짜장면");
        foodList.add("냉모밀");
        foodList.add("떡갈비");
        foodList.add("똠양꿍");
        foodList.add("오겹살");
        foodList.add("갈비탕");
        foodList.add("닭강정");
        foodList.add("제육볶음");
        foodList.add("라조기");
        foodList.add("카레");
        foodList.add("팟타이");
        foodList.add("피자");
        foodList.add("훠궈");
        foodList.add("훈제오리");
        foodList.add("설렁탕");
        foodList.add("샌드위치");
        foodList.add("알밥");
        foodList.add("매운탕");
        foodList.add("치킨");
        foodList.add("떡국");
        foodList.add("양갈비");
        foodList.add("떡볶이");
        foodList.add("텐동");
        foodList.add("비빔국수");
        foodList.add("메밀국수");
        foodList.add("양념게장");
        foodList.add("파스타");
        foodList.add("족발");
        foodList.add("버섯전골");
        foodList.add("마라탕");
        foodList.add("만두");
        foodList.add("우동");

        sharedPreferences = getSharedPreferences("SESSION_INFO", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "empty");

        // 완전 랜덤 룰렛 버튼 클릭 (로그인X)
        btnDrawRoulette15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                STRINGS = setRandom(num_roulette);
                circleManager = new CircleManager(MainActivity.this, num_roulette);
                layoutRoulette.addView(circleManager);
            }
        });

        // 나만의 선호 리스트 가져와서 룰렛에 넣기 (로그인 시에만 가능)
        btnDrawRouletteFav.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (inId == null || inId == "") {
                        Toast.makeText(MainActivity.this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
        btnDrawRouletteFav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int i, long l) {
                //선택한 항목의 인덱스 i가 전달되어 해당 항목을 알수있다.
                if(i==1) {
                    STRINGS = setRandomFav(num_roulette);
                    circleManager = new CircleManager(MainActivity.this, num_roulette);
                    layoutRoulette.addView(circleManager);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 룰렛 돌리기(SPIN) 버튼 클릭
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateLayout(layoutRoulette, num_roulette);
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
                    menu_page2.setVisibility(View.INVISIBLE);   //  룰렛 페이지
                    //menu_page3.setVisibility(View.INVISIBLE);   //  지도 페이지
                    //menu_page4.setVisibility(View.INVISIBLE);   //  리뷰 페이지
                    //menu_page5.setVisibility(View.INVISIBLE);   //  마이 페이지
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
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(intent);
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
                    Log.i("check", id + " : if 들어옴");
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MyPageWebView.class);
                    startActivity(intent);
                    Log.i("check", id + " : else 들어옴");
                }
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
    }//// end of onCreate()

    // 룰렛을 돌릴 각도를 받아와 돌리는 함수
    public void rotateLayout(final RelativeLayout layout, final int num) {
        final float fromAngle = getRandom(360) + 3600 + initAngle;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getResult(fromAngle, num); // start when animation complete
            }
        }, 3000);

        RotateAnimation rotateAnimation = new RotateAnimation(initAngle, fromAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.accelerate_decelerate_interpolator));
        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        layout.startAnimation(rotateAnimation);
    }

    // 룰렛에 올릴 이름 랜덤으로 뽑아내서 채우기
    public ArrayList<String> setRandom(int num) {
        ArrayList<String> strings = new ArrayList<>();

        int a[] = new int [num];
        Random random = new Random();

        for(int i=0; i<num; i++) {
            a[i] = random.nextInt(49);
            for(int j=0; j<i; j++) {
                if(a[i]==a[j]) {
                    i--;
                }
            }

        }
        for(int i=0; i<num; i++) {
            strings.add(foodList.get(a[i]));
        }
        return strings;
    }

    // 나만의 선호 리스트에 연결해서 가져오고, 파싱해서, 남는 룰렛 공간에 음식리스트에서 랜덤으로 뽑아 채우기
    public ArrayList<String> setRandomFav(int num) {
        ArrayList<String> strings = new ArrayList<>();

        strings.add("라면");
        strings.add("김치볶음밥");
        strings.add("김치찌개");
        strings.add("삼겹살");
        strings.add("초밥");
        strings.add("떡튀순");

        int a[] = new int [num];
        Random random = new Random();

        for(int i=0; i<num-6; i++) {
            a[i] = random.nextInt(49);
            for(int j=0; j<i; j++) {
                if(a[i]==a[j]) {
                    i--;
                }
            }
        }
        for(int i=0; i<num; i++) {
            strings.add(foodList.get(a[i]));
        }

        return strings;
    }

    // 룰렛을 돌릴 각도를 난수로 뽑아냄
    private int getRandom(int maxNumber) {
        double r = Math.random();
        return (int)(r * maxNumber);
    }

    // 룰렛의 결과값을 구해옴(각도로 계산)
    private void getResult(float angle, int num_roulette) {
        String text = "";
        angle = angle % 360;

        Log.d("Roulette", "getResult : " + angle);

        if (angle <= 270 && angle > 246) {
            text = STRINGS.get(0);
            buildAlert(text);
        } else if (angle <=246 && angle > 222) {
            text = STRINGS.get(1);
            buildAlert(text);
        } else if (angle <= 222 && angle > 198) {
            text = STRINGS.get(2);
            buildAlert(text);
        } else if (angle <= 198 && angle > 174) {
            text = STRINGS.get(3);
            buildAlert(text);
        } else if (angle <= 174 && angle > 150) {
            text = STRINGS.get(4);
            buildAlert(text);
        } else if (angle <= 150 && angle > 126) {
            text = STRINGS.get(5);
            buildAlert(text);
        } else if (angle <= 126 && angle > 102) {
            text = STRINGS.get(6);
            buildAlert(text);
        } else if (angle <= 102 && angle > 78) {
            text = STRINGS.get(7);
            buildAlert(text);
        } else if (angle <= 78 && angle > 54) {
            text = STRINGS.get(8);
            buildAlert(text);
        } else if (angle <= 54 && angle > 30) {
            text = STRINGS.get(9);
            buildAlert(text);
        } else if (angle <= 30 && angle > 6) {
            text = STRINGS.get(10);
            buildAlert(text);
        } else if (angle <= 6 || angle > 342) {
            text = STRINGS.get(11);
            buildAlert(text);
        } else if (angle <= 342 && angle > 318) {
            text = STRINGS.get(12);
            buildAlert(text);
        } else if (angle <= 318 && angle > 294) {
            text = STRINGS.get(13);
            buildAlert(text);
        } else if (angle <= 294 && angle > 270) {
            text = STRINGS.get(14);
            buildAlert(text);
        }
    }

    // 룰렛 결과를 새 창으로 띄워서 보여주고, 주변 식당 정보로 이동
    private void buildAlert(String text) {

        // ##### 일정시간 후(발표용으로 5초후?) 리뷰를 작성해달라고 메세지 보내기 (파이어베이스)

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(text+" 를 뽑으셨습니다!")
                .setMessage("근처 식당을 보러 가시겠습니까?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        layoutRoulette.setRotation(360 - initAngle);

                        Intent intent = new Intent(getApplicationContext(), Information.class);
                        intent.putExtra("menu", text);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 룰렛판을 그리는 클래스
    public class CircleManager extends View {
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int num;

        public CircleManager(Context context, int num) {
            super(context);
            this.num = num;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = layoutRoulette.getWidth();
            int height = layoutRoulette.getHeight();
            int sweepAngle = 360 / num;

            RectF rectF = new RectF(0, 0, width, height);
            Rect rect = new Rect(0, 0, width, height);

            int centerX = (rect.left + rect.right) / 2;
            int centerY = (rect.top + rect.bottom) / 2;
            int radius = (rect.right - rect.left) / 2;

            int temp = 0;

            for (int i = 0; i < num; i++) {
                if(i%3==0) {paint.setARGB(255,237,106,90);}
                if(i%3==1) {paint.setARGB(255,81,163,163);}
                if(i%3==2) {paint.setARGB(255,45,57,75);}
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setAntiAlias(true);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawArc(rectF, temp, sweepAngle, true, paint);

                float medianAngle = (temp + (sweepAngle / 2f)) * (float) Math.PI / 180f;

                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setColor(Color.WHITE);
                paint.setTextSize(32);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);

                float arcCenterX = (float) (centerX + (radius * Math.cos(medianAngle))); // Arc's center X
                float arcCenterY = (float) (centerY + (radius * Math.sin(medianAngle))); // Arc's center Y

                // put text at middle of Arc's center point and Circle's center point
                float textX = (centerX + arcCenterX) / 2;
                float textY = (centerY + arcCenterY) / 2;

                canvas.drawText(STRINGS.get(i), textX, textY, paint);
                temp += sweepAngle;
            }
        }
    }


    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            if (isPage) {
                menu_page1.setVisibility(View.INVISIBLE);
                menu_page2.setVisibility(View.VISIBLE); //  룰렛 페이지
                //menu_page3.setVisibility(View.VISIBLE); //  리뷰 페이지
                //menu_page4.setVisibility(View.VISIBLE); //  지도 페이지
                //menu_page5.setVisibility(View.VISIBLE); //  마이 페이지
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