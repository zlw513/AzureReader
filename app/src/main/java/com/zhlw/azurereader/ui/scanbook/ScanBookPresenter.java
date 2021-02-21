package com.zhlw.azurereader.ui.scanbook;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.adapter.BaseFragmentsViewPageAdapter;
import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.utils.Constant;
import com.zhlw.azurereader.utils.StringUtils;
import com.zhlw.azurereader.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 等待其两个子fragment的完成，它们完成后才可以进行这里的工作
 */
public class ScanBookPresenter implements BasePresenter {

    private String[] titles = {"智能导入", "手机目录"};
    private List<Fragment> mFragments;

    private FindBookManualFragment mFindBookManualFragment;
    private ScanBookAutoFragment mScanBookAutoFragment;
    private BaseFileFragment mCurUsedFragment;
    private ScanBookActivity mScanBookActivity;
    private Book book;
    private List<Book> mBooks = new ArrayList<>();
    private List<File> files;

    private BaseFileFragment.OnFileCheckedListener mListener = new BaseFileFragment.OnFileCheckedListener() {
        @Override
        public void onItemCheckedChange(boolean isChecked) {
            changeMenuStatus();
        }

        @Override
        public void onCategoryChanged() {
            //状态归零
            mCurUsedFragment.setCheckedAll(false);
            //改变菜单
            changeMenuStatus();
            //改变是否能够全选
            changeCheckedAllStatus();
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    //转换成Book,并存储
                    BookService.getInstance().addBooks(mBooks);
                    //改变菜单状态
                    changeMenuStatus();
                    //改变是否可以全选
                    changeCheckedAllStatus();
                    //提醒更新数据
                    mCurUsedFragment.mAdapter.notifyDataSetChanged();
                    //提示加入书架成功
                    ToastUtils.showToast(mScanBookActivity.getResources().getString(R.string.local_file_add_scuess, mBooks.size()));
                    break;
                case 2:
                    ToastUtils.showToast("无网络状态导入，书籍部分信息可能丢失......导入成功");
                    break;
            }
        }
    };

    public ScanBookPresenter(ScanBookActivity scanBookActivity){
        mScanBookActivity = scanBookActivity;
    }

    private void initView(){
        mFragments = new ArrayList<>();
        mScanBookAutoFragment = ScanBookAutoFragment.newInstance();
        mFindBookManualFragment = FindBookManualFragment.newInstance();
        mCurUsedFragment = mScanBookAutoFragment;

        mFragments.add(mScanBookAutoFragment);
        mFragments.add(mFindBookManualFragment);

        mScanBookActivity.getmViewPager().setAdapter(new BaseFragmentsViewPageAdapter(mScanBookActivity.getSupportFragmentManager(), titles, mFragments));
        mScanBookActivity.getmViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int flag = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mCurUsedFragment = mScanBookAutoFragment;
                } else {
                    flag++;
                    if (flag == 1) mFindBookManualFragment.setOnFileCheckedListener(mListener);
                    mCurUsedFragment = mFindBookManualFragment;
                }
                //改变菜单状态
                changeMenuStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //和viewpager关联
        mScanBookActivity.getmTabLayout().setupWithViewPager(mScanBookActivity.getmViewPager());
        mScanBookAutoFragment.setOnFileCheckedListener(mListener);
        setUpOnClickListener();
    }

    public void setUpOnClickListener(){
        mScanBookActivity.getmAddButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取选中的文件
                files = mCurUsedFragment.getCheckedFiles();
                convertToBook(files);//将文件转换为书籍
                mCurUsedFragment.setCheckedAll(false);
            }
        });
        mScanBookActivity.getmDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出，确定删除文件吗。
                new AlertDialog.Builder(mScanBookActivity)
                        .setTitle("删除文件")
                        .setMessage("确定删除文件吗?")
                        .setPositiveButton(mScanBookActivity.getResources().getString(R.string.tips_sure), (dialog, which) -> {
                            //删除选中的文件
                            mCurUsedFragment.deleteCheckedFiles();
                            //提示删除文件成功
                            ToastUtils.showToast("删除文件成功");
                        })
                        .setNegativeButton(mScanBookActivity.getResources().getString(R.string.tips_cancel), null)
                        .show();
            }
        });
        mScanBookActivity.getmCheckBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置全选状态
                mCurUsedFragment.setCheckedAll(mScanBookActivity.getmCheckBox().isChecked());
                //改变菜单状态
                changeMenuStatus();
            }
        });
    }

    /**
     * 将文件转换成Book
     * @param files:需要加载的文件列表
     * @return
     */
    private void convertToBook(List<File> files) {
        //在这里执行反查操作
        for (File file : files) {
            //判断文件是否存在
            if (!file.exists()) continue;
            getDetail(file.getName().replace(".txt", ""),file);//根据名字反查书籍信息
        }
    }

    private void getDetailFinished(File file){
        if (book == null){
            book = new Book();
            book.setChapterUrl(file.getAbsolutePath());//设置书籍url为本地书籍的url
            book.setName(file.getName().replace(".txt", ""));
            book.setIsLocal(true);
            book.setUpdateDate(StringUtils.
                    dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            mBooks.add(book);
        } else {
            Log.d("debuging", "getDetailFinished: 走错了");
            book.setChapterUrl(file.getAbsolutePath());//设置书籍url为本地书籍的url
            book.setIsLocal(true);//在这里给他设置上本地标签
            mBooks.add(book);
        }
    }

    private void getDetail(String bookName,File file){
        CommonApi.searchDeatil(bookName, new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                book = (Book) o;//查到的话此时的book对象将会含有很多信息
                if (book.getAuthor() == null || book.getAuthor().equals("")) book = null;
                getDetailFinished(file);//反查完成后调用转换
                if (mBooks.size() == files.size()) handler.sendMessage(handler.obtainMessage(1));//有网络时
            }

            @Override
            public void onError(Exception e) {
                book = null;//查不到返回新的book对象
                getDetailFinished(file);//反查完成后调用转换
                if (mBooks.size() == files.size()) handler.sendMessage(handler.obtainMessage(2));//无网络时导入的
            }
        });
    }

    /**
     * 改变底部选择栏的状态
     */
    private void changeMenuStatus() {
        //点击、删除状态的设置
        if (mCurUsedFragment.getCheckedCount() == 0) {
            mScanBookActivity.getmAddButton().setText(mScanBookActivity.getString(R.string.local_file_add_shelf));
            //设置某些按钮的是否可点击
            setMenuClickable(false);
            if (mScanBookActivity.getmCheckBox().isChecked()) {
                mCurUsedFragment.setChecked(false);
                mScanBookActivity.getmCheckBox().setChecked(mCurUsedFragment.isCheckedAll());
            }
        } else {
            mScanBookActivity.getmAddButton().setText(mScanBookActivity.getString(R.string.local_file_add_shelves, mCurUsedFragment.getCheckedCount()));
            setMenuClickable(true);

            //全选状态的设置
            //如果选中的全部的数据，则判断为全选
            if (mCurUsedFragment.getCheckedCount() == mCurUsedFragment.getCheckableCount()) {
                //设置为全选
                mCurUsedFragment.setChecked(true);
                mScanBookActivity.getmCheckBox().setChecked(mCurUsedFragment.isCheckedAll());
            } else if (mCurUsedFragment.isCheckedAll()) {
                //如果曾今是全选则替换
                mCurUsedFragment.setChecked(false);
                mScanBookActivity.getmCheckBox().setChecked(mCurUsedFragment.isCheckedAll());
            }
        }

        //重置全选的文字
        if (mCurUsedFragment.isCheckedAll()) {
            mScanBookActivity.getmCheckBox().setText("取消");
        } else {
            mScanBookActivity.getmCheckBox().setText("全选");
        }
    }

    /**
     * 改变全选按钮的状态
     */
    private void changeCheckedAllStatus() {
        //获取可选择的文件数量
        int count = mCurUsedFragment.getCheckableCount();

        //设置是否能够全选
        if (count > 0) {
            mScanBookActivity.getmCheckBox().setClickable(true);
            mScanBookActivity.getmCheckBox().setEnabled(true);
        } else {
            mScanBookActivity.getmCheckBox().setClickable(false);
            mScanBookActivity.getmCheckBox().setEnabled(false);
        }
    }

    private void setMenuClickable(boolean isClickable) {
        //设置是否可删除
        mScanBookActivity.getmDeleteButton().setEnabled(isClickable);
        mScanBookActivity.getmDeleteButton().setClickable(isClickable);

        //设置是否可添加书籍
        mScanBookActivity.getmAddButton().setEnabled(isClickable);
        mScanBookActivity.getmAddButton().setClickable(isClickable);
    }

    @Override
    public void start() {
        //这里是oncreate中执行的
        initView();
    }
}
