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
        mAuth = FirebaseAuth.getInstance();
        String emailID = mAuth.getCurrentUser().getEmail().toString();
        String userID = returnUsername(emailID);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double avgscore = calculateAverage((Map<String, Object>) dataSnapshot.getValue());
                double standardscore = calculateStd((Map<String, Object>) dataSnapshot.getValue(), avgscore);
                TextView as = (TextView) findViewById(R.id.averagescore);
                as.setText("The average score is " + avgscore);
                TextView stds = (TextView) findViewById(R.id.stdscore);
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
        double c = 0.00;
        double s = 0.00;
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            Map singleuser = (Map) entry.getValue();
            if ((Long) singleuser.get("score") != -1) {
                Object o = singleuser.get("score");
                s = s + (double) o;
                c += 1;
            }

        }
        return s / c;
    }
    public double calculateStd(Map<String, Object> users, double average){
        double c = 0.00;
        double s = 0.00;
        double diff = 0.00;
        for(Map.Entry<String, Object> entry : users.entrySet()){
            Map singleuser = (Map) entry.getValue();
            if((Long) singleuser.get("score") != -1){
                Object o = singleuser.get("score");
                diff = (double) o  - average;
                s = s + diff * diff;
                c += 1;
            }

        }
        return Math.sqrt(s / c);
    }
}
