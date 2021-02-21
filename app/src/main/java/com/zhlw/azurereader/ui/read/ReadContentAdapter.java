package com.zhlw.azurereader.ui.read;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spreada.utils.chinese.ZHConverter;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.api.GuanShuWangApi;
import com.zhlw.azurereader.application.SysManager;
import com.zhlw.azurereader.constant.URLCONST;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.bean.Setting;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.enums.Font;
import com.zhlw.azurereader.enums.Language;
import com.zhlw.azurereader.greendao.entity.Chapter;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.greendao.service.ChapterService;
import com.zhlw.azurereader.utils.LocalBookReadContentUtiles;
import com.zhlw.azurereader.utils.StringHelper;
import com.zhlw.azurereader.utils.ToastUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 增加本地书籍的阅读方法
 */
public class ReadContentAdapter extends RecyclerView.Adapter<ReadContentAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<Chapter> mDatas;
    private OnClickItemListener mOnClickItemListener;
    private View.OnTouchListener mOnTouchListener;

    private ChapterService mChapterService;
    private BookService mBookService;
    private Setting mSetting;
    private Book mBook;
    private Typeface mTypeFace;
    private TextView curTextView;
    private int mResourceId;
    private Context mContext;
    private RecyclerView rvContent;
    private LocalBookReadContentUtiles mReadContentUtiles;//  1
    private String preContent,nextContent;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ViewHolder viewHolder = (ViewHolder) msg.obj;
                    viewHolder.tvErrorTips.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public ReadContentAdapter(Context context, int resourceId, ArrayList<Chapter> datas, Book book,LocalBookReadContentUtiles readContentUtiles) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        mResourceId = resourceId;
        mChapterService = new ChapterService();
        mBookService = BookService.getInstance();
        mSetting = SysManager.getSetting();
        mBook = book;
        mReadContentUtiles = readContentUtiles;//  2
        mContext = context;
        initFont();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }
        TextView tvTitle;
        TextView tvContent;
        TextView tvErrorTips;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private Chapter getItem(int position) {
        return mDatas.get(position);
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (rvContent == null) rvContent = (RecyclerView) viewGroup;
        View view = mInflater.inflate(mResourceId, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.tvTitle =  view.findViewById(R.id.tv_title);
        viewHolder.tvContent = view.findViewById(R.id.tv_content);
        viewHolder.tvErrorTips = view.findViewById(R.id.tv_loading_error_tips);//加载失败的提示
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        initView(i, viewHolder);
        if (mOnTouchListener != null){
            viewHolder.itemView.setOnTouchListener(mOnTouchListener);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickItemListener != null) {
                    mOnClickItemListener.onClick(viewHolder.itemView, i);
                }
            }
        });
    }

    private void initView(final int postion, final ViewHolder viewHolder) {
        final Chapter chapter = getItem(postion);

        viewHolder.tvContent.setTypeface(mTypeFace);
        viewHolder.tvTitle.setTypeface(mTypeFace);
        viewHolder.tvErrorTips.setVisibility(View.GONE);
        viewHolder.tvTitle.setText("【" + getLanguageContext(chapter.getTitle()) + "】");
        if (mSetting.isDayStyle()) {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(mSetting.getReadWordColor(),null));
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(mSetting.getReadWordColor(),null));
        } else {
            viewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.sys_night_word,null));
            viewHolder.tvContent.setTextColor(mContext.getResources().getColor(R.color.sys_night_word,null));
        }

        viewHolder.tvTitle.setTextSize(mSetting.getReadWordSize() + 2);
        viewHolder.tvContent.setTextSize(mSetting.getReadWordSize());
        viewHolder.tvErrorTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBook.getIsLocal()){
                    ToastUtils.showToast("本地书籍解析失败...");
                } else {
                    getChapterContent(chapter, viewHolder);
                }
            }
        });
        if (mBook.getIsLocal()) chapter.setContent(getLocalBookContent(postion));//  设置当前章节的内容 本地书籍的
        if (StringHelper.isEmpty(chapter.getContent())) {
            getChapterContent(chapter, viewHolder);
        } else {
            viewHolder.tvContent.setText(getLanguageContext(chapter.getContent()));
        }
        curTextView = viewHolder.tvContent;
        //预加载上下一章的内容
        preLoading(postion);
        lastLoading(postion);
    }

    public void notifyDataSetChangedBySetting() {
        mSetting = SysManager.getSetting();
        initFont();
        super.notifyDataSetChanged();
    }

    public TextView getCurTextView() {
        return curTextView;
    }

    private String getLanguageContext(String content) {
        if (mSetting.getLanguage() == Language.traditional && mSetting.getFont() == Font.默认字体) {
            return ZHConverter.convert(content, ZHConverter.TRADITIONAL);
        }
        return content;
    }

    private String getLocalBookContent(int pos){ //  3
        return mReadContentUtiles.getContent(pos);
    }

    /**
     * 加载章节内容
     *
     * @param chapter
     * @param viewHolder
     */
    private void getChapterContent(final Chapter chapter, final ViewHolder viewHolder) {
        if (viewHolder != null) {
            viewHolder.tvErrorTips.setVisibility(View.GONE);
        }
        Chapter cacheChapter = mChapterService.findChapterByBookIdAndTitle(chapter.getBookId(), chapter.getTitle());

        if (cacheChapter != null && !StringHelper.isEmpty(cacheChapter.getContent())) {
            chapter.setContent(cacheChapter.getContent());//获得章节内容
            chapter.setId(cacheChapter.getId());
            if (viewHolder != null) {
                if (mSetting.getLanguage() == Language.traditional) {
                    viewHolder.tvContent.setText(ZHConverter.convert(chapter.getTitle(), ZHConverter.TRADITIONAL));
                } else {
                    viewHolder.tvContent.setText(chapter.getContent());
                }
                viewHolder.tvErrorTips.setVisibility(View.GONE);
            }
        } else {
            if (mBook.getDesc().contains("官术网")){
                //解析官术网
                GuanShuWangApi.getChapterContent(chapter.getUrl(), new ResultCallback() {
                    @Override
                    public void onFinish(final Object o, int code) {
                        chapter.setContent((String) o);
                        mChapterService.saveOrUpdateChapter(chapter);
                        if (viewHolder != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.tvContent.setText(getLanguageContext((String) o));
                                    viewHolder.tvErrorTips.setVisibility(View.GONE);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (viewHolder != null) {
                            mHandler.sendMessage(mHandler.obtainMessage(1, viewHolder));
                        }
                    }
                });
            } else {
                CommonApi.getChapterContent(chapter.getUrl(), new ResultCallback() {
                    @Override
                    public void onFinish(final Object o, int code) {
                        chapter.setContent((String) o);
                        mChapterService.saveOrUpdateChapter(chapter);
                        if (viewHolder != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.tvContent.setText(getLanguageContext((String) o));
                                    viewHolder.tvErrorTips.setVisibility(View.GONE);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (viewHolder != null) {
                            mHandler.sendMessage(mHandler.obtainMessage(1, viewHolder));
                        }
                    }
                });
            }
        }
    }

    /**
     * 预加载下一章
     */
    private void preLoading(int position) {
        if (position + 1 < getItemCount()) {
            Chapter chapter = getItem(position + 1);
            if (StringHelper.isEmpty(chapter.getContent())) {//如果下一章的内容为空，就预加载
                if (mBook.getIsLocal()){
                    CommonApi.getBookChapterContent(mReadContentUtiles, new ResultCallback() {
                        @Override
                        public void onFinish(Object o, int code) {
                            chapter.setContent((String)o);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    }, position + 1);
                } else {
                    getChapterContent(chapter, null);
                }
            }
        }
    }

    /**
     * 预加载上一章
     * @param position
     */
    private void lastLoading(int position) {
        if (position > 0) {
            Chapter chapter = getItem(position - 1);
            if (StringHelper.isEmpty(chapter.getContent())) {
                if (mBook.getIsLocal()){
                    CommonApi.getBookChapterContent(mReadContentUtiles, new ResultCallback() {
                        @Override
                        public void onFinish(Object o, int code) {
                            chapter.setContent((String) o);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    }, position - 1);
                } else {
                    getChapterContent(chapter, null);
                }
            }
        }
    }

    public void saveHistory(int position) {
        if (!StringHelper.isEmpty(mBook.getId())) {
            mBook.setHisttoryChapterNum(position);
            mBookService.updateEntity(mBook);
        }
    }

    public void initFont() {
        if (mSetting.getFont() == Font.默认字体) {
            mTypeFace = null;
        } else {
            mTypeFace = Typeface.createFromAsset(mContext.getAssets(), mSetting.getFont().path);
        }
    }

    private void hiddenSoftInput(EditText editText){
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmOnClickItemListener(OnClickItemListener mOnClickItemListener) {
        this.mOnClickItemListener = mOnClickItemListener;
    }

    public void setmOnTouchListener(View.OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }

    public interface OnClickItemListener {
        void onClick(View view, int positon);
    }

}
