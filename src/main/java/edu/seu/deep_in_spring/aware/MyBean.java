package edu.seu.deep_in_spring.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;

@Slf4j
public class MyBean implements BeanNameAware {

    @Override
    public void setBeanName(String s) {
        log.debug("当前Bean" + this + "名字为" + s);
    }
}
