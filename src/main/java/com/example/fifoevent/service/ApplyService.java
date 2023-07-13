package com.example.fifoevent.service;

import com.example.fifoevent.domain.Coupon;
import com.example.fifoevent.producer.CouponCreateProducer;
import com.example.fifoevent.repository.AppliedUserRepository;
import com.example.fifoevent.repository.CouponCountRepository;
import com.example.fifoevent.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyService {
    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;
    private final AppliedUserRepository appliedUserRepository;

    public void apply(Long userId) {
        /* 쿠폰 갯수 확인 */
        long count = couponRepository.count();

        /* 쿠폰 100개 초과 시 종료 */
        if(count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }

    public void applyRedis(Long userId) {
        /* 쿠폰 갯수 확인 */
        long count = couponCountRepository.increaseCouponCount();

        /* 쿠폰 100개 초과 시 종료 */
        if (count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }

    public void applyKafka(Long userId) {
        /* 레디스 쿠폰 갯수 확인 */
        long count = couponCountRepository.increaseCouponCount();

        /* 쿠폰 100개 초과 시 종료 */
        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }


    public void applyCouponPerUser(Long userId) {
        /* 한명당 하나의 쿠폰 요청 처리 */
        Long add = appliedUserRepository.add(userId);

        if (add != 1) {
            return;
        }

        /* 레디스 쿠폰 갯수 확인 */
        long count = couponCountRepository.increaseCouponCount();

        /* 쿠폰 100개 초과 시 종료 */
        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }

}
