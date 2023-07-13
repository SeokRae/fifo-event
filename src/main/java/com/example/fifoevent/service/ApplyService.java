package com.example.fifoevent.service;

import com.example.fifoevent.domain.Coupon;
import com.example.fifoevent.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplyService {
    private final CouponRepository couponRepository;

    public void apply(Long userId) {
        /* 쿠폰 갯수 확인 */
        long count = couponRepository.count();

        /* 쿠폰 100개 초과 시 종료 */
        if(count > 100) {
            return;
        }

        couponRepository.save(new Coupon(userId));
    }
}
