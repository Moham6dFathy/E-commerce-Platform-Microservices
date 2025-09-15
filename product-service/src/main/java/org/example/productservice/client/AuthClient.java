package org.example.productservice.client;

import org.example.productservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "auth", url = "http://localhost:8083")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/auth/user")
    UserResponse getUserByUsername(@RequestParam("username") String username);

}
