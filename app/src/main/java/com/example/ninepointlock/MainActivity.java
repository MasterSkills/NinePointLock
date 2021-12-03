package com.example.ninepointlock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NinePointLock ninePointLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        this.ninePointLock = findViewById(R.id.ninePointLock);
        //
        this.ninePointLock.setOnLockResult(r -> {
            Toast.makeText(this, r, Toast.LENGTH_SHORT).show();
        });
    }
}
