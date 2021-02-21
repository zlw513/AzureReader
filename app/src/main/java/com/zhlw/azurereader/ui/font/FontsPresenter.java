package com.zhlw.azurereader.ui.font;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.enums.Font;
import com.zhlw.azurereader.presenter.BasePresenter;

import java.util.ArrayList;

public class FontsPresenter implements BasePresenter {

    private FontsActivity mFontsActivity;
    private ArrayList<Font> mFonts;
    private FontsAdapter mFontsAdapter;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    init();
                    break;
            }
        }
    };

    public FontsPresenter(FontsActivity fontsActivity) {

        mFontsActivity = fontsActivity;
    }


    @Override
    public void start() {
        mFontsActivity.getLlTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFontsActivity.finish();
            }
        });
        mFontsActivity.getTvTitleText().setText(mFontsActivity.getString(R.string.font));
        init();
    }

    private void init() {
        initFonts();
        mFontsAdapter = new FontsAdapter(mFontsActivity, R.layout.listview_font_item, mFonts);
        mFontsActivity.getLvFonts().setAdapter(mFontsAdapter);
        mFontsActivity.getPbLoading().setVisibility(View.GONE);
    }



    private void initFonts() {
        mFonts = new ArrayList<>();
        mFonts.add(Font.默认字体);
        mFonts.add(Font.方正楷体);
        mFonts.add(Font.经典宋体);
        mFonts.add(Font.方正行楷);
        mFonts.add(Font.迷你隶书);
        mFonts.add(Font.方正黄草);
        mFonts.add(Font.书体安景臣钢笔行书);
    }
}
