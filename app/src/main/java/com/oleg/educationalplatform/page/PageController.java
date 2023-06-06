package com.oleg.educationalplatform.page;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class PageController {
    @GetMapping("/chat")
    public ResponseEntity<Object> chat(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws URISyntaxException {
        URI chat = new URI("http://localhost:8081/chat");
        System.out.println(token);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("token", token);
        httpHeaders.setLocation(chat);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login/login.html";
    }

    @GetMapping("/main")
    public String course() {
        return "pages/main/main.html";
    }
}
