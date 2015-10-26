package beiing.com.standardcompuse;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlayerAvtivity extends AppCompatActivity implements Runnable, SeekBar.OnSeekBarChangeListener, View.OnClickListener, GestureDetector.OnGestureListener {

    private static final int ISSHOW = 123;
    private VideoView videoView;
    private SeekBar seekBar;
    private Message message;
    private int currentPosition;
    private ImageView imgViewFF, imgViewFB, imgViewPause;
    private TextView currentTime, durationTime;
    private LinearLayout bottomLinearLayout;
    private RelativeLayout player_title_container;


    boolean isPause = false;

    private GestureDetector gestureDetector;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_avtivity);

        initWidgets();

        initClickListener();

        // http://10.10.60.18:8080/video_test.3gp
        // 1 设置视频的地址，可以是res/raw/内部文件，也可以是本地手机文件，还可以是网络地址
//        videoView.setMediaController(new MediaController(this));
//        videoView.setVideoURI(Uri.parse("http://10.10.60.18:8080/video_test.3gp"));
        String pageName = getPackageName();
        Log.d("Tag", pageName);
        videoView.setVideoURI(Uri.parse("android.resource://" + pageName + "/raw/adele_hello"));
        thread = new Thread(this);
        thread.start();

        videoView.start();
        simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);


    }


    private void initWidgets() {
        videoView = (VideoView) findViewById(R.id.video_view);
        seekBar = (SeekBar) findViewById(R.id.mc_seekBar);
        imgViewFF = (ImageView) findViewById(R.id.imgView_FF);
        imgViewPause = (ImageView) findViewById(R.id.imgView_Pause);
        imgViewFB = (ImageView) findViewById(R.id.imgView_FB);
        bottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_info_linearLayout);
        player_title_container = (RelativeLayout) findViewById(R.id.player_title_container);
        currentTime = (TextView) findViewById(R.id.current_time);
        durationTime = (TextView) findViewById(R.id.duration_time);
    }

    private void initClickListener() {
        imgViewFF.setOnClickListener(this);
        imgViewFB.setOnClickListener(this);
        imgViewPause.setOnClickListener(this);
        videoView.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_UP:

                if (bottomLinearLayout.isShown() && player_title_container.isShown()) {


                    hideView(View.INVISIBLE);
                } else {
                    bottomLinearLayout.setVisibility(View.VISIBLE);
                    player_title_container.setVisibility(View.VISIBLE);
                }
                if (bottomLinearLayout.isShown() && player_title_container.isShown()) {
                    Message message1 = handler.obtainMessage(ISSHOW);
                    handler.sendMessageDelayed(message1, 3000);
                }
                break;


        }
        return true;
    }

    private List<Long> times;

    private void hideView(int invisible) {
        bottomLinearLayout.setVisibility(invisible);
        player_title_container.setVisibility(invisible);
    }

    private SimpleDateFormat simpleDateFormat;
    private Thread thread;
    private boolean running;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 998) {

                int durationTime = msg.arg1;
                seekBar.setMax(durationTime);
                int currentTime = msg.arg2;
                seekBar.setProgress(currentTime);


            } else if (what == ISSHOW) {
                hideView(View.INVISIBLE);

            }
        }
    };

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                if (videoView != null && videoView.isPlaying()) {
                    // handler

                    currentPosition = videoView.getCurrentPosition();

                    duration = videoView.getDuration();

                    message = handler.obtainMessage(998);
                    message.arg1 = duration;
                    message.arg2 = currentPosition;
                    handler.sendMessage(message);
                }
                thread.sleep(1000);


            } catch (InterruptedException e) {

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        running = false;
        thread.interrupt();

        videoView.stopPlayback();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 使用seekBar拖拽处理，注意第三个参数
        if (fromUser) {
            // 用户操作才处理
            videoView.seekTo(progress);
        }

        currentTime.setText(simpleDateFormat.format(new Date(progress)));
        durationTime.setText(simpleDateFormat.format(new Date(duration)));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imgView_FB:
                if (videoView.canSeekBackward()) {
                    currentPosition = currentPosition - 10000;
                    videoView.seekTo(currentPosition);
                }
                break;
            case R.id.imgView_Pause:
                isPause = !isPause;
                if (videoView.canPause() && isPause) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
                break;
            case R.id.imgView_FF:
                // 是否可以快进
                if (videoView.canSeekForward()) {
                    currentPosition = currentPosition + 10000;
                    videoView.seekTo(currentPosition);
                }

                break;

            case R.id.video_view:

                times.add(SystemClock.uptimeMillis());
                if (times.size() == 2) {
                    if (times.get(times.size() - 1) - times.get(0) < 500) {
                        times.clear();
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        } else {
                            videoView.start();
                        }


                    } else {
                        times.remove(0);
                    }
                }

                break;


        }
    }


    /**
     * 以下是实现了GestureListener的方法
     *
     * @param e
     * @return
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
