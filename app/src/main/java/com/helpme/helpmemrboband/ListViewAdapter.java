package com.helpme.helpmemrboband;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView contentTextView;
    private  TextView idTextView;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    //ListViewAdapter의 생성자
    public ListViewAdapter(){

    }

    //Adapter에 사용되는 데이터의 갯수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        //listView_item Layout을 inflate하여 convertView 참조획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           // convertView = inflater.inflate(R.layout.listview_item,parent,false);
        }
        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        titleTextView = (TextView) convertView.findViewById(R.id.title);
        imageView = (ImageView) convertView.findViewById(R.id.image);
        contentTextView = (TextView) convertView.findViewById(R.id.content);
       // idTextView=(TextView)convertView.findViewById(R.id.id);

        ListViewItem listViewItem = listViewItemList.get(position);

        //아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.getTitle());
        imageView.setImageResource(listViewItem.getImage());
        contentTextView.setText(listViewItem.getContent());
        idTextView.setText(listViewItem.getId());

        return convertView;
    }
    //지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    //아이템 데이터 추가를 위한 함수.
    public void addItem(String id,String title, String content){
        ListViewItem item = new ListViewItem();
        item.setTitle(title);
        item.setContent(content);
        item.setId(id);

        listViewItemList.add(item);
    }
    //아이템 데이터 추가를 위한 함수.
    public void addItem(String id,String title, int image, String content){
        ListViewItem item = new ListViewItem();
        item.setTitle(title);
        item.setImage(image);
        item.setContent(content);
        item.setId(id);

        listViewItemList.add(item);
    }

    public class ListViewItem{
        private int imageDrawable;
        private String contentStr;
        private String titleStr;
        private String idStr;

        public void setId(String id){ idStr = id;}
        public void setTitle(String title){
            titleStr = title;
        }
        public void setImage(int image){
            imageDrawable = image;
        }
        public void setContent(String content){
            contentStr = content;
        }

        public String getId(){return this.idStr;}
        public int getImage(){
            return this.imageDrawable;
        }
        public String getContent(){
            return this.contentStr;
        }
        public String getTitle(){
            return this.titleStr;
        }
    }






}
