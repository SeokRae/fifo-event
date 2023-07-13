package com.example.fifoevent.service;

import com.example.fifoevent.repository.CouponCountRepository;
import com.example.fifoevent.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ApplyServiceSingleRedisTest {


    @Autowired
    private ApplyService applyService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponCountRepository couponCountRepository;

    @BeforeEach
    void setUp() {
        couponCountRepository.flush();
    }

    @DisplayName("한명이 여러번 쿠폰 응모 시 레디스로 단일 쿠폰 발급 처리 확인 테스트")
    @Test
    void 여러번_응모() throws InterruptedException {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    applyService.applyCouponPerUser(1L);
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

        Thread.sleep(2000); // 준 실시간 처리 확인을 위해 5초 대기

        long count = couponRepository.count();

        assertThat(count).isEqualTo(1);
    }
}