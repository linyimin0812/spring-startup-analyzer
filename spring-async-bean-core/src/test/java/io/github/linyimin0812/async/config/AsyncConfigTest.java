package io.github.linyimin0812.async.config;


import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author linyimin
 **/
public class AsyncConfigTest {

    @Test
    public void getInstance() {
        assertNotNull(AsyncConfig.getInstance());
    }

    @Test
    public void getAsyncBeanProperties() throws IOException {
        AsyncBeanProperties asyncBeanProperties = AsyncBeanPropertiesTest.parsePropertiesFromFile();

        AsyncConfig.getInstance().setAsyncBeanProperties(asyncBeanProperties);

        assertEquals(asyncBeanProperties, AsyncConfig.getInstance().getAsyncBeanProperties());
    }

    @Test
    public void setAsyncBeanProperties() throws IOException {

        AsyncBeanProperties asyncBeanProperties = AsyncBeanPropertiesTest.parsePropertiesFromFile();

        AsyncConfig.getInstance().setAsyncBeanProperties(asyncBeanProperties);

        assertEquals(asyncBeanProperties, AsyncConfig.getInstance().getAsyncBeanProperties());

    }

    @Test
    public void isAsyncBean() throws IOException {

        AsyncConfig.getInstance().setAsyncBeanProperties(AsyncBeanPropertiesTest.parsePropertiesFromFile());

        assertTrue(AsyncConfig.getInstance().isAsyncBean("testBean"));

    }
}