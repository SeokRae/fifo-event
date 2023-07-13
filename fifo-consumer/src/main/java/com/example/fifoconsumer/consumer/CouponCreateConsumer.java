package com.example.fifoconsumer.consumer;

import com.example.fifoconsumer.domain.Coupon;
import com.example.fifoconsumer.domain.FailedEvent;
import com.example.fifoconsumer.repository.CouponRepository;
import com.example.fifoconsumer.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponCreateConsumer {

    private final CouponRepository couponRepository;
    private final FailedEventRepository failedEventRepository;

    @KafkaListener(topics = "coupon_create", groupId = "group_1")
    public void listener(Long userId) {
        log.info("Receive coupon create message: {}", userId);
        try {

            couponRepository.save(new Coupon(userId));
        } catch (Exception e) {
            log.error("Failed to save coupon: {}", userId, e);
            failedEventRepository.save(new FailedEvent(userId));
        }
    }
}
