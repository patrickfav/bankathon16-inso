package at.favre.app.bankathon16.view;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AppBarLayoutWidget extends AppBarLayout implements AppBarLayout.OnOffsetChangedListener {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDEFINED, COLLAPSED, EXPANDED, SEMI_COLLAPSED, SEMI_EXPANDED})
    public @interface AppBarState {
    }

    public static final int UNDEFINED = -1;
    public static final int COLLAPSED = 0;
    public static final int EXPANDED = 1;
    public static final int SEMI_COLLAPSED = 2;
    public static final int SEMI_EXPANDED = 3;

    private int currentState = UNDEFINED;

    private OnStateChangedListener onStateChangeListener;

    public interface OnStateChangedListener {
        void onStateChange(@AppBarState int state, double scrolledInPercent);
    }

    public AppBarLayoutWidget(Context context) {
        super(context);
    }

    public AppBarLayoutWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!(getLayoutParams() instanceof CoordinatorLayout.LayoutParams) || !(getParent() instanceof CoordinatorLayout)) {
            throw new IllegalStateException("AppBarLayoutWidget must be a direct child of CoordinatorLayout.");
        }
        addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
        final double scrolledInPercent = 1.0 - ((double) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange());

        if (scrolledInPercent >= 1) {
            setCurrentState(EXPANDED, scrolledInPercent);
        } else if (scrolledInPercent <= 0) {
            setCurrentState(COLLAPSED, scrolledInPercent);
        } else if (scrolledInPercent >= 0.325) {
            setCurrentState(SEMI_EXPANDED, scrolledInPercent);
        } else {
            setCurrentState(SEMI_COLLAPSED, scrolledInPercent);
        }
    }

    private void setCurrentState(@AppBarState int state, double scrolledInPercent) {
        if (currentState != state) {
            currentState = state;
            if (onStateChangeListener != null) {
                onStateChangeListener.onStateChange(currentState, scrolledInPercent);
            }
        }
    }

    public void setOnStateChangeListener(OnStateChangedListener listener) {
        if (listener != null && listener != this.onStateChangeListener) {
            this.onStateChangeListener = listener;
        }
    }
}