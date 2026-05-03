package com.logistics.common.feign;

import com.logistics.common.dto.LogisticsUpdateDTO;
import com.logistics.common.entity.Logistics;
import com.logistics.common.entity.LogisticsTrack;
import com.logistics.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "logistics-service", path = "/api/logistics")
public interface LogisticsFeignClient {

    @GetMapping("/order/{orderId}")
    Result<Logistics> getByOrderId(@PathVariable("orderId") Long orderId);

    @GetMapping("/track/{orderId}")
    Result<List<LogisticsTrack>> getTracksByOrderId(@PathVariable("orderId") Long orderId);

    @PostMapping("/update")
    Result<Void> updateLogistics(@RequestBody LogisticsUpdateDTO dto);

    @PostMapping("/init/{orderId}")
    Result<Logistics> initLogistics(@PathVariable("orderId") Long orderId);
}
