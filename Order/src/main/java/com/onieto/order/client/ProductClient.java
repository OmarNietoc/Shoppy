package com.onieto.order.client;

import com.onieto.order.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog", url = "${client.catalog.url:http://localhost:8081/api/products}")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponseDto getProductById(@PathVariable("id") String id);
}
