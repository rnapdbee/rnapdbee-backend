package com.example.rnapdbeeBackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping(path = "/")
  public String home() {
    return "Rnapdbee backend home";
  }
}
