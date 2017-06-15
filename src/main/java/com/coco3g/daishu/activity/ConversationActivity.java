package com.coco3g.daishu.activity;

import android.os.Bundle;

import com.coco3g.daishu.R;
import com.coco3g.daishu.view.TopBarView;


/**
 * Created by coco3g on 17/3/30.
 */

public class ConversationActivity extends BaseFragmentActivity {
    private TopBarView mTopBar;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        mTopBar = (TopBarView) findViewById(R.id.topbar_conversation);
        title = getIntent().getData().getQueryParameter("title");
        mTopBar.setTitle(title);

    }


}
