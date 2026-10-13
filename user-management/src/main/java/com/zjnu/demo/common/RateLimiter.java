package com.zjnu.demo.common;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 滑动窗口限流器（实验五：每用户每分钟最多 5 次）
 */
public final class RateLimiter {

    private final int limit;
    private final long windowMillis;
    private final ConcurrentMap<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    public RateLimiter(int limit, Duration window) {
        this.limit = limit;
        this.windowMillis = window.toMillis();
    }

    /** 尝试为 key 获取一次配额；窗口内超过 limit 次返回 false。 */
    public synchronized boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> q = hits.computeIfAbsent(key, k -> new ArrayDeque<>());
        while (!q.isEmpty() && now - q.peekFirst() >= windowMillis) {
            q.pollFirst();                       // 滑出过期时间戳
        }
        if (q.size() >= limit) {
            return false;
        }
        q.addLast(now);
        return true;
    }
}
