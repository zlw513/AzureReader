package com.zhlw.azurereader.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.alibaba.fastjson.JSON;
import com.flod.loadingbutton.LoadingButton;
import com.zhlw.azurereader.bean.User;
import com.zhlw.azurereader.callback.HttpCallback;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.utils.HttpUtil;
import com.zhlw.azurereader.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivityPresenter implements BasePresenter {

    private LoginActivity mLoginActivity;
    private static boolean isAnimStart = false;
    private static LoadingButton mButton;

    LoginActivityPresenter(LoginActivity loginActivity) {
        mLoginActivity = loginActivity;
    }

    static class MyAsyncTask extends AsyncTask<Void,String,Void>{
        boolean flag = false;//登录成功的提示
        String userName;
        String passWord;
        User userInfo;
        // 校园网 地址   (1)10.129.36.196主楼     (2) 10.137.15.251 宿舍  (3) 网线 10.159.192.64
        String serverUrl = "http://10.159.192.64:8080/user/mobile/";
        int proformance;// 0代表登录功能，1代表注册功能

        MyAsyncTask(String username,String password,int prof){
            userName = username;
            passWord = password;
            proformance = prof;
        }

        /**
         * 回传的数据是个jasn格式数组，所以最后返回时要把 [] 去掉
         * @param in
         * @return
         */
        private String phaseToString(InputStream in){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = "";
            try {
                while ((line = br.readLine()) != null){
                    sb.append(line);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return sb.toString().substring(1,sb.length()-1);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isAnimStart = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (proformance == 0){
                    //这里获取用户登录成功还是失败的信息
                    HttpUtil.sendGetRequest_okHttp(serverUrl+userName, new HttpCallback() {
                        @Override
                        public void onFinish(String response) {

                        }

                        @Override
                        public void onFinish(InputStream in) {
                            //处理登录返回的结果
                            String response = phaseToString(in);
                            userInfo = JSON.parseObject(response, User.class);//转换为userbean类型
                            if (userInfo != null) {
                                flag = userInfo.getUsername().equals(userName) && userInfo.getUserPassword().equals(passWord);//登录成功
                            }
                        }

                        @Override
                        public void onFinish(Bitmap bm) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } else {
                    //注册功能
                    JSONObject jsonObject = new JSONObject();
                    // 参数put到json串里面
                    jsonObject.put("username",userName);
                    jsonObject.put("userPassword",passWord);
                    String data = String.valueOf(jsonObject);
                    HttpUtil.sendPostRequest(serverUrl + "tosignup",data , new HttpCallback() {
                        @Override
                        public void onFinish(String response) {

                        }

                        @Override
                        public void onFinish(InputStream in) {
                            //注册成功
                            String response = phaseToString(in);
                            if (!response.equals("")){
                                flag = true;
                            }
                        }

                        @Override
                        public void onFinish(Bitmap bm) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
                Thread.sleep(1600);
            } catch (InterruptedException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (flag){
                mButton.complete();
            } else {
                mButton.fail();
            }
            isAnimStart = false;
        }
    }

    @Override
    public void start() {
        mButton = mLoginActivity.getLoadingButton();
        mLoginActivity.getLoadingButton().setLoadingStrokeWidth((int) (mLoginActivity.getLoadingButton().getTextSize() * 0.14f));
        mLoginActivity.getLoadingButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mLoginActivity.getmEtUserName().getText().toString().equals("")&&!mLoginActivity.getmEtPassWord().getText().toString().equals("")){
                    if (mLoginActivity.getmTvResgister().getVisibility() == View.VISIBLE){
                        //此时为登录事件
                        if (!isAnimStart){
                            mLoginActivity.getLoadingButton().start();//开始动画
                            new MyAsyncTask(mLoginActivity.getmEtUserName().getText().toString(),mLoginActivity.getmEtPassWord().getText().toString(),0).execute();
                        }
                    } else {
                        if (!isAnimStart){
                            //此时为注册事件
                            mLoginActivity.getLoadingButton().start();//开始动画
                            new MyAsyncTask(mLoginActivity.getmEtUserName().getText().toString(),mLoginActivity.getmEtPassWord().getText().toString(),1).execute();
                        }
                    }
                } else {
                    ToastUtils.showToast("账号或密码填写未完整");
                }
            }
        });

        mLoginActivity.getLoadingButton().setOnLoadingListener(new LoadingButton.OnLoadingListenerAdapter(){
            @Override
            public void onLoadingStart() {
                super.onLoadingStart();
                mLoginActivity.getmTvToLogin().setClickable(false);
                mLoginActivity.getmTvResgister().setClickable(false);
            }

            @Override
            public void onLoadingStop() {
                super.onLoadingStop();
            }

            @Override
            public void onCompleted() {
                mLoginActivity.getmTvToLogin().setClickable(true);
                mLoginActivity.getmTvResgister().setClickable(true);
                if (mLoginActivity.getmTvResgister().getVisibility() == View.VISIBLE){
                    //检查状态的  这里是登录状态的情况
                    ToastUtils.showToast("登录成功");
                    Intent intent = new Intent();
                    intent.putExtra("用户名", "欢迎："+mLoginActivity.getmEtUserName().getText().toString());
                    mLoginActivity.setResult(Activity.RESULT_OK,intent);
                    mLoginActivity.finish();
                } else {
                    //这里是注册状态的情况
                    ToastUtils.showToast("注册成功");
                }
                super.onCompleted();
            }

            @Override
            public void onFailed() {
                mLoginActivity.getmTvToLogin().setClickable(true);
                mLoginActivity.getmTvResgister().setClickable(true);
                if (mLoginActivity.getmTvResgister().getVisibility() == View.VISIBLE) ToastUtils.showToast("登录失败，请检查用户名和密码");
                else ToastUtils.showToast("注册失败,用户可能已经存在");
                super.onFailed();
            }

            @Override
            public void onCanceled() {
                super.onCanceled();
            }
        });

        mLoginActivity.getmTvToLogin().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换回登录模式
                mLoginActivity.getmTvResgister().setVisibility(View.VISIBLE);
                mLoginActivity.getmTvToLogin().setVisibility(View.GONE);
                mLoginActivity.getLoadingButton().setText("登录");
                mLoginActivity.getmTvTitleLogin().setText("欢迎登录");
            }
        });

        mLoginActivity.getmTvResgister().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换为注册模式
                mLoginActivity.getmTvResgister().setVisibility(View.GONE);
                mLoginActivity.getmTvToLogin().setVisibility(View.VISIBLE);
                mLoginActivity.getLoadingButton().setText("注册");
                mLoginActivity.getmTvTitleLogin().setText("用户注册");
            }
        });

        mLoginActivity.getmTvLoginBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginActivity.finish();
            }
        });

    }

}
