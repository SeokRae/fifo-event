package com.example.fifoevent.service;

import com.example.fifoevent.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ApplyServiceRedisTest {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;


    @DisplayName("쿠폰 여러번 응모 동시성 이슈 확인")
    @Test
    void 여러번_응모() {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            long userId = i;

            executorService.submit(() -> {
                try {
                    applyService.applyRedis(userId);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long count = couponRepository.count();

        assertThat(count).isEqualTo(100);
    }
}