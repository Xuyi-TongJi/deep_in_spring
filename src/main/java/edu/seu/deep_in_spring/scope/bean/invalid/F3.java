package edu.seu.deep_in_spring.scope.bean.invalid;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class F3 {
}
