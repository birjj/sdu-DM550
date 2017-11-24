package dm550.tictactoe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class ScrollView2D extends ScrollView {

    private HorizontalScrollView innerScrollView;

    public ScrollView2D(Context context) {
        super(context);
        addInnerScrollView(context);
    }

    public ScrollView2D(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 1) {
            View subView = getChildAt(0);
            removeViewAt(0);
            addInnerScrollView(getContext());
            this.innerScrollView.addView(subView);
        } else {
            addInnerScrollView(getContext());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = super.onTouchEvent(event);
        handled |= this.innerScrollView.dispatchTouchEvent(event);
        return handled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        super.onInterceptTouchEvent(event);
        return true;
    }

    public void setContent(View content) {
        if (content != null) {
            this.innerScrollView.addView(content);
        }
    }

    private void addInnerScrollView(Context context) {
        this.innerScrollView = new HorizontalScrollView(context);
        this.innerScrollView.setHorizontalScrollBarEnabled(false);
        addView(this.innerScrollView);
    }

}