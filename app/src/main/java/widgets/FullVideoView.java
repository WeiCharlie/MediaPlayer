package widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.VideoView;

/**
 * Created by:
 * Author : Charlie Wei
 * Date : 2015/10/26.
 * Email : charlie_net@163.com
 */
public class FullVideoView extends VideoView {
    public FullVideoView(Context context) {
        super(context);
    }

    public FullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写VideoView的尺寸测量，使自身充满容器，
     * 宽高都是容器的size
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(wSize,hSize);
    }
}
