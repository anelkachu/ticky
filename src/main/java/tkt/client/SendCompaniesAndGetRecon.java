package tkt.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import tkt.util.Gzip;

public class SendCompaniesAndGetRecon {

	public static String preparingStringToSend() {
		JSONArray array = new JSONArray();
		for (int i = 1; i < 2000; i++) {
			if (i % 2 == 0) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", i);
				jsonObject.put("hashLogo", "6919e79092e1ae1476e7e526e6fe7943");
				jsonObject.put("hashTemplate", "7098ce780f0d722df7e4b1cc65e015dc");
				array.put(jsonObject);
			}
		}
		String toBeSend = array.toString();
		return toBeSend;
	}

	public static String compressAndEncode(String toBeSend) throws IOException {
		// Check encoded sizes
		final byte[] utf8Bytes = toBeSend.getBytes("UTF-8");
		System.out.println(utf8Bytes.length + " bytes");

		byte[] compressed = Gzip.compress(toBeSend);
		byte[] b64String = Base64.encodeBase64(compressed);
		System.out.println("B64: " + b64String.length + " bytes");

		return new String(b64String);
	}

	public static void main(String[] args) {
		try {
			String toBeSend = preparingStringToSend();
			toBeSend = compressAndEncode(toBeSend);

			// Step2: Now pass JSON File Data to REST Service
			try {
				URL url = new URL("http://localhost:8080/recon");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(toBeSend);
				out.close();

				System.out.println("\nCrunchify REST Service Invoked Successfully..");

				// opens input stream from the HTTP connection
				InputStream inputStream = connection.getInputStream();
				String response = IOUtils.toString(inputStream);
				inputStream.close();
				System.out.println("RESPONSE: " + response);

			} catch (Exception e) {
				System.out.println("\nError while calling Crunchify REST Service");
				System.out.println(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
