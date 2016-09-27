package com.uncgcapstone.android.seniorcapstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public class TestActivity extends AppCompatActivity implements SwipeBackActivityBase {

    private SwipeBackActivityHelper mHelper;
    String url, recipename, servings, preptime, cooktime = "";
    ImageView detailImage;
    TextView detailRecipeNameText, servesTextDetail, prepTextDetail, cookTextDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            url = bundle.getString("url");
            recipename = bundle.getString("recipename");
            servings = bundle.getString("servings");
            preptime = bundle.getString("preptime");
            cooktime = bundle.getString("cooktime");
        }

        detailImage = (ImageView) findViewById(R.id.detailImage);
        Glide.with(this).load(url).centerCrop().into(detailImage);

        detailRecipeNameText = (TextView) findViewById(R.id.detailRecipeNameText);
        detailRecipeNameText.setText(recipename);

        servesTextDetail = (TextView) findViewById(R.id.servesTextDetail);
        servesTextDetail.setText("Serves " + servings);

        prepTextDetail = (TextView) findViewById(R.id.prepTextDetail);
        prepTextDetail.setText("Prep: " + preptime + "m");

        cookTextDetail = (TextView) findViewById(R.id.cookTextDetail);
        cookTextDetail.setText("Cook: " + cooktime + "m");





        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity(){
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("OnStopCalled", "TestActivity");
    }



}
