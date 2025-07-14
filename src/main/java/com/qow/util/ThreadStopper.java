package com.qow.util;

/**
 * スレッドを一時的に停止する
 *
 * @version 2025/07/14
 * @since 1.0.0
 */
public class ThreadStopper {
    private final static Object object = new Object();
    private boolean stopping;
    private boolean ready;

    /**
     * 初期化
     */
    public ThreadStopper() {
        init();
    }

    /**
     * 初期化
     */
    public void init() {
        stopping = false;
        ready = false;
    }

    /**
     * 停止を強制的に解除する<br>
     * {@link ThreadStopper#setReady(boolean)}が内部で呼ばれている<br>
     * もとより停止していなかった場合は何もしない
     */
    public void start() {
        synchronized (object) {
            ready = true;
            if (stopping) object.notify();
        }
    }

    /**
     * 停止する<br>
     * {@link ThreadStopper#setReady(boolean)}にて{@link ThreadStopper#ready}がすでにtrueだった場合は停止しない
     */
    public void stop() {
        try {
            synchronized (object) {
                stopping = true;
                while (!ready) {
                    object.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopping = false;
        }
    }

    /**
     * 停止を解除する準備が整っているかを設定する
     *
     * @param ready 停止が解除されてもいい場合はtrue
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
