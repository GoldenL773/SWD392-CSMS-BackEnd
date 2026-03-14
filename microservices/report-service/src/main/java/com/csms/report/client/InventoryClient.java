package com.csms.report.client;

import com.csms.report.dto.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    
    @GetMapping("/api/transactions")
    List<TransactionResponse> getAllTransactions();
}
