package com.zhlw.azurereader.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flod.loadingbutton.LoadingButton;
import com.zhlw.azurereader.R;
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
    EditText mEtUserName;
    @BindView(R.id.et_password)
    EditText mEtPassWord;
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

    public EditText getmEtUserName() {
        return mEtUserName;
    }

    public EditText getmEtPassWord() {
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
