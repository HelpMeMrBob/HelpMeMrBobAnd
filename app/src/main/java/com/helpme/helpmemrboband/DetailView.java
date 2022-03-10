package com.helpme.helpmemrboband;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {

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

    TextView detail;
    Button btn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        sharedPreferences = getSharedPreferences("SESSION_INFO", MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "empty");

        Intent intent = getIntent();
        String place = intent.getStringExtra("place");
        String address = intent.getStringExtra("address");
        String menu = intent.getStringExtra("menu");
        String plcNum = intent.getStringExtra("plcNum");
        String price = intent.getStringExtra("price");
        String operTime = intent.getStringExtra("operTime");

        String menuPrice = "정보제공없음";

        if (!menu.equals("null") && price.equals("null")) {
            String[] menus = menu.split("@");
            menuPrice = null;
            price = " : 0원\n";

            for (int i = 0; i < menus.length; i++) {
                if (menuPrice == null) {
                    menuPrice = menus[0] + price;
                }
                else {
                    menuPrice += (menus[i] + price);
                }
            }

            System.out.println("1 : " + menuPrice);
        }
        else if (!menu.equals("null") && !price.equals("null")) {
            String[] menus = menu.split("@");
            String[] prices = price.split("@");
            menuPrice = null;

            for (int i = 0; i < menus.length; i++) {
                if (menuPrice == null) {
                    menuPrice = menus[0] + " : " + prices[0] + "원\n";
                }
                else {
                    menuPrice += (menus[i] + " : " + prices[i] + "원\n");
                }
            }

            System.out.println("2 : " + menuPrice);
        }

        if (plcNum.equals("null")) {
            plcNum = "정보제공없음";
        }
        if (operTime.equals("null")) {
            operTime = "정보제공없음";
        }

        detail = findViewById(R.id.detailView);
        detail.setMovementMethod(new ScrollingMovementMethod());
        detail.setText(
                "식당 이름" + "\n" + place + "\n\n" +
                "식당 주소" + "\n" + address + "\n\n" +
                "--- 메뉴 ---" + "\n" + menuPrice + "\n\n" +
                "식당 번호" + "\n" + plcNum + "\n\n" +
                "영업 시간" + "\n" + operTime + "\n\n"
        );

        btn = findViewById(R.id.finish_detail);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        /****************************************************/
        menu_page1 = findViewById(R.id.menu_page1);
        menu_page2 = findViewById(R.id.menu_page2);
        menu_page3 = findViewById(R.id.menu_page3); //  지도 페이지
        menu_page4 = findViewById(R.id.menu_page4); //  리뷰 페이지
        menu_page5 = findViewById(R.id.menu_page5); //  마이 페이지
        menu_page6 = findViewById(R.id.menu_page6); //  마이 페이지

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
                    menu_page3.setVisibility(View.INVISIBLE);   //  지도 페이지
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //  지도 페이지 이동
        button_page3 = findViewById(R.id.mapPage);
        button_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), Information.class);
                //startActivity(intent);
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




    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation) {
            if (isPage) {
                menu_page1.setVisibility(View.INVISIBLE);
                //menu_page2.setVisibility(View.VISIBLE);
                menu_page3.setVisibility(View.VISIBLE); //  리뷰 페이지
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