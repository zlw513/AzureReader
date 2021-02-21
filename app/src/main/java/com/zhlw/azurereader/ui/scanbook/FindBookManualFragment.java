package com.zhlw.azurereader.ui.scanbook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.bean.LocalFileBean;
import com.zhlw.azurereader.custom.DividerItemDecoration;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.utils.FileStack;
import com.zhlw.azurereader.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindBookManualFragment extends BaseFileFragment {

    @BindView(R.id.file_category_tv_path)
    TextView mTvPath;
    @BindView(R.id.file_category_tv_back_last)
    TextView mTvBackLast;
    @BindView(R.id.rv_file_category)
    RecyclerView mRvFileCategory;

    List<LocalFileBean> mFileBeans = new ArrayList<>();
    private FileStack mFileStack;//文件栈
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = setContentView(container, R.layout.fragment_findlocal_book_manual);
        return view;
    }

    public static FindBookManualFragment newInstance() {
        FindBookManualFragment fragment = new FindBookManualFragment();
        return fragment;
    }

    @Override
    public void initView() {
        super.initView();
        mFileStack = new FileStack();

        mAdapter = new LocalFileAdapter(mFileBeans);
        mRvFileCategory.setLayoutManager(new LinearLayoutManager(context));
        mRvFileCategory.addItemDecoration(new DividerItemDecoration(context));
        mRvFileCategory.setAdapter(mAdapter);

        File root=null;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            root = context.getExternalFilesDir("/storage/emulated/0");
//        } else {
//            root = Environment.getExternalStorageDirectory();
//        }
        root = Environment.getExternalStorageDirectory();
        // ↑这里的解决方案是 在  manifest中 application 标签中加android:requestLegacyExternalStorage="true" 即可解决问题
        toggleFileTree(root);

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            File file = mFileBeans.get(position).getFile();
            if (file.isDirectory()) {
                //保存当前信息。
                FileStack.FileSnapshot snapshot = new FileStack.FileSnapshot();
                snapshot.filePath = mTvPath.getText().toString();
                snapshot.files = new ArrayList<File>(mAdapter.getAllFiles());
                snapshot.scrollOffset = mRvFileCategory.computeVerticalScrollOffset();
                mFileStack.push(snapshot);
                //切换下一个文件
                toggleFileTree(file);
            } else {
                //如果是已加载的文件，则点击事件无效。
                String url = file.getAbsolutePath();
                if (BookService.getInstance().getLocalBookUrl(url) != null){
                    return;
                }
                //点击选中
                mAdapter.setCheckedItem(position);
                //反馈
                if (mListener != null) {
                    mListener.onItemCheckedChange(mAdapter.getItemIsChecked(position));
                }
            }
        });

        mTvBackLast.setOnClickListener(v -> {
            FileStack.FileSnapshot snapshot = mFileStack.pop();
            int oldScrollOffset = mRvFileCategory.computeHorizontalScrollOffset();
            if (snapshot == null) return;
            mTvPath.setText(snapshot.filePath);
            addFiles(snapshot.files);
            mRvFileCategory.scrollBy(0, snapshot.scrollOffset - oldScrollOffset);
            //反馈
            if (mListener != null) {
                mListener.onCategoryChanged();
            }
        });
    }

    private void toggleFileTree(File file) {
        //路径名
        mTvPath.setText(getString(R.string.file_storage_name, file.getPath()));
        //获取数据
        File[] files = file.listFiles(new SimpleFileFilter());
        //转换成List
        List<File> rootFiles = Arrays.asList(files);
        //排序
        Collections.sort(rootFiles, new FileComparator());
        //加入
        addFiles(rootFiles);
        //反馈
        if (mListener != null) {
            mListener.onCategoryChanged();
        }
    }

    /**
     * 添加文件数据
     * @param files
     */
    private void addFiles(List<File> files) {
        mFileBeans.clear();
        for (File file : files) {
            LocalFileBean localFileBean = new LocalFileBean();
            localFileBean.setSelect(false);
            localFileBean.setFile(file);
            mFileBeans.add(localFileBean);
        }
        mAdapter.notifyDataSetChanged();
    }

    public class FileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() && o2.isFile()) {
                return -1;
            }
            if (o2.isDirectory() && o1.isFile()) {
                return 1;
            }
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    public class SimpleFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (pathname.getName().startsWith(".")) {
                return false;
            }
            //文件夹内部数量为0
            if (pathname.isDirectory() && pathname.list().length == 0) {
                return false;
            }
            /**
             * 现在只支持TXT文件的显示
             */
            //文件内容为空,或者不以txt为开头
            if (!pathname.isDirectory() &&
                    (pathname.length() == 0 || !pathname.getName().endsWith(FileUtils.SUFFIX_TXT))) {
                return false;
            }
            return true;
        }
    }

}
