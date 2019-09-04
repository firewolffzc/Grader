package com.example.grader;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class grades extends AppCompatActivity{
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);
        mAuth = FirebaseAuth.getInstance();
        String emailID = mAuth.getCurrentUser().getEmail().toString();
        String userID = returnUsername(emailID);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double avgscore = calculateAverage((Map<String, Object>) dataSnapshot.getValue());
                double standardscore = calculateStd((Map<String, Object>) dataSnapshot.getValue(), avgscore);
                TextView as = findViewById(R.id.averagescore);
                as.setText("The average score is " + avgscore);
                TextView stds = findViewById(R.id.stdscore);
                stds.setText("The standard deviation is "+ standardscore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public String returnUsername(String email){
        return email.substring(0, email.indexOf("@")).replaceAll("[. &#/*%$!)(^{}\\\\\\[\\]]","_");
    }

    public double calculateAverage(Map<String, Object> users) {
        Long c = new Long(0);
        Long s = new Long(0);
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            Map singleuser = (Map) entry.getValue();
            if ((Long) singleuser.get("score") != -1) {
                s = s + (Long) singleuser.get("score");
                c += 1;
            }
        }
        double ds = s;
        double dc = c;
        return ds/dc;
    }
    public double calculateStd(Map<String, Object> users, double average){
        Long c = new Long(0);
        Long s = new Long(0);
        Long diff;
        for(Map.Entry<String, Object> entry : users.entrySet()){
            Map singleuser = (Map) entry.getValue();
            if((Long) singleuser.get("score") != -1){
                diff = (Long) singleuser.get("score") - (long) average;
                s = s + diff * diff;
                c += 1;
            }

        }
        double ds = s;
        double dc = c;
        return Math.sqrt(ds/dc);
    }
}
