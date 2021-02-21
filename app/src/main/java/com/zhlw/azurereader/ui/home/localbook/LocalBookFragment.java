package com.zhlw.azurereader.ui.home.localbook;


import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.custom.DragSortGridView;
import com.zhlw.azurereader.ui.scanbook.ScanBookActivity;
import com.zhlw.azurereader.ui.search.SearchBookActivity;
import com.zhlw.azurereader.utils.DisplayUtils;
import com.zhlw.azurereader.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalBookFragment extends Fragment {

    private boolean mIsOpen = false;
    private ImageView mImageView;
    private TextView mTextViewYes;
    private TextView mTextViewNo;
    private RelativeLayout mRelativeLayout;
    private Context mContext;
    private Onclick onclick;
    private RecyclerView mRecyclerView;

    @BindView(R.id.gv_book)
    DragSortGridView dragSortGridView;
    @BindView(R.id.ll_no_data_tips)
    LinearLayout mLinearLayout;
    @BindView(R.id.imageview_nodata_tips)
    ImageView imageView;

    private Unbinder unbinder;

    private LocalBookPresenter mLocalBookPresenter;

    public LocalBookFragment() {
        // Required empty public constructor
    }

    public static LocalBookFragment newInstance() {
        LocalBookFragment fragment = new LocalBookFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_local_book, container, false);
        unbinder = ButterKnife.bind(this, v);
        mLocalBookPresenter = new LocalBookPresenter(this);
        mLocalBookPresenter.start();//初始化LocalBook相关操作
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = view.findViewById(R.id.iv_localbook_function);
        mTextViewYes = view.findViewById(R.id.tv_bubble_yes);
        mTextViewNo = view.findViewById(R.id.tv_bubble_no);
        mRelativeLayout = view.findViewById(R.id.rl_bubble);
        mRecyclerView = view.findViewById(R.id.rv_local_book);
        if (onclick == null) onclick = new Onclick();
        searchBook(mImageView,mTextViewYes,mTextViewNo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class Onclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_localbook_function:
                    if (!mIsOpen){
                        open();
                    } else {
                        close();
                    }
                    break;
                case R.id.tv_bubble_yes:
                    //TODO 跳转到查找本地书籍那里  进行权限检查
                    SoulPermission.getInstance().checkAndRequestPermissions(Permissions.build(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),new CheckRequestPermissionsListener(){
                        @Override
                        public void onAllPermissionOk(Permission[] allPermissions) {
                            Intent intent = new Intent(mContext, ScanBookActivity.class);
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onPermissionDenied(Permission[] refusedPermissions) {
                            ToastUtils.showToast("权限没开");
                        }
                    });
                    break;
                case R.id.tv_bubble_no:
                    close();
                    break;
            }
        }
    }

    private void open() {
        mIsOpen = true;
        startAnim(mRelativeLayout,95);//radis单位为dp
        mImageView.setImageResource(R.mipmap.iv_function_select);
    }

    private void close() {
        mIsOpen = false;
        closeAnim(mRelativeLayout, 95);
        mImageView.setImageResource(R.mipmap.iv__function_normal);
    }

    private void searchBook(ImageView imageView,TextView tv1,TextView tv2) {
        imageView.setOnClickListener(onclick);
        tv1.setOnClickListener(onclick);
        tv2.setOnClickListener(onclick);
    }

    private void startAnim(View v,int radis) {
        if (v.getVisibility() != View.VISIBLE){
            v.setVisibility(View.VISIBLE);
        }
        double degree = Math.toRadians(0);
        int translateX = -(int)(radis * Math.sin(degree));
        int translateY = -(int)(radis * Math.cos(degree));
        translateX = DisplayUtils.dp2px(mContext, translateX);//转换一下，解决屏幕适配问题
        translateY = DisplayUtils.dp2px(mContext, translateY);

        AnimatorSet as = new AnimatorSet();
        as.playTogether(
                ObjectAnimator.ofFloat(v,"translationX",0,translateX),
                ObjectAnimator.ofFloat(v,"translationY",0,translateY),
                ObjectAnimator.ofFloat(v,"ScaleX",0f,1f),
                ObjectAnimator.ofFloat(v,"ScaleY",0f,1f),
                ObjectAnimator.ofFloat(v,"alpha",0f,1f)
        );
        as.setDuration(400).start();
    }

    private void closeAnim(View v,int radis) {
        if (v.getVisibility() != View.VISIBLE){
            v.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * 0;
        int translateX = -(int)(radis * Math.sin(degree));
        int translateY = -(int)(radis * Math.cos(degree));
        translateX = DisplayUtils.dp2px(mContext, translateX);
        translateY = DisplayUtils.dp2px(mContext, translateY);

        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(v, "translationX", translateX, 0),
                ObjectAnimator.ofFloat(v, "translationY", translateY, 0),
                ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.1f),
                ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.1f),
                ObjectAnimator.ofFloat(v, "alpha", 1f, 0.0f));
        set.setDuration(400).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocalBookPresenter.getData();
    }

    @Override
    public void onPause() {
        if (mIsOpen) closeOnPause();//当misopen已经为false时就不会再进来了
        super.onPause();
    }

    private void closeOnPause() {
        mIsOpen = false;
        mRelativeLayout.setVisibility(View.GONE);
        mRelativeLayout.setTranslationX(0);
        mRelativeLayout.setTranslationY(0);
        mRelativeLayout.setScaleX(0.1f);
        mRelativeLayout.setScaleY(0.1f);
        mRelativeLayout.setAlpha(0.0f);
    }

    public LinearLayout getmLinearLayout(){
        return mLinearLayout;
    }

    public RecyclerView getmRecyclerView(){
        return mRecyclerView;
    }

    public DragSortGridView getDragSortGridView(){
        return dragSortGridView;
    }

    public ImageView getImageView(){
        return imageView;
    }
}
