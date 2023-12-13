package io.github.linyimin0812.profiler.core.http

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author linyimin
 * */
@Stepwise
class SimpleHttpServerSpec extends Specification {

    def "test start"() {

        given:
        SimpleHttpServer.stop()

        when:
        SimpleHttpServer.start()

        then:
        isURLAvailable(SimpleHttpServer.endpoint() + "/hello")

        cleanup:
        SimpleHttpServer.stop()
    }

    def "test stop"() {
        when:
        SimpleHttpServer.stop();

        then:
        !isURLAvailable(SimpleHttpServer.endpoint() + "/hello")

        cleanup:
        SimpleHttpServer.stop()

    }

    def "test getPort default"() {
        when:
        ProfilerSettings.clear()

        then:
        SimpleHttpServer.getPort() == 8065
    }

    def "test getPort from properties"() {

        given:
        URL configurationURL = SimpleHttpServerSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties");


        when:
        ProfilerSettings.loadProperties(configurationURL.getPath());

        then:
        SimpleHttpServer.getPort() == 8066

    }

   def "test endpoint default"() {
       when:
       ProfilerSettings.clear();

       then:
       SimpleHttpServer.endpoint() == 'http://localhost:8065'
   }

    def "test endpoint from properties"() {

        given:
        URL configurationURL = SimpleHttpServerSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties");

        when:
        ProfilerSettings.loadProperties(configurationURL.getPath());

        then:
        SimpleHttpServer.endpoint() == 'http://localhost:8066'

    }

    static boolean isURLAvailable(String endpoint) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3_000);
            connection.setReadTimeout(3_000);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException ignored) {
            return false;
        }
    }
}
