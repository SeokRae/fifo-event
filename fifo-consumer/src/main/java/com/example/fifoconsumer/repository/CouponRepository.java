package com.example.fifoconsumer.repository;


import com.example.fifoconsumer.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}