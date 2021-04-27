package me.toy.atdd.subwayadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

public class PageController {

    @GetMapping(value = {"/", "/stations", "/lines", "/sections"}, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }
}
