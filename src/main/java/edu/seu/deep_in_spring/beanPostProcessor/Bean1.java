package edu.seu.deep_in_spring.beanPostProcessor;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@ToString
public class Bean1 {
    private static final Logger LOG = LoggerFactory.getLogger(Bean1.class);

    private Bean2 bean2;
    private Bean3 bean3;
    private String port;

    @Autowired
    public void setBean2(Bean2 bean2) {
        LOG.debug("@Autowired:bean2");
        this.bean2 = bean2;
    }

    @Resource
    public void setBean3(Bean3 bean3) {
        LOG.debug("@Resource:bean3");
        this.bean3 = bean3;
    }

    @Autowired
    public void setPort(@Value("123") String port) {
        LOG.debug("@Value生效");
        this.port = port;
    }

    @PostConstruct
    public void init() {
        LOG.debug("初始化@PostConstruct生效");
    }

    @PreDestroy
    public void destroy() {
        LOG.debug("销毁前@PreDestory生效");
    }

    public String getPort() {
        return port;
    }
}
