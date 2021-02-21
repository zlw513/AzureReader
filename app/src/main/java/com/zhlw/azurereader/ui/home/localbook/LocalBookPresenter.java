package com.zhlw.azurereader.ui.home.localbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.adapter.BookItemAdapter;
import com.zhlw.azurereader.adapter.LocalBookDragAdapter;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.custom.DragSortGridView;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.ui.home.MainActivity;
import com.zhlw.azurereader.ui.read.ReadActivity;
import com.zhlw.azurereader.ui.search.SearchBookActivity;
import com.zhlw.azurereader.utils.DateHelper;
import com.zhlw.azurereader.utils.ToastUtils;

import java.util.ArrayList;

public class LocalBookPresenter implements BasePresenter {

    private LocalBookFragment mLocalBookFragment;
    private ArrayList<Book> mBooks = new ArrayList<>();
    private BookItemAdapter mBookItemAdapter;
    private BookService mBookService;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private LocalBookDragAdapter mLocalBookDragAdapter;//切换为宫格风格的item adapter
    private MainActivity mainActivity;
    private TextView gridModelText;

    LocalBookPresenter(LocalBookFragment localBookFragment){
        mLocalBookFragment = localBookFragment;
        mBookService = BookService.getInstance();
        mainActivity = (MainActivity) mLocalBookFragment.getActivity();//获取到mainactivity
    }

    private void init(){
        initBook();
        if (mRecyclerView == null) mRecyclerView = mLocalBookFragment.getmRecyclerView();
        if (mBooks == null || mBooks.size() == 0){
            //这不用改
            mRecyclerView.setVisibility(View.GONE);
            mLocalBookFragment.getDragSortGridView().setVisibility(View.GONE);
            mLocalBookFragment.getmLinearLayout().setVisibility(View.VISIBLE);
        } else {
            mLocalBookFragment.getmLinearLayout().setVisibility(View.GONE);
            //有数据才进这里
            if (mBookItemAdapter == null || mLocalBookDragAdapter == null){
                mContext = mLocalBookFragment.getContext();
                mBookItemAdapter = new BookItemAdapter(R.layout.layout_rv_localbook_item, mBooks,mContext);
                mBookItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                        Intent intent = new Intent(mContext,ReadActivity.class);
                        final Book book = mBooks.get(position);
                        intent.putExtra(APPCONST.BOOK,book);//将信息发送到readactivity
                        book.setNoReadNum(0);//未读章数量
                        mContext.startActivity(intent);
                    }
                });

                OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
                    Book book;
                    @Override
                    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                        book = mBooks.get(pos);
                    }
                    @Override
                    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}
                    @Override
                    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                        if (book != null) remove(book);
                        book = null;
                        if (mBooks.size() == 0) getData();
                    }
                    @Override
                    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {
                    }
                };

                ItemDragAndSwipeCallback ic = new ItemDragAndSwipeCallback(mBookItemAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(ic);
                itemTouchHelper.attachToRecyclerView(mRecyclerView);

                //开启滑动删除
                mBookItemAdapter.enableSwipeItem();
                mBookItemAdapter.setOnItemSwipeListener(onItemSwipeListener);

                mBookItemAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
                mBookItemAdapter.isFirstOnly(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mLocalBookFragment.getContext()));
                mRecyclerView.setAdapter(mBookItemAdapter);

                //这下面是初始化 mLocalBookDragAdapter 的
                mLocalBookDragAdapter = new LocalBookDragAdapter(mLocalBookFragment.getContext(), R.layout.gridview_book_item, mBooks, false);
                mLocalBookFragment.getDragSortGridView().setDragModel(-1);
                mLocalBookFragment.getDragSortGridView().setTouchClashparent(mainActivity.getViewPager());
                mLocalBookFragment.getDragSortGridView().setAdapter(mLocalBookDragAdapter);

                //获得状态保存的方法
                if (mainActivity.getaSwitch().isChecked()) {
                    mLocalBookFragment.getDragSortGridView().setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            } else {
                if (mainActivity.getaSwitch().isChecked()) mLocalBookDragAdapter.notifyDataSetChanged();
                else mBookItemAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 刷新数据的方法
     */
    public void getData(){
        init();
    }

    /**
     * 重新从数据库中查询书籍数据
     */
    private void initBook(){
        mBooks.clear();
        mBooks.addAll(mBookService.getAllBooks());
    }

    //删除书籍数据
    public void remove(Book item) {
        mBooks.remove(item);
        mBookItemAdapter.notifyDataSetChanged();
        mBookService.deleteBook(item);//删除其在数据库中的字段
    }

    /**
     * 初始化全靠它了
     */
    @Override
    public void start() {
        gridModelText = mainActivity.getGridModel();
        mLocalBookFragment.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mLocalBookFragment.getContext(), SearchBookActivity.class);
                mLocalBookFragment.startActivity(intent);
            }
        });

        mLocalBookFragment.getDragSortGridView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mLocalBookDragAdapter.ismEditState()){
                    mLocalBookDragAdapter.setmEditState(true);
                    mLocalBookDragAdapter.notifyDataSetChanged();
                    ToastUtils.showToast("当前处于编辑模式，再次长按可以退出");
                } else {
                    mLocalBookDragAdapter.setmEditState(false);
                    mLocalBookDragAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        mainActivity.getaSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    gridModelText.setTextColor(mainActivity.getResources().getColor(R.color.sys_red2,null));
                    mainActivity.getaSwitch().setTextColor(mainActivity.getResources().getColor(R.color.colorWrite,null));
                    if (mLocalBookDragAdapter != null) mLocalBookDragAdapter.notifyDataSetChanged();
                    mLocalBookFragment.getDragSortGridView().setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    if (mLocalBookDragAdapter != null && mLocalBookDragAdapter.ismEditState()) mLocalBookDragAdapter.setmEditState(false);
                    mainActivity.getaSwitch().setTextColor(mainActivity.getResources().getColor(R.color.sys_red2,null));
                    gridModelText.setTextColor(mainActivity.getResources().getColor(R.color.colorWrite,null));
                    if (mBookItemAdapter != null) mBookItemAdapter.notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLocalBookFragment.getDragSortGridView().setVisibility(View.GONE);
                }
            }
        });
    }

}
