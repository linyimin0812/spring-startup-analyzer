package io.github.linyimin0812.async.config;


import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author linyimin
 **/
class AsyncConfigTest {

    @Test
    void getInstance() {
        assertNotNull(AsyncConfig.getInstance());
    }

    @Test
    void getAsyncBeanProperties() throws IOException {
        AsyncBeanProperties asyncBeanProperties = AsyncBeanPropertiesTest.parsePropertiesFromFile();

        AsyncConfig.getInstance().setAsyncBeanProperties(asyncBeanProperties);

        assertEquals(asyncBeanProperties, AsyncConfig.getInstance().getAsyncBeanProperties());
    }

    @Test
    void setAsyncBeanProperties() throws IOException {

        AsyncBeanProperties asyncBeanProperties = AsyncBeanPropertiesTest.parsePropertiesFromFile();

        AsyncConfig.getInstance().setAsyncBeanProperties(asyncBeanProperties);

        assertEquals(asyncBeanProperties, AsyncConfig.getInstance().getAsyncBeanProperties());

    }

    @Test
    void isAsyncBean() throws IOException {

        AsyncConfig.getInstance().setAsyncBeanProperties(AsyncBeanPropertiesTest.parsePropertiesFromFile());

        assertTrue(AsyncConfig.getInstance().isAsyncBean("testBean"));

    }
}