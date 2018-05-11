package com.example.zombiessy.zombiessyhw9;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class ResultsActivity extends AppCompatActivity {
    private Fragment ResFragment;
    private Toolbar resultToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(resultToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        resultToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ResFragment = new ResultsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.searchResults, ResFragment).commit();
    }
}
