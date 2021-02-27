package com.zhlw.azurereader.utils;

import android.content.Intent;
import android.text.Html;
import android.util.Log;

import com.zhlw.azurereader.constant.URLCONST;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.entity.Chapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 天籁小说网html解析工具
 */
public class TianLaiReadUtil {

    /**
     * 从html中获取章节正文
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
     *
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
            String title = content.substring(content.indexOf("\"  >")+4, content.lastIndexOf("<"));
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
     * 获取主页html中书的信息  用于从天籁小说主页获取书城推荐中列出的小说信息
     *
     * @param html
     * @return
     */
    public static ArrayList<Book> getBooksFromHtml(String html) {
        ArrayList<Book> books = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("l");//  l 代表每日推荐
        Element div = divs.get(0);
        for (Element element : div.children()) {
            //element 是 <div class="item">
            Book book = new Book();
            Element img = element.child(0);
            book.setImgUrl(img.child(0).child(0).attr("src"));//保存图片连接
            book.setChapterUrl(URLCONST.nameSpace_tianlai + img.child(0).attr("href"));
            Element info = element.child(1);
            book.setAuthor(info.child(0).child(0).text()); //作者名
            book.setName(info.child(0).child(1).text());  //书名
            book.setDesc(info.child(1).text()); //简介
            books.add(book);
        }
        return books;
    }

    /**
     * 这个方法用来获取 分类中更多的书
     * @param html
     * @return
     */
    public static ArrayList<Book> getClassifyBooksFromHtml(String html) {
        ArrayList<Book> books = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("l");//  l 代表每日推荐
        Element div = divs.get(0).child(1);
        for (Element element : div.children()) {
            Book book = new Book();
            String res = element.child(0).text();
            book.setName(res.substring(1, res.length()-1));
            book.setAuthor(element.child(2).text());
            book.setImgUrl(URLCONST.nameSpace_tianlai + StringUtils.getImageUrl(element.child(0).child(0).attr("href")));
            book.setChapterUrl(URLCONST.nameSpace_tianlai + element.child(0).child(0).attr("href"));
            book.setDesc("暂无简介");
            books.add(book);
        }
        Elements divs1 = doc.getElementsByClass("r");
        Element div1 = divs1.get(0).child(1);
        for (Element element:div1.children()){
            Book book = new Book();
            book.setName(element.child(0).text());
            book.setAuthor(element.child(1).text());
            book.setChapterUrl(URLCONST.nameSpace_tianlai + element.child(0).child(0).attr("href"));
            book.setImgUrl(URLCONST.nameSpace_tianlai + StringUtils.getImageUrl(element.child(0).child(0).attr("href")));
            book.setDesc("暂无简介");
            books.add(book);
        }
        return books;
    }

    /**
     * 获取主页html中书的信息  用于从天籁小说主页中获取书城  6 大分类 中列出的小说信息
     *
     * @param html
     * @return
     */
    public static HashMap<String, ArrayList<Book>> getBooksFromHtmlClassify(String html) {
        HashMap<String, ArrayList<Book>> resMap = new HashMap<>();
        int place = 0;
        String[] titles = {"玄幻", "修真", "都市", "历史", "网游", "科幻"};
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("novelslist");//  l 代表每日推荐
        Element div = divs.get(0);
        Element div1 = divs.get(1);
        for (Element element : div.children()) {
            ArrayList<Book> books = new ArrayList<>();
            //这一块循环是 获取推荐中有图片的那一块的  上 三 本
            if (place <= 2) {
                Element title = element.child(1);
                Book book = new Book();
                book.setImgUrl(title.child(0).child(0).child(0).attr("src"));//保存图片连接
                book.setChapterUrl(URLCONST.nameSpace_tianlai + title.child(0).child(0).attr("href"));
                book.setName(title.child(1).child(0).text());
                books.add(book);
            } else {
                break;
            }
            for (Element element1 : element.child(2).children()) {
                Book book1 = new Book();
                //这块是获取小分类的
                String tempStr = element1.text();
                book1.setName(element1.text().substring(0, tempStr.indexOf("/")));
                book1.setAuthor(element1.text().substring(tempStr.indexOf("/") + 1));
                book1.setChapterUrl(URLCONST.nameSpace_tianlai + element1.child(0).attr("href"));
                book1.setImgUrl(StringUtils.getImageUrl(URLCONST.nameSpace_tianlai + element1.child(0).attr("href")));//设置图片地址
                books.add(book1);
            }
            resMap.put(titles[place], books);
            place++;
        }

        for (Element element : div1.children()) {
            ArrayList<Book> books = new ArrayList<>();
            //这一块循环是 获取推荐中有图片的那一块的  上 三 本
            if (place <= 5){
                Element title = element.child(1);
                Book book = new Book();
                book.setImgUrl(title.child(0).child(0).child(0).attr("src"));//保存图片连接
                book.setChapterUrl(URLCONST.nameSpace_tianlai + title.child(0).child(0).attr("href"));
                book.setName(title.child(1).child(0).text());
                books.add(book);
            } else {
                break;
            }
            for (Element element1 : element.child(2).children()) {
                Book book1 = new Book();
                //这块是获取小分类的
                String tempStr = element1.text();
                book1.setName(element1.text().substring(0, tempStr.indexOf("/")));
                book1.setAuthor(element1.text().substring(tempStr.indexOf("/") + 1));
                book1.setChapterUrl(URLCONST.nameSpace_tianlai + element1.child(0).attr("href"));
                book1.setImgUrl(StringUtils.getImageUrl(URLCONST.nameSpace_tianlai + element1.child(0).attr("href")));//设置图片地址
                books.add(book1);
            }
            resMap.put(titles[place], books);
            place++;
        }
        return resMap;
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
            book.setChapterUrl(title.child(0).attr("href"));
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
            books.add(book);
        }
        return books;
    }

    public static int getPageNum(String html) {
        Document doc = Jsoup.parse(html);
        Elements pageDivs = doc.getElementsByClass("search-result-page");
        Element pageDiv = pageDivs.get(0);
        Element page = pageDiv.getElementsByClass("search-result-page-main").get(0);//获取<div class="search-result-page-main">中的内容
        String pageNum = "0";
        for (Element element : page.children()) {
            String info = element.text();
            if (info.contains("末页")) {
                Pattern pattern = Pattern.compile("page=");
                Matcher matcher = pattern.matcher(element.attr("href"));
                if (matcher.find()) {
                    pageNum = element.attr("href").substring(matcher.end());
                }
            }
        }
        return Integer.parseInt(pageNum);
    }

    /**
     * 通过书名反查书籍信息
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

    public static Book getBookInfo(String html){
        Book book = new Book();
        Document doc = Jsoup.parse(html);
        Elements divs = doc.getElementsByClass("box_con");
        Element div = divs.get(0).children().get(1);//<div id="maininfo"> 这里获取的就是上面的maininfo
        Log.d("zlww", "getBookInfoBySearchHtml: div "+div);
        book.setName(div.children().get(0).child(0).text());
        book.setAuthor(div.children().get(0).child(1).text());
        book.setType(div.children().get(0).child(2).ownText());//只获取它自己的text，不要“子类”中的text
        book.setUpdateDate(div.children().get(0).child(3).text());
        book.setNewestChapterTitle(div.children().get(0).child(4).child(0).text());
        book.setNewestChapterUrl(div.children().get(0).child(4).child(0).attr("href"));
        book.setDesc(div.children().get(1).text());
        return book;
    }

}
