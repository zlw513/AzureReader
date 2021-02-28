package com.zhlw.azurereader.utils;

import android.text.Html;
import android.util.Log;

import com.zhlw.azurereader.constant.URLCONST;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.entity.Chapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 官术小说网html解析工具
 */
public class GuanShuReadUtil {

    /**
     * 从html中获取章节正文  应该一样的吧
     *
     * @param html
     * @return
     */
    public static String getContentFormHtml(String html) {
        Pattern pattern = Pattern.compile("<div id=\"content\">[\\s\\S]*?</div>");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String content = Html.fromHtml(matcher.group(0)).toString();
            char c = 160;
            String spaec = "" + c;
            content = content.replace(spaec, "  ");
            return content;
        } else {
            return "";
        }
    }

    /**
     * 从html中获取章节列表
     * @param html
     * @return
     */
    public static ArrayList<Chapter> getChaptersFromHtml(String html) {
        ArrayList<Chapter> chapters = new ArrayList<>();
        Pattern pattern = Pattern.compile("<dd><a href=\"([\\s\\S]*?)</a>");
        Matcher matcher = pattern.matcher(html);
        String lastTile = null;
        int i = 0;
        while (matcher.find()) {
            String content = matcher.group();
            String title = content.substring(content.indexOf("\"  >") + 4, content.lastIndexOf("<"));
            if (!StringHelper.isEmpty(lastTile) && title.equals(lastTile)) {
                continue;
            }
            Chapter chapter = new Chapter();
            chapter.setNumber(i++);
            chapter.setTitle(title);
            chapter.setUrl(content.substring(content.indexOf("\"") + 1, content.lastIndexOf("l\"") + 1));
            chapters.add(chapter);
            lastTile = title;
        }
        return chapters;
    }

    /**
     * 从搜索html中得到书列表
     *
     * @param html
     * @return
     */
    public static ArrayList<Book> getBooksFromSearchHtml(String html) {
        ArrayList<Book> books = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("result-list");
        Element div = divs.get(0);
        for (Element element : div.children()) {
            Book book = new Book();
            Element img = element.child(0).child(0).child(0);
            book.setImgUrl(img.attr("src"));
            Element title = element.getElementsByClass("result-item-title result-game-item-title").get(0);
            book.setName(title.child(0).attr("title"));
            String tempChapterUrl = title.child(0).attr("href").substring(5);
            book.setChapterUrl("https://www.thxsw.com/html/" + tempChapterUrl.substring(1, 3)+tempChapterUrl);// 拼接成类似这样的 https://www.thxsw.com/html/86/86232/
            Log.d("zlww", "getBooksFromSearchHtml guanshu ChapterUrl is  "+book.getChapterUrl());
            Element desc = element.getElementsByClass("result-game-item-desc").get(0);
            book.setDesc("官术网："+desc.text());
            Element info = element.getElementsByClass("result-game-item-info").get(0);
            for (Element element1 : info.children()) {
                String infoStr = element1.text();
                if (infoStr.contains("作者：")) {
                    book.setAuthor(infoStr.replace("作者：", "").replace(" ", ""));
                } else if (infoStr.contains("类型：")) {
                    book.setType(infoStr.replace("类型：", "").replace(" ", ""));
                } else if (infoStr.contains("更新时间：")) {
                    book.setUpdateDate(infoStr.replace("更新时间：", "").replace(" ", ""));
                } else {
                    Element newChapter = element1.child(1);
                    book.setNewestChapterUrl(newChapter.attr("href"));
                    book.setNewestChapterTitle(newChapter.text());
                }
            }
            books.add(book);
        }
        return books;
    }

    /**
     * 这里和23不一样  具体是Pattern.compile("p=");这里
     * @param html
     * @return
     */
    public static int getPageNum(String html) {
        Document doc = Jsoup.parse(html);
        Elements pageDivs = doc.getElementsByClass("search-result-page");
        Element pageDiv = pageDivs.get(0);
        Element page = pageDiv.getElementsByClass("search-result-page-main").get(0);//获取<div class="search-result-page-main">中的内容
        String pageNum = "0";
        for (Element element : page.children()) {
            String info = element.text();
            if (info.contains("末页")) {
                Log.d("debuging", "在末页了");
                Pattern pattern = Pattern.compile("p=");
                Matcher matcher = pattern.matcher(element.attr("href"));
                if (matcher.find()) {
                    pageNum = element.attr("href").substring(matcher.end());
                }
            }
        }
        return Integer.parseInt(pageNum);
    }

    /**
     * 通过书名反查书籍信息 在本地书籍加入书架时用的  暂时未使用
     */
    public static Book getBookInfoBySearchHtml(String html, String bookName) {
        Book book = new Book();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("result-list");
        Element div = divs.get(0);
        for (Element element : div.children()) {
            Element title = element.getElementsByClass("result-item-title result-game-item-title").get(0);
            if (bookName.equals(title.child(0).attr("title"))) {
                book.setName(title.child(0).attr("title"));
            } else {
                continue;
            }
            Element img = element.child(0).child(0).child(0);
            book.setImgUrl(img.attr("src"));
//            book.setChapterUrl(title.child(0).attr("href"));//本地书籍就不设置网上的章节url了
            Element desc = element.getElementsByClass("result-game-item-desc").get(0);
            book.setDesc(desc.text());
            Element info = element.getElementsByClass("result-game-item-info").get(0);
            for (Element element1 : info.children()) {
                String infoStr = element1.text();
                if (infoStr.contains("作者：")) {
                    book.setAuthor(infoStr.replace("作者：", "").replace(" ", ""));
                } else if (infoStr.contains("类型：")) {
                    book.setType(infoStr.replace("类型：", "").replace(" ", ""));
                } else if (infoStr.contains("更新时间：")) {
                    book.setUpdateDate(infoStr.replace("更新时间：", "").replace(" ", ""));
                } else {
                    Element newChapter = element1.child(1);
                    book.setNewestChapterUrl(newChapter.attr("href"));
                    book.setNewestChapterTitle(newChapter.text());
                }
            }
            break;
        }
        return book;
    }

}
