package tkt.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import tkt.util.Gzip;

public class TestDownloadUpdate {

	public static String preparingStringToSend() {
		JSONArray array = new JSONArray();
		for (int i = 1; i < 2000; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", i);
			jsonObject.put("hashLogo", "4d186321c1a7f0f354b297e8914ab240");
			jsonObject.put("hashTemplate", "4d186321c1a7f0f354b297e8914ab240");
			array.put(jsonObject);
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
				URL url = new URL("http://192.168.1.27:8080/downloadUpdate");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setChunkedStreamingMode(0);
				urlConnection.setConnectTimeout(5000);
				urlConnection.setReadTimeout(5000);

				OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

				OutputStreamWriter osw = new OutputStreamWriter(out);
				osw.write(toBeSend);
				osw.close();
				// writeStream(out);

				// InputStream in = new
				// BufferedInputStream(urlConnection.getInputStream());
				// readStream(in);

				// opens input stream from the HTTP connection
				// InputStream inputStream = connection.getInputStream();
				InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
				String saveFilePath = "C:/TEMP/xyzClient111.zip";

				// opens an output stream to save into file
				FileOutputStream outputStream = new FileOutputStream(saveFilePath);

				int bytesRead = -1;
				byte[] buffer = new byte[2048];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				System.out.println("\nError while calling Crunchify REST Service");
				System.out.println(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
