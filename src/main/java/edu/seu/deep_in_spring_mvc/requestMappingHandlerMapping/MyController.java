package edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@Slf4j
public class MyController {

    @GetMapping("/test1")
    public ModelAndView test1() {
        log.debug("test1()");
        return null;
    }

    @PostMapping("/test2")
    public ModelAndView test2(@RequestParam("name")String name) {
        log.debug("test2({})",name);
        return null;
    }

    @PutMapping("/test3")
    public ModelAndView test3(@RequestBody String token) {
        log.debug("test3({})", token);
        return null;
    }

    @RequestMapping("/test4")
    public ModelAndView test4() {
        log.debug("test4()");
        return null;
    }
}
