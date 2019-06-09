package primoz.com.alarmcontinue.libraries.filepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import primoz.com.alarmcontinue.R;

public class MaxHeightLayout extends FrameLayout {

    private static final float DEFAULT_MAX_RATIO = 0.6f;
    private static final float DEFAULT_MAX_HEIGHT = 0f;

    private float mMaxRatio = DEFAULT_MAX_RATIO;
    private float mMaxHeight = DEFAULT_MAX_HEIGHT;

    public MaxHeightLayout(Context context) {
        super(context);
        init();
    }

    public MaxHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    public MaxHeightLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MaxHeightLayout);

        final int count = a.getIndexCount();
        for (int i = 0; i < count; ++i) {
            int attr = a.getIndex(i);
            if(attr == R.styleable.MaxHeightLayout_mhl_HeightRatio){
                mMaxRatio = a.getFloat(attr, DEFAULT_MAX_RATIO);
            }else if(attr == R.styleable.MaxHeightLayout_mhl_HeightDimen){
                mMaxHeight = a.getDimension(attr, DEFAULT_MAX_HEIGHT);
            }
        }
        a.recycle();
    }

    private void init(){
        if (mMaxHeight <= 0) {
            mMaxHeight = mMaxRatio * (float) getScreenHeight(getContext());
        } else {
            mMaxHeight = Math.min(mMaxHeight, mMaxRatio * (float) getScreenHeight(getContext()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = heightSize <= mMaxHeight ? heightSize
                    : (int) mMaxHeight;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightSize <= mMaxHeight ? heightSize
                    : (int) mMaxHeight;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = heightSize <= mMaxHeight ? heightSize
                    : (int) mMaxHeight;
        }
        int maxHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightSize,
                heightMode);
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
    }

    private int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }
}
