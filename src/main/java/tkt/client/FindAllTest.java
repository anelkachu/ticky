package tkt.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

public class FindAllTest {

	public static void main(String[] args) {
		
		 URL url = null;
         try {
             url = new URL("http://tkt-irofuer.rhcloud.com/findAll");
             URLConnection connection = url.openConnection();
             connection.setDoOutput(true);
             connection.setRequestProperty("Content-Type", "application/json");
             connection.setConnectTimeout(5000);
             connection.setReadTimeout(5000);

             String ret = IOUtils.toString(connection.getInputStream(), "UTF-8");
             System.out.println("RET " + ret);
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }


	}

}
