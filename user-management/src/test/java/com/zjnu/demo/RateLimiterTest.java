package com.zjnu.demo;

import com.zjnu.demo.common.RateLimiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 实验五：限流器单元测试（每用户 5 次/分钟）
 */
class RateLimiterTest {

    @Test
    @DisplayName("tryAcquire_blocks_after_limit：窗口内超过 5 次被限流")
    void tryAcquire_blocks_after_limit() {
        RateLimiter limiter = new RateLimiter(5, Duration.ofMinutes(1));
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire("zsan"), "第 " + (i + 1) + " 次应放行");
        }
        assertFalse(limiter.tryAcquire("zsan"), "第 6 次应被限流");
    }

    @Test
    @DisplayName("window_slides_after_expiry：窗口过期后恢复配额")
    void window_slides_after_expiry() throws InterruptedException {
        RateLimiter limiter = new RateLimiter(2, Duration.ofMillis(80));
        assertTrue(limiter.tryAcquire("a"));
        assertTrue(limiter.tryAcquire("a"));
        assertFalse(limiter.tryAcquire("a"));
        Thread.sleep(150);                        // 等待滑动窗口过期
        assertTrue(limiter.tryAcquire("a"), "窗口过期后应重新放行");
    }

    @Test
    @DisplayName("different_keys_are_independent：不同用户互不影响")
    void different_keys_are_independent() {
        RateLimiter limiter = new RateLimiter(1, Duration.ofMinutes(1));
        assertTrue(limiter.tryAcquire("zsan"));
        assertTrue(limiter.tryAcquire("admin"), "admin 的配额独立于 zsan");
        assertFalse(limiter.tryAcquire("zsan"));
    }
}
