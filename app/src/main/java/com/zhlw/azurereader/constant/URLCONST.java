package com.zhlw.azurereader.constant;

public class URLCONST {

    /***********************************添加多小说源  备用*****************************************/
    // 命名空间
    public static final String nameSpace_tianlai = "http://www.233txt.com";
    public static final String nameSpace_biyuwu = "https://www.thxsw.com";  // 备胎首选  官术网 2021-02-28
    public static final String nameSpace_midog = "http://www.92zw.la";
    public static final String nameSpace_630book_cc = "http://www.630book.cc";
    public static final String nameSpace_fengyuwenxue = "http://www.44pq.net";
    public static final String nameSpace_17xiaoshuowang = "http://www.17k.com";
    public static final String nameSpace_bixiawenxue = "http://www.bxwx9.org";
    public static final String nameSpace_yishou= "http://book.easou.com";
    public static final String nameSpace_renrenxiaoshuo = "http://www.ppxs.net";
    public static final String nameSpace_ranwenxiaoshuo = "http://www.ranwena.com";
    public static final String nameSpace_shuloumoval = "http://www.shulou.cc";
    public static final String nameSpace_shaoniannoval = "http://www.snwx8.com";
    public static final String nameSpace_siyuannoval = "http://www.syzww.net";
    public static final String nameSpace_pinshunoval = "http://www.vodtw.com";    //这里搜索有加密
    public static final String nameSpace_abcnoval = "http://www.yb3.cc";
    public static final String nameSpace_wuzhounoval = "http://www.gxwztv.com";
    /**************************************************************************************/

    public static String nameSpace_system = "http://10.10.123.31:8080/jeecg";

    public static boolean isRSA = false;

    // 搜索小说
    public static String method_buxiu_search = "http://www.233txt.com/search.php";
    public static String method_biyuwu_search = "https://m.thxsw.com/search.php";//现在使用官术网手机端的搜索

    // 获取最新版本号
    public static String method_getCurAppVersion = nameSpace_system + "/mReaderController.do?getCurAppVersion";

    // app下载
    public static String method_downloadApp = nameSpace_system + "/upload/app/AzureReader.apk";

}

