package framework.core.templateservice.test.controller;

import framework.domain.R;
import framework.domain.ServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/t2")
public class TestExceptionController {
    @RequestMapping("/e1")
    public R<Void> e1() {
        throw new ServiceException("TestExceptionController");
    }
}
