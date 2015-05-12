package org.nustaq.kontraktor.util;

import org.nustaq.kontraktor.IPromise;
import org.nustaq.kontraktor.Promise;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ruedi on 27.07.14.
 *
 * Wraps a future and triggers it after having received N results (counter is counted down).
 * Note that only the last result/error is actually transmitteed to the wrapped future.
 * An implementation collecting intermediate results in a concurrentlist which then is
 * used as a result could be implemented if needed.
 * Usually used for pure signaling (so result is "void")
 */
public class PromiseLatch<T> {

    IPromise<T> wrapped;
    AtomicInteger count;

    public PromiseLatch(IPromise<T> wrapped) {
        this.wrapped = wrapped;
        count = new AtomicInteger(1);
    }

    public PromiseLatch(int counter, IPromise<T> wrapped) {
        this( wrapped,counter);
    }

    public PromiseLatch(int counter) {
        this( new Promise<>(), counter);
    }

    public PromiseLatch(IPromise<T> wrapped, int counter) {
        this.wrapped = wrapped;
        count = new AtomicInteger(counter);
    }

    public void countDown() {
        countDown(null,null);
    }

    public void countDown(T result, Object error) {
        int i = count.decrementAndGet();
        if ( i == 0 ) {
            wrapped.complete(result, error);
        } else if ( i < 0 ) {
            throw new RuntimeException("latch already triggered !");
        }
    }

    public void countUp(int amount) {
        count.incrementAndGet();
    }

    /**
     * debug, cannot be used to implement reliable logic in a concurrent environment
     * @return
     */
    public int getCount() {
        return count.get();
    }

    public IPromise<T> getPromise() {
        return wrapped;
    }
}