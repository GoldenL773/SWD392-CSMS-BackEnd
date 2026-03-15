package com.csms.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @PostMapping("/api/employees")
    void createEmployee(@RequestBody EmployeeRegistrationRequest request);

    @Data
    @Builder
    class EmployeeRegistrationRequest {
        private Long userId;
        private String firstName;
        private String lastName;
        private String position;
        private LocalDate hireDate;
        private String phone;
        private String address;
    }
}
