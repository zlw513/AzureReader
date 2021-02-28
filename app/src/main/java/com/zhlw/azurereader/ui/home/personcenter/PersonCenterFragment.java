package com.zhlw.azurereader.ui.home.personcenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.custom.MineRowView;
import com.zhlw.azurereader.custom.ShapeTextView;
import com.zhlw.azurereader.ui.home.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonCenterFragment extends Fragment {

    @BindView(R.id.tv_login_flag)
    TextView mTvLoginName;//未登录或用户名
    @BindView(R.id.tv_action_login)
    ShapeTextView mShapeTextView;//登录按钮
    @BindView(R.id.sw_night_mode)
    Switch mSwitch;//夜间 日间切换开关
    @BindView(R.id.mine_app_setting)
    MineRowView mSkinChange;
    private Unbinder unbinder;
    private Context mContext;
    private PersonCenterPresenter mPersonCenterPresenter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public PersonCenterFragment() {
        // Required empty public constructor
    }

    public static PersonCenterFragment newInstacne(){
        PersonCenterFragment fragment = new PersonCenterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_center, container, false);;
        unbinder = ButterKnife.bind(this,view);
        mPersonCenterPresenter = new PersonCenterPresenter(this);
        mPersonCenterPresenter.start();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO  do something  正常情况下是对控件进行绑定的
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("debuging", "onActivityResult: 调用on activity result");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            mTvLoginName.setText(data.getStringExtra("用户名"));
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getViewModel().setLoginName(data.getStringExtra("用户名"));
        } else {
            //登录失败，故不进行处理
        }
    }

    public TextView getmTvLoginName(){
        return mTvLoginName;
    }

    public ShapeTextView getmShapeTextView(){
        return mShapeTextView;
    }

    public Switch getmSwitch(){
        return mSwitch;
    }

    public MineRowView getmSkinChange() {
        return mSkinChange;
    }
}
