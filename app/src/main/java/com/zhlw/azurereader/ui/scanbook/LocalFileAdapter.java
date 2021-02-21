package com.zhlw.azurereader.ui.scanbook;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.bean.LocalFileBean;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.utils.Constant;
import com.zhlw.azurereader.utils.FileUtils;
import com.zhlw.azurereader.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalFileAdapter extends BaseQuickAdapter<LocalFileBean, BaseViewHolder> {
    private int mCheckedCount = 0;
    private BookService mBookService;//对book进行数据库操作的

    public LocalFileAdapter(@Nullable List<LocalFileBean> data) {
        super(R.layout.item_local_file, data);
        mBookService = BookService.getInstance();
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalFileBean item) {
        File file = item.getFile();
        if (file.isDirectory()) {
            helper.setVisible(R.id.file_iv_icon, true)
                    .setImageResource(R.id.file_iv_icon, R.drawable.ic_dir)
                    .setVisible(R.id.file_cb_select, false)
                    .setVisible(R.id.file_ll_brief, false)
                    .setText(R.id.file_tv_name, file.getName())
                    .setVisible(R.id.file_tv_sub_count, true)
                    .setText(R.id.file_tv_sub_count, mContext.getString(R.string.file_sub_count, file.list().length));
        } else {
            if (mBookService.getLocalBookUrl(item.getFile().getAbsolutePath()) != null) {
                helper.setVisible(R.id.file_cb_select, false)
                        .setVisible(R.id.file_iv_icon, true)
                        .setImageResource(R.id.file_iv_icon, R.drawable.ic_coll_book);
            } else {
                helper.setVisible(R.id.file_cb_select, true)
                        .setChecked(R.id.file_cb_select, item.isSelect())
                        .setVisible(R.id.file_iv_icon, false);
            }
            helper.setVisible(R.id.file_ll_brief, true)
                    .setVisible(R.id.file_tv_sub_count, false)
                    .setText(R.id.file_tv_name, file.getName())
                    .setText(R.id.file_tv_size, FileUtils.getFileSize(file.length()))
                    .setText(R.id.file_tv_date, StringUtils.dateConvert(file.lastModified(), Constant.FORMAT_FILE_DATE));
        }
    }

    /**
     * 删除选中文件
     */
    public void removeCheckedItems(List<LocalFileBean> localFileBeans) {
        mData.removeAll(localFileBeans);
        mCheckedCount -= localFileBeans.size();
        notifyDataSetChanged();
    }

    /**
     * 选中文件
     *
     * @param position
     */
    public void setCheckedItem(int position) {
        LocalFileBean bean = mData.get(position);
        //如果是已加载的文件，则点击事件无效。
        if (isFileLoaded(bean.getFile().getAbsolutePath())) return;

        if (bean.isSelect()) {
            bean.setSelect(false);
            --mCheckedCount;
        } else {
            bean.setSelect(true);
            ++mCheckedCount;
        }
        notifyDataSetChanged();
    }

    /**
     * 全选和反选
     *
     * @param isChecked
     */
    public void setCheckdAll(boolean isChecked) {
        mCheckedCount = 0;
        for (LocalFileBean localFileBean : mData) {
            if (localFileBean.getFile().isFile() && !isFileLoaded(localFileBean.getFile().getAbsolutePath())) {
                localFileBean.setSelect(isChecked);
                if (isChecked) {
                    ++mCheckedCount;
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 选中文件
     *
     * @return
     */
    public List<LocalFileBean> getCheckedFiles() {
        List<LocalFileBean> beans = new ArrayList<>();
        for (LocalFileBean localFileBean : mData) {
            if (localFileBean.isSelect()) {
                beans.add(localFileBean);
            }
        }
        return beans;
    }

    /**
     * 获取可点击文件数量
     *
     * @return
     */
    public int getCheckableCount() {
        int count = 0;
        for (LocalFileBean localFileBean : mData) {
            if (!isFileLoaded(localFileBean.getFile().getAbsolutePath()) && localFileBean.getFile().isFile()) {
                ++count;
            }
        }
        return count;
    }

    /**
     * 判断是否选中
     *
     * @param pos
     * @return
     */
    public boolean getItemIsChecked(int pos) {
        return mData.get(pos).isSelect();
    }

    /**
     * 获取选中数量
     *
     * @return
     */
    public int getCheckedCount() {
        return mCheckedCount;
    }

    /**
     * 获取所有文件或文件夹
     *
     * @return
     */
    public List<File> getAllFiles() {
        List<File> files = new ArrayList<>();
        for (LocalFileBean localFileBean : mData) {
            files.add(localFileBean.getFile());
        }
        return files;
    }

    /**
     * 是否加载过此文件
     * @param url 本书籍的地址
     * @return
     */
    private boolean isFileLoaded(String url) {
        //如果是已加载的文件，则点击事件无效。
        if (mBookService.getLocalBookUrl(url) != null) {
            return true;
        }
        return false;
    }
}
