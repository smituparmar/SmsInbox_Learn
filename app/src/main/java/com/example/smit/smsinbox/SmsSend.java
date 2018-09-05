package com.example.smit.smsinbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SmsSend extends AppCompatActivity {

    Button sendSmsButton;
    ImageButton contactButton;
    EditText toPhoneNumber,messages;

    private static int PICK_CONTACT=1;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_send);

        sendSmsButton=(Button)findViewById(R.id.button3);
        toPhoneNumber=(EditText)findViewById(R.id.phone);
        messages=(EditText)findViewById(R.id.sms);
        contactButton=(ImageButton)findViewById(R.id.contact);

        sendSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toPhone=toPhoneNumber.getText().toString();
                String msg=messages.getText().toString();

                try{
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(toPhone,null,msg,null,null);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,PICK_CONTACT);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_CONTACT)
        {
            if(resultCode==AppCompatActivity.RESULT_OK)
            {

                Uri contactData=data.getData();
                Cursor c=getContentResolver().query(contactData,null,null,null,null);

                if(c.moveToFirst())
                {
                    String contactID=c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                            new String[]{contactID},
                            null);
                    String contactNumber=null;

                    if (cursorPhone.moveToFirst()) {
                        contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    cursorPhone.close();

                    toPhoneNumber.setText(contactNumber);
                }
            }
        }
    }
}
