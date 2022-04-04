package edu.seu.deep_in_spring.beanFactoryPostProcessor.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Bean2 {

    private static final Logger LOG = LoggerFactory.getLogger(Bean2.class);

    public Bean2() {
        LOG.debug("l have been managed by Spring ");
    }
}
