package com.example.daynotes.OnBoarding;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;


import com.example.daynotes.R;
import com.example.daynotes.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {
    private OnBoardingAdapter onBoardingAdapter;
    private LinearLayout layoutBoardingIndicators;
    private Button btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().hide();
        }

        btn=(Button) findViewById(R.id.button);
        setupOnboardingItems();
        layoutBoardingIndicators=findViewById(R.id.layoutOnBoardingIndicators);
        ViewPager2 onBoardingViewPager= findViewById(R.id.onboardingviewpager);
        onBoardingViewPager.setAdapter(onBoardingAdapter);
        setupOnBoardingIndicator();
        setCurrentOnBoardingIndicator(0);
        onBoardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicator(position);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onBoardingViewPager.getCurrentItem()+1< onBoardingAdapter.getItemCount())
                {
                    onBoardingViewPager.setCurrentItem(onBoardingViewPager.getCurrentItem()+1);
                }
                else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }
    private void setupOnboardingItems()
    {
        List<OnBoardingItem> onBoardingItems= new ArrayList<>();
        OnBoardingItem itemPlay= new OnBoardingItem();
        itemPlay.setTitle("Your are strong,don't be afraid");
        itemPlay.setDescription("From inside should be strong and confident out there");
        itemPlay.setImage(R.drawable.s1);

        OnBoardingItem item= new OnBoardingItem();
        item.setTitle("Don't think too much");
        item.setDescription("Let's go world where women can walk with confidence, not caution.");
        item.setImage(R.drawable.s3);

        onBoardingItems.add(itemPlay);
        onBoardingItems.add(item);

        onBoardingAdapter= new OnBoardingAdapter(onBoardingItems);
    }
    private void setupOnBoardingIndicator(){
        ImageView[] indicators= new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i= 0;i<indicators.length;i++)
        {
            indicators[i]= new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutBoardingIndicators.addView(indicators[i]);
        }
    }
    private void setCurrentOnBoardingIndicator(int index)
    {
        int childCount= layoutBoardingIndicators.getChildCount();
        for(int i=0 ;i<childCount;i++)
        {
            ImageView imageView= (ImageView)layoutBoardingIndicators.getChildAt(i);
            if(i==index)
            {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_active));
            }
            else{
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive));
            }
        }
        if(index==onBoardingAdapter.getItemCount()-1)
        {
            btn.setText("Start");
        }
        else{
            btn.setText("Next");
        }
    }


}