package beiing.com.standardcompuse.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import beiing.com.standardcompuse.Constants;

/**
 * Date:2015/10/26
 * Author:Beiing
 * Email:15764230067@163.com
 **/

/**
 * 数据加载服务
 */
public class DataFetchService extends Service implements Runnable{

    private Queue<String> urls;

    /**
     * 用于下载 ： 操作urls队列的内容
     */
    private Thread downloadThread;

    /**
     * 控制线程，是否继续执行
     */
    private boolean running;
    /**
     * 在对象的存活期间，只执行一次；用于初始化数据与对象；通常，都是启动线程、数据；
     * 在主线程中执行
     */
    @Override
    public void onCreate() {
        super.onCreate();
        urls = new LinkedList<>();
        Log.d("--" , "onCreate");

        //启动线程
        downloadThread = new Thread(this);
        downloadThread.start();
    }

    /**
     *  第一个参数用于传递参数；这个方法在主线程执行，同时Intent需要传递多一些参数，不要只传一个extra
     *
     *  intent可以传递一些int参数，用于区分内部不同操作
     * @param intent
     * @param flags
     * @param startId
     * @return int  代表进程意外终止的情况下，服务应该如何恢复
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("--" , "onStartCommand");
        //检查Intent
        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
            switch (action){
                case 0: // 0 ： 代表添加网址，开始下载
                    processDownload(intent);
                    break;

                case 1: // 1 :  代表获取下载进度
                    getProgress(intent);
                    break;

                case 2: // 2 :  删除下载任务
                    removeDownloadRequest(intent);
                    break;
            }

        }
        //返回值，如果调用super，那么启动的服务是粘性的，进程终止，服务重新启动
        //但是 Intent 为null
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 处理地址下载的操作
     * @param intent
     */
    private void processDownload(Intent intent){
        if(intent != null){
            String url = intent.getStringExtra("url");
            if(url != null){
                urls.offer(url);
            }
        }
    }

    /**
     * 获取下载的进度 ： 利用 广播获取
     * @param intent
     */
    private void getProgress(Intent intent){
        //因为要获取进度，那么服务在没有绑定的情况下，需要使用广播来完成进度更新
        sendUpdateBroadcast();
    }

    private void sendUpdateBroadcast() {
        Intent bcIntent = new Intent(Constants.ACTION_PROGRESS_UPDATE);
        String[] strs = new String[urls.size()];
        urls.toArray(strs);
        bcIntent.putExtra("urls", strs);

        sendBroadcast(bcIntent);
    }

    /**
     * 删除下载任务
     * @param intent
     */
    private void removeDownloadRequest(Intent intent){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //终止线程
        running = false;
        downloadThread.interrupt();

        urls.clear();
        urls = null;
        Log.d("--" , "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if( ! urls.isEmpty()){
            // 删除第一个
            urls.poll();
        }
        return null;
    }


    @Override
    public void run() {
        running = true;
        try{
            while(running){
                String url = urls.poll();
                if(url != null){
                    // TODO 模拟网络下载
                    Thread.sleep(5000);

                    // TODO 更新状态
                    sendUpdateBroadcast();
                }

                Thread.sleep(500);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}









