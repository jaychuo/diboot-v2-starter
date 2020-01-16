package test.java;

import com.diboot.component.file.file.http.CustomSSLSocketFactory;
import junit.framework.TestCase;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;

/**
 * dengxiaojie
 * wechat:haizei508953
 */
public class CustomSSLSocketFactoryTest  extends TestCase {

    public void testRequest() {
        try {
            // 创建SAXReader对象
            SAXReader reader = new SAXReader();
            // 读取文件 转换成Document
            Document document = reader.read(new File("D:\\java开发\\eclipse-workspace\\zz\\WebContent\\WEB-INF\\proposal\\2300.xml"));
            // document转换为String字符串
            String documentStr = document.asXML();
            System.out.println(postMultipartSSL("对方地址", documentStr));
            assert (true);
        } catch (Exception e){

        }
    }

    public static String postMultipartSSL(String url, String params) {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = "";
        String fileName = "";
        try {
            httpClient = (CloseableHttpClient)CustomSSLSocketFactory.newHttpClient();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(4000).setConnectTimeout(4000).build();
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            HttpEntity entity = new StringEntity(params, "utf-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");

        } catch (ClientProtocolException e) {
            result = "";
        } catch (IOException e) {
            result = "";
        }
        return result;
    }

}
