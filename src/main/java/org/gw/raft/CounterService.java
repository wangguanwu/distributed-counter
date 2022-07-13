package org.gw.raft;

/**
 * @author guanwu
 * @created on 2022-07-13 15:13:16
 * 支持查询和计数方法
 **/
public interface CounterService {
    /**
     * Get current value from counter
     *
     * Provide consistent reading if {@code readOnlySafe} is true.
     */
    void get(final boolean readOnlySafe, final CounterClosure closure);

    /**
     * Add delta to counter then get value
     */
    void incrementAndGet(final long delta, final CounterClosure closure);
}
