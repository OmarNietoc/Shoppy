package com.onieto.order.config;

import com.onieto.order.model.Coupon;
import com.onieto.order.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CouponRepository couponRepository;


    @Override
    public void run(String... args) throws Exception {
// Crear 12 cupones
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Coupon coupon = Coupon.builder()
                    .code("DESC" + String.format("%02d", i))
                    .discountAmount(Integer.valueOf(5 + i)) // de 6.00 a 17.00
                    .active(true) // todos inician como activos
                    .build();
            coupons.add(coupon);
        }
        coupons = couponRepository.saveAll(coupons);

    }
}
