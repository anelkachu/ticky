package tkt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Strings;

import tkt.dao.CompanyDao;
import tkt.model.Company;

public class TktUtil {

	public static File dataDir;

	static {
		if (Strings.isNullOrEmpty(System.getenv("OPENSHIFT_DATA_DIR"))) {
			dataDir = new File(System.getProperty("OPENSHIFT_DATA_DIR"));
		} else {
			dataDir = new File(System.getenv("OPENSHIFT_DATA_DIR"));
		}
	}

	public static Map<Long, Pair<String, String>> getMapFromCompaniesReceived(String receivedData) throws IOException {
		byte[] b64Decoded = Base64.decodeBase64(receivedData);
		String result64 = Gzip.decompress(b64Decoded);
		JSONArray array = new JSONArray(result64);
		Map<Long, Pair<String, String>> companiesReceived = new HashMap<Long, Pair<String, String>>(array.length());

		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			long id = obj.getLong("id");
			String hashLogo = obj.getString("hashLogo");
			String hashTemplate = obj.getString("hashTemplate");
			companiesReceived.put(id, Pair.of(hashLogo, hashTemplate));
		}
		return companiesReceived;
	}

	public static void initDatabaseWithFolder(CompanyDao dao) {
		Map<String, String> map = new HashMap<String, String>();
		File[] logosAndTemplates = dataDir.listFiles();
		for (File f : logosAndTemplates) {
			try {
				FileInputStream fis = new FileInputStream(f);
				String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
				map.put(f.getName(), md5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Company c : dao.findAll()) {
			String hashLogo = map.get(c.getId() + ".png");
			String hashTemplate = map.get(c.getId() + ".html");

			if (!Strings.isNullOrEmpty(hashLogo)) {
				c.setHashLogo(hashLogo);
			}

			if (!Strings.isNullOrEmpty(hashTemplate)) {
				c.setHashTemplate(hashTemplate);
			}
			dao.save(c);
		}
	}

}
