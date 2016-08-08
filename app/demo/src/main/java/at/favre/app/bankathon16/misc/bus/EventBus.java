package at.favre.app.bankathon16.misc.bus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple event bus based on {@link LocalBroadcastManager} broadcasting.
 * This is basically just a wrapper for using local broadcast with some added type safety and
 * less verbose and easier syntax, handling some of the boiler plate code. It works internally  with
 * a single {@link BroadcastReceiver} that informs all event handler of updates of the specific topic.
 */
public final class EventBus {
    private static final String TAG = EventBus.class.getSimpleName();

    private static String LOCAL_BROADCAST_EVENT = EventBus.class.getName() + ".LOCAL_BROADCAST_EVENT";
    private static String LOCAL_BROADCAST_EVENT_TOPIC = LOCAL_BROADCAST_EVENT + "_TOPIC";
    private static String LOCAL_BROADCAST_EVENT_RESULT = LOCAL_BROADCAST_EVENT + "_RESULT";
    public static String LOCAL_BROADCAST_EVENT_PERSISTENCE = LOCAL_BROADCAST_EVENT + "_PERSISTENCE";

    private static EventBus instance;

    private Context ctx;
    private IntentFilter intentFilter = new IntentFilter(LOCAL_BROADCAST_EVENT);
    private Map<String, List<EventHandler>> eventHandlerMap = new ConcurrentHashMap<>();
    private Map<String, List<Intent>> persistentEvents = new HashMap<>();
    private boolean receiverStarted = false;
    private Handler osHandler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            synchronized (EventBus.this) {
                Log.v(TAG, "Main receiver got broadcast");

                final String topic = intent.getExtras().getString(LOCAL_BROADCAST_EVENT_TOPIC);
                final @Result.Type int resultType = intent.getExtras().getInt(LOCAL_BROADCAST_EVENT_RESULT);

                if (eventHandlerMap.containsKey(topic)) {
                    Iterator<EventHandler> iterator = eventHandlerMap.get(topic).iterator();
                    while (iterator.hasNext()) {
                        try {
                            final EventHandler handler = iterator.next();
                            Log.v(TAG, "Notify handler " + handler + " on topic " + topic);

                            notifyHandler(handler, intent);

                            if (handler instanceof OneTimeEventHandler) {
                                unregisterHandler(handler);
                            }
                        } catch (Exception e) {
                            iterator = null;
                            //check if this is responsible for this kind of errors: A/libc: Fatal signal 11 (SIGSEGV), code 1, fault addr 0x550004 in tid 21778 (JDWP) --pf
                            Log.wtf(TAG, "exception while notifying handler on topic " + topic + " - remove from listeners. This usually happens if an event is triggered in an handler callback or similar.", e);
                            unregisterAllFromTopic(topic);
                            break;
                        }
                    }
                } else {
                    if (intent.getBooleanExtra(LOCAL_BROADCAST_EVENT_PERSISTENCE, false)) {
                        Log.d(TAG, "could not find listener for topic " + topic + " but is persistent event, so store");
                        if (!persistentEvents.containsKey(topic)) {
                            persistentEvents.put(topic, new ArrayList<Intent>());
                        }
                        persistentEvents.get(topic).add(intent);
                    } else {
                        Log.w(TAG, "got event for topic " + topic + " but it seems there is no registered listener");
                    }
                }

                checkIfMainReceiverCanBeStopped();
            }
        }
    };

    private void notifyHandler(final EventHandler handler, final Intent intent) {
        //delay callback after this method is done to avoid concurrent exceptions
        osHandler.post(new Runnable() {
            @Override
            public void run() {
                //noinspection ResourceType
                handler.onEventReceive(intent.getExtras().getString(LOCAL_BROADCAST_EVENT_TOPIC), intent.getExtras().getInt(LOCAL_BROADCAST_EVENT_RESULT), intent);
            }
        });
    }


    private EventBus() {
        throw new UnsupportedOperationException();
    }

    private EventBus(Context ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("provided context must not be null");
        }
        this.ctx = ctx;
    }

    public static EventBus get(Context ctx) {
        if (ctx == null) {
            Log.w(TAG, "trying to get instance with null context");
        }
        if (instance == null) {
            instance = new EventBus(ctx);
        }
        return instance;
    }

    /**
     * Same as {@link EventBus#sendEvent(String, int, Bundle)} with param {@link Result#UNDEFINED}
     *
     * @param topic
     * @param bundle
     */
    public void sendEvent(String topic, @Nullable Bundle bundle) {
        sendEvent(topic, Result.UNDEFINED, bundle);
    }


    /**
     * Same as {@link EventBus#sendEvent(String, int, Bundle)} with param {@link Result#UNDEFINED} and {@link Bundle#EMPTY}
     *
     * @param topic
     */
    public void sendEvent(String topic) {
        sendEvent(topic, Result.UNDEFINED, Bundle.EMPTY);
    }

    /**
     * Informs all {@link EventBus.EventHandler} listening to the given topic
     * of the result.
     *
     * @param topic      you may want to use
     * @param resultType see {@link Result.Type}
     * @param bundle     add additional data e.g. properties defined by
     */
    public void sendEvent(String topic, @Result.Type int resultType, @Nullable Bundle bundle) {
        if (receiverStarted) {
            Log.d(TAG, "Send broadcast for topic " + topic);
            LocalBroadcastManager.getInstance(ctx).sendBroadcast(createIntent(topic, resultType, bundle));
        } else {
            Log.v(TAG, "No receiver found, ignore message");
        }
    }

    private Intent createIntent(String topic, @Result.Type int resultType, @Nullable Bundle bundle) {
        Intent i = new Intent(LOCAL_BROADCAST_EVENT);
        i.putExtra(LOCAL_BROADCAST_EVENT_TOPIC, topic);
        i.putExtra(LOCAL_BROADCAST_EVENT_RESULT, resultType);
        if (bundle != null) {
            i.putExtras(bundle);
        }
        return i;
    }

    /**
     * Register an {@link EventBus.EventHandler} for one or multiple topics. The
     * event handler will be informed each time a {@link #sendEvent(String, int, Bundle)} with the
     * same topic will be called.
     *
     * @param eventHandler
     * @param topicArray   one or more topics
     * @throws IllegalArgumentException if no topics are passed
     */
    public synchronized void registerHandler(EventHandler eventHandler, @NonNull String... topicArray) {
        if (topicArray.length == 0) {
            throw new IllegalArgumentException("there must be at least one topic");
        }

        startMainReceiverIfNeeded();
        for (String topic : topicArray) {
            Log.d(TAG, "Register handler " + eventHandler + " for topic " + topic);

            if (!eventHandlerMap.containsKey(topic)) {
                eventHandlerMap.put(topic, new ArrayList<EventHandler>(1));
            }

            if (eventHandlerMap.get(topic).contains(eventHandler)) {
                Log.w(TAG, "handler " + eventHandler + "already registered for this topic " + topic + " - ignore");
            } else {
                eventHandlerMap.get(topic).add(eventHandler);
            }
            checkIfPersistenceEventsAreWaiting(topic, eventHandler);
        }
    }

    private void checkIfPersistenceEventsAreWaiting(String topic, EventHandler handler) {
        if (persistentEvents.containsKey(topic)) {
            Log.d(TAG, "found persistent events: " + persistentEvents.get(topic).size());
            for (Intent intent : persistentEvents.get(topic)) {
                notifyHandler(handler, intent);
            }
            persistentEvents.remove(topic);
        }
    }

    private synchronized void startMainReceiverIfNeeded() {
        if (!receiverStarted) {
            Log.v(TAG, "start main receiver");
            LocalBroadcastManager.getInstance(ctx).registerReceiver(receiver, intentFilter);
            receiverStarted = true;
        }
    }

    /**
     * Unregister the handler from all of its topics.
     *
     * @param eventHandler
     */
    public synchronized void unregisterHandler(EventHandler eventHandler) {
        Log.d(TAG, "Unregister handler " + eventHandler + " for all topics");

        for (Iterator<Map.Entry<String, List<EventHandler>>> entryIterator = eventHandlerMap.entrySet().iterator(); entryIterator.hasNext(); ) {
            Map.Entry<String, List<EventHandler>> entry = entryIterator.next();

            for (Iterator<EventHandler> iterator = entry.getValue().iterator(); iterator.hasNext(); ) {
                EventHandler handler = iterator.next();
                if (handler == eventHandler) {
                    iterator.remove();
                }
            }

            if (entry.getValue().isEmpty()) {
                entryIterator.remove();
            }
        }

        checkIfMainReceiverCanBeStopped();
    }

    public synchronized void unregisterAllFromTopic(String topic) {
        eventHandlerMap.remove(topic);
    }

    private void checkIfMainReceiverCanBeStopped() {
        if (eventHandlerMap.keySet().isEmpty()) {
            Log.v(TAG, "no more event handler");
            stopMainReceiver();
        }
    }

    private synchronized void stopMainReceiver() {
        if (receiverStarted) {
            Log.v(TAG, "stop main receiver");
            LocalBroadcastManager.getInstance(ctx).unregisterReceiver(receiver);
            receiverStarted = false;
        }
    }

    /* *********************************************************************************** HANDLER INTERFACES */

    /**
     * Event Handler interface for registering
     */
    public interface EventHandler {
        /**
         * This will be called on an broadcast event
         *
         * @param topic      the broadcast was sent for
         * @param resultType
         * @param rawIntent  additional data from the sender
         */
        void onEventReceive(String topic, @Result.Type int resultType, Intent rawIntent);
    }

    /**
     * Same as {@link EventBus.EventHandler} but will remove itself automatically, meaning
     * the handler will automatically be removed after the handler was notified.
     */
    public interface OneTimeEventHandler extends EventHandler {
    }

}
