package com.tien.fly2top;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private DynamicListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
        ArrayList<String>mCheeseList = new ArrayList<String>();
        for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
            mCheeseList.add(Cheeses.sCheeseStrings[i]);
        }

        StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.text_view, mCheeseList);
        listView = (DynamicListView) findViewById(R.id.listview);

        //listView.setCheeseList(mCheeseList);
        listView.setAdapter(adapter);
        listView.setDatas(mCheeseList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Toast.makeText(getApplicationContext(), "position:"+arg2, 1000).show();
                listView.fly(arg2);
            }
        });
    }
    
}
