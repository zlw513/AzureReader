package com.zhlw.azurereader.ui.scanbook;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.bean.LocalFileBean;
import com.zhlw.azurereader.custom.DividerItemDecoration;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.utils.FileUtils;
import com.zhlw.azurereader.utils.ToastUtils;
import com.zhlw.azurereader.utils.rxhelper.RxUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 等待local file adapter的完成
 */
public class ScanBookAutoFragment extends BaseFileFragment {

    @BindView(R.id.btn_scan)
    Button mButton;
    @BindView(R.id.rv_files)
    RecyclerView mRecyclerView;
    @BindView(R.id.spin_kit)
    SpinKitView mSpinKitView;
    @BindView(R.id.tv_scanning)
    TextView mTextView;

    List<LocalFileBean> mFileBeans = new ArrayList<>();
    private Context context;

    public static ScanBookAutoFragment newInstance(){
        return new ScanBookAutoFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = setContentView(container, R.layout.fragment_findlocal_book_auto);
        return v;
    }

    @Override
    public void initView() {
        super.initView();
        //初始化界面等
        mAdapter = new LocalFileAdapter(mFileBeans);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //如果是已加载的文件，则点击事件无效。
                String url = mFileBeans.get(position).getFile().getAbsolutePath();//获取
                if (BookService.getInstance().getLocalBookUrl(url) != null){
                    return;//无效它的点击事件
                }
                mAdapter.setCheckedItem(position);
                if (mListener != null){
                    mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position));
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSpinKitView.getVisibility() != View.VISIBLE) {
                    scanFiles();
                }
            }
        });
    }

    /**
     * 搜索文件
     */
    private void scanFiles(){
        mSpinKitView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        addDisposable(FileUtils.getSDTxtFile().compose(RxUtils::toSimpleSingle).subscribe(
                files -> {
                    mSpinKitView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.GONE);
                    mFileBeans.clear();
                    if (files.size() == 0) {
                        ToastUtils.showToast("没有扫描到文件");
                    } else {
                        for (File file : files) {
                            LocalFileBean localFileBean = new LocalFileBean();
                            localFileBean.setSelect(false);
                            localFileBean.setFile(file);
                            mFileBeans.add(localFileBean);
                        }
                        mAdapter.notifyDataSetChanged();
                        //反馈
                        if (mListener != null) {
                            mListener.onCategoryChanged();
                        }
                    }
                }
        ));

    }
}
