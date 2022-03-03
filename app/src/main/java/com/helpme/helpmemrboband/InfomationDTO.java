package com.helpme.helpmemrboband;

public class InfomationDTO {
    private String idx;		//식당 인덱스
    private String food;	//음식 키워드
    private String place;	//식당명
    private String address; //식당 주소
    private String lati;	//위도
    private String longi;	//경도
    private String plcNum;  //식당 전화번호
    private String menu;	//식당 메뉴
    private String price;	//메뉴 가격
    private String operTime;//운영 시간

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getPlcNum() {
        return plcNum;
    }

    public void setPlcNum(String plcNum) {
        this.plcNum = plcNum;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOperTime() {
        return operTime;
    }

    public void setOperTime(String operTime) {
        this.operTime = operTime;
    }
}

