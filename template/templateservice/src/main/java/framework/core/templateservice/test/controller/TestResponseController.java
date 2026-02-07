package framework.core.templateservice.test.controller;

import framework.core.templateservice.test.pojo.Student;
import framework.domain.R;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/t1")
@Validated
public class TestResponseController {
    @GetMapping("/s")
    public R<String> s(){
        return R.success("test");
    }

    @RequestMapping("/s1")
    public R<Student> stu(Student student) {
        return R.success(student);
    }

    @RequestMapping("/s2")
    public R<Student> stu2(@Validated @RequestBody Student student) {
        return R.success(student);
    }

    @GetMapping("/s3")
    public R<Boolean> s3(@NotNull String size, @Max(15) Integer age, @NotNull String name) {
        return R.success(true);
    }

}
