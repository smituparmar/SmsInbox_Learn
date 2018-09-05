package com.example.smit.smsinbox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SmsReceive extends Activity implements AdapterView.OnItemClickListener{

    private static SmsReceive inst;
    ArrayList<String> smsMessageList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;

    public static SmsReceive instance()
    {
        return inst;
    }

    protected void onStart()
    {
        super.onStart();
        inst=this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_receive);

        smsListView=(ListView)findViewById(R.id.SMSList);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, smsMessageList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        refreshInbox();
    }

    public void refreshInbox()
    {
        ContentResolver contentResolver=getContentResolver();
        Cursor smsCursor=contentResolver.query(Uri.parse("content://sms/inbox"),null,null,null,null);
        int indexBody=smsCursor.getColumnIndex("body");
        int indexAddress=smsCursor.getColumnIndex("address");
        int timeMillis=smsCursor.getColumnIndex("date");
        Date date=new Date(timeMillis);
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yy");
        String dateText=format.format(date);

        if(indexBody<0 || !smsCursor.moveToFirst())return;

        arrayAdapter.clear();

        do {
            String str=smsCursor.getString(indexAddress)+ " at " + "\n" + smsCursor.getString(indexBody)+ dateText + "\n";
            arrayAdapter.add(str);
        }
        while(smsCursor.moveToNext());
    }

    public void updateList(final String smsMessage)
    {
        arrayAdapter.insert(smsMessage,0);
        arrayAdapter.notifyDataSetChanged();

    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try
        {
            String[] smsMessages=smsMessageList.get(i).split("\n");
            String address=smsMessages[0];
            String smsMessage="";

            for(int j=1;j<smsMessages.length;j++)
            {
                smsMessage+=smsMessages[i];
            }

            String smsMessageStr=address+"\n";
            smsMessageStr+=smsMessage;
            Toast.makeText(this,smsMessageStr,Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void goToCompose(View view)
    {
        Intent intent=new Intent(this,SmsSend.class);
        startActivity(intent);
    }
}
