package beiing.com.standardcompuse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import beiing.com.standardcompuse.service.MusicService;

public class MainActivity extends Activity {

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                String[] urls = intent.getStringArrayExtra("urls");
                // TODO 更新UI
                if(urls != null){
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < urls.length; i++) {
                        sb.append(urls[i]).append('\n');
                    }

                    txtInfo.setText(sb.toString());
                }
            }
        }
    };

    private TextView txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtInfo = (TextView) findViewById(R.id.progress_info);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 动态注册接收者
        IntentFilter filter = new IntentFilter(Constants.ACTION_PROGRESS_UPDATE);
        registerReceiver(progressReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(progressReceiver);
    }

    public void btnAddRequest(View view) {
        // 添加请求
        Intent service = new Intent(Constants.ACTION_DATA_FETCH);
        service.putExtra("action", 0); // 0 代表添加请求

        service.putExtra("url", "http://www.baidu.com");
        startService(service);
    }

    public void btnGetProgress(View view) {
        // 获取进度
        Intent service = new Intent(Constants.ACTION_DATA_FETCH);
        service.putExtra("action", 1); // 0 代表添加请求
        startService(service);
    }

    public void btnDeleteRequest(View view) {
    }

    /**
     * 启动服务，播放音乐
     * @param view
     */
    public void btnPlayMusic(View view) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action",0);
        intent.putExtra("url","http://10.10.60.18:8080/nobody.mp3");
        startService(intent);
    }

    /**
     * 暂停播放
     * @param view
     */
    public void btnPauseMusic(View view) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action",1);
        startService(intent);

    }
}
