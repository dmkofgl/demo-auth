package com.example.auth.web.feign;

import com.example.common.api.contract.UserApi;
import com.example.common.api.model.user.UserPrincipal;
import com.example.common.api.model.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "${client.user.name}", url = "${client.user.url}")
public interface UserClient extends UserApi {

    @GetMapping(value=ID_TEMPLATE_PATH, headers = "Authorization")
    ResponseEntity<UserResponse> getUserWithToken(@PathVariable(ID_TEMPLATE_NAME) Long id, @RequestHeader("Authorization") String bearerToken);

    @GetMapping(value = EMAIL_TEMPLATE_PATH, headers = "Authorization")
    ResponseEntity<UserPrincipal> getUserByEmailWithToken(@PathVariable(EMAIL_TEMPLATE_NAME) String email, @RequestHeader("Authorization") String bearerToken);
}
