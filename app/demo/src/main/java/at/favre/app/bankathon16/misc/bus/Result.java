package at.favre.app.bankathon16.misc.bus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used in {@link EventBus} as result type
 */
public final class Result {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDEFINED, SUCCESS, FAILURE, CANCEL})
    public @interface Type {
    }

    public static final
    int UNDEFINED = -1;
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int CANCEL = 2;
}
