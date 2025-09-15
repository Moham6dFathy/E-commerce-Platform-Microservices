package org.example.orderservice.client;

import org.example.orderservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "auth", url = "http://localhost:8083")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/auth/user")
    UserResponse getUserByUsername(@RequestParam("username") String username);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/auth/user")
    UserResponse getUserById(@RequestParam("userId") String userId);
}
