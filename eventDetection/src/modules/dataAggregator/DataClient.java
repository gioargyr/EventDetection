package modules.dataAggregator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class DataClient {

    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String ENTITY_SET_NAME = "Products";
    private static final String HTTP_HEADER_ACCEPT = "Accept";
    private static final String ID_PREFIX = "('";
    private static final String ID_SUFFIX = "')";
    private static final String SEPARATOR = "/";
    private static final String SERVICE_URL = "https://scihub.copernicus.eu/apihub/odata/v1";
    private static final String USED_FORMAT = DataClient.APPLICATION_OCTET_STREAM;
    private static final String URL_SUFFIX = "$value";
    
    private final HttpClient httpClient;

    public DataClient(String username, String password) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		System.out.println(username+" "+password);
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).setConnectionManager(cm).build();
    }

    private String createUri(String serviceUri, String entitySetName, String id) {
        final StringBuilder absolutUri = new StringBuilder(serviceUri).append(SEPARATOR).append(entitySetName);
        if (id != null) {
            absolutUri.append(ID_PREFIX).append(id).append(ID_SUFFIX).append(SEPARATOR).append(URL_SUFFIX);
        }
        System.out.println(absolutUri.toString());
        return absolutUri.toString();
    }
    
    public void downloadAndSaveById(String id, String targetFile) throws IOException{
        OutputStream target = new FileOutputStream(targetFile);
        String absolutUri = createUri(SERVICE_URL, ENTITY_SET_NAME, id);
        InputStream content = null;
        HttpGet request =null;
        try {
            request = new HttpGet(absolutUri);
            content = execute(request, USED_FORMAT);
            IOUtils.copy(content, target);
        }catch (final ConnectionClosedException ignore) {
            //System.out.println(ignore.getMessage());
        	ignore.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }finally{
        target.close();
        content.close();
        request.releaseConnection();
        }
    }
    
    private InputStream execute( HttpGet request, String contentType) throws IOException {
       
        request.addHeader(HTTP_HEADER_ACCEPT, contentType);
        HttpResponse response =null;
        try {
         response = httpClient.execute(request);
        }catch (Exception e) {
            e.printStackTrace();
        }
        InputStream content = response.getEntity().getContent();

        return content;
    }
}
