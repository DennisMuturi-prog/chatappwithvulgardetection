package com.example.mychatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class specificchat extends AppCompatActivity {
    /*Handler mHandler;*/
    EditText mgetmessage;
    ImageButton msendmessagebutton;
    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mimageviewofspecificuser;
    TextView mnameofspecificuser;
    private String enteredmessage;
    Intent intent;
    String mreceivername,sendername,mreceiveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    String senderroom,receiverroom;
    ImageButton mbackbuttonofspecificchat;
    RecyclerView mmessagerecyclerview;
    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    MessageAdapter messageAdapter;
    ArrayList<Messages> messagesArrayList;
    private List<String> badWords;

    public specificchat() {
        // Initialize the list of bad words
        badWords = new ArrayList<>();
        badWords.add("fuck");
        badWords.add("bitch");
        badWords.add("nigga");
        badWords.add("fucked");
        badWords.add("shit");
        badWords.add("mafi");


        // Add more bad words as needed
    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);



        mgetmessage=findViewById(R.id.getmessage);
        msendmessagecardview=findViewById(R.id.cardviewofsendmessage);
        msendmessagebutton=findViewById(R.id.imageviewsendmessage);
        mtoolbarofspecificchat=findViewById(R.id.toolbarofspecificchat);
        mnameofspecificuser=findViewById(R.id.Nameofspecificuser);
        mimageviewofspecificuser=findViewById(R.id.specificuserimageinimageview);
        mbackbuttonofspecificchat=findViewById(R.id.backbuttonofspecificchat);
        messagesArrayList=new ArrayList<>();
        mmessagerecyclerview=findViewById(R.id.recyclerviewofspecific);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messageAdapter=new MessageAdapter(specificchat.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messageAdapter);




        intent=getIntent();
        setSupportActionBar(mtoolbarofspecificchat);
        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "toolbar is clicked", Toast.LENGTH_SHORT).show();
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");


        msenderuid=firebaseAuth.getUid();
        mreceiveruid=getIntent().getStringExtra("receiveruid");
        mreceivername=getIntent().getStringExtra("name");
        senderroom=msenderuid+mreceiveruid;
        receiverroom=mreceiveruid+msenderuid;
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messageAdapter=new MessageAdapter(specificchat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Messages messages=snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mbackbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(specificchat.this,chatActivity.class);
                startActivity(intent);
                finish();

            }
        });
        mnameofspecificuser.setText(mreceivername);
        String uri=intent.getStringExtra("imageuri");
        if(uri.isEmpty()){
            Toast.makeText(getApplicationContext(), "null is received", Toast.LENGTH_SHORT).show();
        }
        else {
            Picasso.get().load(uri).into(mimageviewofspecificuser);
        }
        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage=mgetmessage.getText().toString();
                specificchat myObj = new specificchat();
                String vulgarword=myObj.hasProfanity(enteredmessage);
                if (enteredmessage.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter message first", Toast.LENGTH_SHORT).show();
                }
                else if(vulgarword!=null){
                    Toast.makeText(getApplicationContext(), "your message contains vulgar language specifically the word "+vulgarword, Toast.LENGTH_LONG).show();

                }
                else{
                    Date date=new Date();
                    currenttime=simpleDateFormat.format(calendar.getTime());
                    Messages messages=new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.getReference()
                                            .child("chats")
                                            .child(receiverroom)
                                            .child("messages")
                                            .push()
                                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });
                    mgetmessage.setText(null);
                    mmessagerecyclerview.setAdapter(messageAdapter);

                }

            }
        });



       /* this.mHandler = new Handler();
        m_Runnable.run();*/


    }
    @Override
    public void onStart() {
        super.onStart();
        mmessagerecyclerview.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

    }
    @Override
    public void onStop() {
        super.onStop();
        if(messageAdapter!=null)
        {
            messageAdapter.notifyDataSetChanged();
        }
    }
   /* private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {

            mmessagerecyclerview.setAdapter(messageAdapter);
            specificchat.this.mHandler.postDelayed(m_Runnable,5000);
        }

    };*/
    public String hasProfanity(String message) {

        // Split the message into words
        String[] words = message.toLowerCase().split("\\s+");
        // Check if any of the words are bad words
        for (String word : words) {
            if (badWords.contains(word)) {
                return word; // The message has profanity
            }
        }
        return null; // The message does not have profanity

    }




}