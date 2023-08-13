package io.github.linyimin0812.async.bean;

import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class AsyncInitBeanFinderTest {

    @org.junit.Test
    public void getAsyncInitMethodName() {
//        String definition = "{\"autowireCandidate\":true,\"autowireMode\":0,\"constructorArgumentValues\":{\"genericArgumentValues\":[],\"indexedArgumentValues\":{}},\"dependencyCheck\":0,\"enforceDestroyMethod\":true,\"enforceInitMethod\":true,\"externallyManagedConfigMembers\":[],\"externallyManagedDestroyMethods\":[],\"externallyManagedInitMethods\":[],\"lenientConstructorResolution\":true,\"methodOverrides\":{\"overrides\":[]},\"nonPublicAccessAllowed\":true,\"primary\":false,\"propertyValues\":{\"converted\":false,\"propertyValueList\":[{\"converted\":false,\"name\":\"order\",\"optional\":false,\"value\":-2147483648},{\"converted\":false,\"name\":\"proxyTargetClass\",\"optional\":false,\"value\":true}]},\"role\":2,\"scope\":\"\",\"synthetic\":false}";
        RootBeanDefinition beanDefinition = new RootBeanDefinition();

        String beanName = "testBean";

        assertNull(AsyncInitBeanFinder.getAsyncInitMethodName(beanName, beanDefinition));
    }
}