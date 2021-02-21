package com.zhlw.azurereader.ui.scanbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.zhlw.azurereader.bean.LocalFileBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by newbiechen on 17-7-10.
 * 扫描本地文件的基础Fragment类
 */
public abstract class BaseFileFragment extends Fragment {

    protected LocalFileAdapter mAdapter;
    protected OnFileCheckedListener mListener;
    protected boolean isCheckedAll;
    protected CompositeDisposable mDisposable;
    private View mView;

    //设置当前列表为全选
    public void setCheckedAll(boolean checkedAll) {
        if (mAdapter == null) return;
        isCheckedAll = checkedAll;
        mAdapter.setCheckdAll(checkedAll);
    }

    public void setChecked(boolean checked) {
        isCheckedAll = checked;
    }

    //当前fragment是否全选
    public boolean isCheckedAll() {
        return isCheckedAll;
    }

    //获取被选中的数量
    public int getCheckedCount() {
        if (mAdapter == null) return 0;
        return mAdapter.getCheckedCount();
    }

    //获取被选中的文件列表
    public List<File> getCheckedFiles() {
        List<File> files = new ArrayList<>();
        if (mAdapter != null) {
            for (LocalFileBean localFileBean : mAdapter.getCheckedFiles()) {
                files.add(localFileBean.getFile());
            }
        }
        return files;
    }

    //获取文件的总数
    public int getFileCount() {
        return mAdapter != null ? mAdapter.getItemCount() : null;
    }

    //获取可点击的文件的数量
    public int getCheckableCount() {
        if (mAdapter == null) return 0;
        return mAdapter.getCheckableCount();
    }

    /**
     * 删除选中的文件
     */
    public void deleteCheckedFiles() {
        //删除选中的文件
        List<LocalFileBean> files = mAdapter.getCheckedFiles();
        //删除显示的文件列表
        mAdapter.removeCheckedItems(files);
        //删除选中的文件
        for (LocalFileBean localFileBean : files) {
            if (localFileBean.getFile().exists()) {
                localFileBean.getFile().delete();
            }
        }
    }

    //设置文件点击监听事件
    public void setOnFileCheckedListener(OnFileCheckedListener listener) {
        mListener = listener;
    }

    //文件点击监听
    public interface OnFileCheckedListener {
        void onItemCheckedChange(boolean isChecked);
        void onCategoryChanged();
    }

    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    /**
     * 不使用Databinding设置布局
     *
     * @param resId  布局layout
     */
    public View setContentView(ViewGroup container, @LayoutRes int resId) {
        if (mView == null) {
            mView = LayoutInflater.from(getActivity()).inflate(resId, container, false);
            ButterKnife.bind(this, mView);
            initView();
        }
        return mView;
    }

    public void initView() {

    }
}
