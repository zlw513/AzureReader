package com.zhlw.azurereader.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flod.loadingbutton.LoadingButton;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.custom.ButtyEditTextView;
import com.zhlw.azurereader.viewmodel.MyViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btn_login_foruser)
    LoadingButton mLoadingButton;
    @BindView(R.id.tv_login_back)
    TextView mTvLoginBack;
    @BindView(R.id.user_regesiter)
    TextView mTvResgister;
    @BindView(R.id.et_user_name)
    ButtyEditTextView mEtUserName;
    @BindView(R.id.et_password)
    ButtyEditTextView mEtPassWord;
    @BindView(R.id.back_to_login)
    TextView mTvToLogin;
    @BindView(R.id.tv_userlogin_title)
    TextView mTvTitleLogin;

    Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bind = ButterKnife.bind(this);
        LoginActivityPresenter mLoginActivityPresenter = new LoginActivityPresenter(this);
        mLoginActivityPresenter.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (!inRangeOfView(mEtUserName,ev) || !inRangeOfView(mEtPassWord, ev)){
                if (mEtUserName.getEditText().isFocused()) {
                    mEtUserName.cancelFocus();
                    hideKeyboard();
                }
                if (mEtPassWord.getEditText().isFocused()){
                    mEtPassWord.cancelFocus();
                    hideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean inRangeOfView(View view, MotionEvent ev){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if(ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())){
            return false;
        }
        return true;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     */
    private void hideKeyboard() {
        Log.d("zlww", "hideKeyboard: ");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public LoadingButton getLoadingButton() {
        return mLoadingButton;
    }

    public TextView getmTvLoginBack() {
        return mTvLoginBack;
    }

    public TextView getmTvResgister() {
        return mTvResgister;
    }

    public ButtyEditTextView getmEtUserName() {
        return mEtUserName;
    }

    public ButtyEditTextView getmEtPassWord() {
        return mEtPassWord;
    }

    public TextView getmTvToLogin() {
        return mTvToLogin;
    }

    public TextView getmTvTitleLogin() {
        return mTvTitleLogin;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
