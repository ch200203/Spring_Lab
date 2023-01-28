package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController  {

    @GetMapping("hello")
    public String Hello(Model model) {
        model.addAttribute("data", "hello!!");
        return "hello"; // hello-> 화면명
    }
}
