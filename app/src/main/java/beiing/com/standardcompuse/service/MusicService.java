package beiing.com.standardcompuse.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static final int STATE_PREPARING = 2;// 准备中状态
    private static final int STATE_STOP = 5;

    public MusicService() {
    }


    /**
     * 静态的确保唯一实例
     */
    private static MediaPlayer player;
    // 是否继续播放下一首
    private boolean configAutoNext;

    private Vector<String> playList;// 播放列表,线程同步时使用vector
    private int currentPosition = 0;

    public static final int STATE_CREATED = 0;// 服务创建成功
    public static final int STATE_DESTORY = 1;// 服务创建成功
    public static final int STATE_PLAYING = 3;// 服务创建成功
    public static final int STATE_PAUSED = 4;// 服务创建成功


    /**
     * 代表播放器的状态
     */
    private int playerState;

    @Override
    public void onCreate() {
        super.onCreate();

        player = new MediaPlayer();
        playList = new Vector<>();
        // MediaPlayer播放网络资源：onPreparedListener-- 当PrepareAsync准备资源完成时，进行回调
        // 适用于网络的资源
        /**
         * 当player的准备状态改变，回调借口
         */

        player.setOnPreparedListener(this);
        // 当一首歌播放完成时，
        player.setOnCompletionListener(this);
        playerState = STATE_CREATED;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        playerState = STATE_DESTORY;
        try {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();

        } catch (Exception e) {
            e.printStackTrace();
            // 因为player的操作可能出现状态错误，所以捕获异常
        }
        player = null;
        playList.clear();
        playList = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            int action = intent.getIntExtra("action", -1);
            // http://10.10.60.18:8080/nobody.mp3
            switch (action) {
                case 0:// 播放指定网址的声音,通常不需要继续播放
                    playMusic(intent);
                    break;
                case 1:
                    pasueMusic(intent);
                    break;
                case 2:
                    continueMusic(intent);
                    break;
                case 3:// 重新设置播放列表，可以包含自动播放的功能

                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void pasueMusic(Intent intent) {
        playerState = STATE_PAUSED;
        try {
            if (playerState == STATE_PLAYING && player.isPlaying()) {
                player.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void continueMusic(Intent i) {
        if (!player.isPlaying() && playerState == STATE_PAUSED) {
            player.start();// 这个方法是开始或者恢复播放
            playerState = STATE_PLAYING;

        } else {
            // MediaPlayer只有播放中的状态，程序进行处理的时候，
            // 需要检测一下player的各种状态；使用一个成员变量来进行判断设置


        }
    }

    /**
     * 播放intent中的网址
     *
     * @param intent
     */
    private void playMusic(Intent intent) {
        if (intent != null) {
            String url = intent.getStringExtra("url");
            playMusic(url);
        }

    }

    private void playMusic(String url) {
        if (url != null) {
            resetPlayer();

            try {
                player.setDataSource(url);
                player.prepareAsync();
                playerState = STATE_PREPARING;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 如果正在播放，那么停止，并且重置mediaPlayer
     * 准备下一次的播放
     */
    private void resetPlayer() {
        try {
            if (player.isPlaying()) {
                player.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.reset();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        playerState = STATE_PLAYING;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playerState  = STATE_STOP;
        if (playList != null && configAutoNext ){
            int size = playList.size();
            if (currentPosition + 1 >= size){
                // TODO 最后一个，不再播放

            }else {
                // TODO 下一首
                String url = playList.get(currentPosition + 1);
                playMusic(url);
                currentPosition ++;
            }

        }
    }
}
