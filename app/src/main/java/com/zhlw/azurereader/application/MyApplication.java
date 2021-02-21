package com.zhlw.azurereader.application;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import androidx.appcompat.app.AppCompatDelegate;

import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.bean.UpdateInfo;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.constant.URLCONST;
import com.zhlw.azurereader.custom.DialogCreator;
import com.zhlw.azurereader.ui.BaseActivity;
import com.zhlw.azurereader.utils.CacheHelper;
import com.zhlw.azurereader.utils.DownloadMangerUtils;
import com.zhlw.azurereader.utils.HttpUtil;
import com.zhlw.azurereader.utils.OpenFileHelper;
import com.zhlw.azurereader.utils.TextHelper;
import com.zhlw.azurereader.utils.UriFileUtil;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MyApplication extends Application {

    private static Handler handler = new Handler();
    private static MyApplication application;
    private ExecutorService mFixedThreadPool;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        settingNightMode();//设置夜间模式
        HttpUtil.trustAllHosts();//信任所有证书
        mFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());//初始化线程池
        BaseActivity.setCloseAntiHijacking(true);
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public static Context getmContext() {
        return application;
    }

    public static Resources getAppResources() {
        return application.getResources();
    }

    public void newThread(Runnable runnable) {
        try {
            mFixedThreadPool.execute(runnable);
        } catch (Exception e) {
            e.printStackTrace();
            mFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());//初始化线程池
            mFixedThreadPool.execute(runnable);
        }
    }

    public void shutdownThreadPool(){
        mFixedThreadPool.shutdownNow();
    }

    /**
     * 主线程执行
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public static MyApplication getApplication() {
        return application;
    }

    private boolean isFolderExist(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * 获取app版本号
     *
     * @return
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = application.getPackageManager();
            PackageInfo info = manager.getPackageInfo(application.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取app版本号(String)
     *
     * @return
     */
    public static String getStrVersionName() {
        try {
            PackageManager manager = application.getPackageManager();
            PackageInfo info = manager.getPackageInfo(application.getPackageName(), 0);

            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    /**
     * 检查更新
     */
    public static void checkVersion(Activity activity) {
        UpdateInfo updateInfo = (UpdateInfo) CacheHelper.readObject(APPCONST.FILE_NAME_UPDATE_INFO);
        int versionCode = getVersionCode();
        if (updateInfo != null) {
            if (updateInfo.getNewestVersionCode() > versionCode) {
                updateApp(activity, updateInfo.getDownLoadUrl(), versionCode);
            }
        }
    }

    /**
     * 检查更新
     */
    public static void checkVersionByServer(final Activity activity) {
        CommonApi.getNewestAppVersion(new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                int versionCode = getVersionCode();
                int newestVersion = Integer.valueOf((String)o) ;

                if (newestVersion > versionCode) {
                    updateApp(activity, URLCONST.method_downloadApp, versionCode);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    /**
     * App自动升级
     *
     * @param activity
     * @param versionCode
     */
    public static void updateApp(final Activity activity, final String url, final int versionCode) {
        DialogCreator.createCommonDialog(activity, "发现新版本", null, "立即更新",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        TextHelper.showText("正在下载更新...");
                        DownloadMangerUtils.downloadFileOnNotificationByFinishListener(activity, APPCONST.UPDATE_APK_FILE_DIR,
                                "AzureReader" + versionCode + ".apk", url, "更新下载",
                                new DownloadMangerUtils.DownloadCompleteListener() {
                                    @Override
                                    public void onFinish(Uri uri) {
                                        ((BaseActivity) activity).setDisallowAntiHijacking(true);//暂停防界面劫持
                                        OpenFileHelper.openFile(application, new File(UriFileUtil.getPath(application, uri)));
                                    }

                                    @Override
                                    public void onError(String s) {

                                    }
                                });
                    }
                });
    }

    /*
    初始化夜间模式
    MODE_NIGHT_NO： 亮色(light)主题，不使用夜间模式
    MODE_NIGHT_YES：暗色(dark)主题，使用夜间模式
     */
    private void settingNightMode(){
        //TODO 需要存到sharedpreferences里去才能判断上一次退出时是什么状态
        AppCompatDelegate.setDefaultNightMode(APPCONST.isDayMode?AppCompatDelegate.MODE_NIGHT_NO:AppCompatDelegate.MODE_NIGHT_YES);
    }

}
