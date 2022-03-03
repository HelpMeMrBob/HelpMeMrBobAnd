package com.helpme.helpmemrboband;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailView extends AppCompatActivity {

    TextView detail;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

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
    }
}