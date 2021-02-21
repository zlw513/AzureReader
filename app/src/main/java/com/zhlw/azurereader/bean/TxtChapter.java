package com.zhlw.azurereader.bean;

/**
 * Created by newbiechen on 17-7-1.
 */
public class TxtChapter {
    //章节名(共用)
    public String title;
    //章节内容在文章中的起始位置(本地)
    public long start;
    //章节内容在文章中的终止位置(本地)
    public long end;
    //选中目录
    boolean isSelect;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
