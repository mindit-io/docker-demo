package io.mindit.dockerdemo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@Slf4j
@Category(Integration.class)
@TestMethodOrder(OrderAnnotation.class)
public class DockerDemoTests {

    @Before
    public void startApplication() {
        log.info("Wait until docker-demo is up");
        Wait.untilApplicationIsUp();
        log.info("Application docker-demo is up");
    }

    @Test
    @Order(1)
    public void testInsertUser() throws IOException {
        log.info("Try to insert user...");
        String name = "Dumi";
        String email = "my.email@something.com";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://localhost:8080/demo/add");

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("email", email));
            post.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(post);
            assertEquals(200, response.getStatusLine().getStatusCode());
            log.info("User inserted successfully...");
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("http://localhost:8080/demo/count");

            CloseableHttpResponse response = client.execute(get);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            assertEquals("1", responseString);
        }
    }
}
