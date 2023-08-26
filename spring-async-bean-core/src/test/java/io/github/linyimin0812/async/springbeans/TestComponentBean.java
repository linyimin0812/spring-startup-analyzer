package io.github.linyimin0812.async.springbeans;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author linyimin
 **/
@Component
public class TestComponentBean {
    @PostConstruct
    public void initTestComponentBean() {}
}
