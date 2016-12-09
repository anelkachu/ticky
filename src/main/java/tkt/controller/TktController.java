package tkt.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.wnameless.json.flattener.JsonFlattener;

import tkt.dao.CompanyDao;
import tkt.dao.GenTicketDao;
import tkt.model.Company;
import tkt.model.GenTicket;
import tkt.model.Ticket;
import tkt.util.Gzip;
import tkt.util.Minify;
import tkt.util.TktUtil;
import tkt.util.Zip;

@RestController
public class TktController {

	@Autowired
	private CompanyDao dao;

	@Autowired
	private GenTicketDao genTicketDao;

	public static String ERROR_CODE = "-1";
	public static String OK_CODE = "0";

	@PostConstruct
	public void init() throws IOException {
		// TktUtil.initDatabaseWithFolder(dao);
	}

	@RequestMapping(value = "/recon", method = RequestMethod.POST, produces = "application/json")
	public Iterable<Company> recon(InputStream incomingData) throws IOException {
		StringBuilder receivedData = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				receivedData.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}

		Map<Long, Pair<String, String>> companiesReceived = TktUtil
				.getMapFromCompaniesReceived(receivedData.toString());
		Iterable<Company> databaseCompanies = dao.findAll(companiesReceived.keySet());

		List<Company> list = new ArrayList<Company>();

		for (Company databaseCompany : databaseCompanies) {
			// Hash logo + hash template
			Pair<String, String> pair = companiesReceived.get(databaseCompany.getId());
			String hashLogo = pair.getLeft();
			String hashTemplate = pair.getRight();

			if (!hashLogo.equalsIgnoreCase(databaseCompany.getHashLogo())) {
				databaseCompany.setHashLogo(ERROR_CODE);
			} else {
				databaseCompany.setHashLogo(OK_CODE);
			}

			if (!hashTemplate.equalsIgnoreCase(databaseCompany.getHashTemplate())) {
				databaseCompany.setHashTemplate(ERROR_CODE);
			} else {
				databaseCompany.setHashTemplate(OK_CODE);
			}

			if (databaseCompany.getHashLogo().equalsIgnoreCase(ERROR_CODE)
					|| databaseCompany.getHashTemplate().equalsIgnoreCase(ERROR_CODE)) {
				list.add(databaseCompany);
			}
		}
		return list;
	}

	@RequestMapping(value = "/test", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/x-gzip")
	public ResponseEntity<InputStreamResource> test(@RequestParam("input") String input) {
		System.out.println("RECIBO:" + input);
		return null;
	}

	@RequestMapping(value = "/downloadUpdate", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/x-gzip")
	public ResponseEntity<InputStreamResource> downloadUpdate(@RequestParam("input") String input) throws IOException {
		System.out.println("Received: " + input);
		Map<Long, Pair<String, String>> companiesReceived = TktUtil.getMapFromCompaniesReceived(input);

		// -------------------------------------------------------------
		// Reconciliation
		List<File> filesToBeCompressed = new ArrayList<File>(companiesReceived.keySet().size());

		Iterable<Company> databaseCompanies = dao.findAll(companiesReceived.keySet());
		for (Company databaseCompany : databaseCompanies) {
			// Hash logo + hash template
			Pair<String, String> pair = companiesReceived.get(databaseCompany.getId());
			String hashLogo = pair.getLeft();
			String hashTemplate = pair.getRight();

			if (!hashLogo.equalsIgnoreCase(databaseCompany.getHashLogo())) {
				File logo = new File(TktUtil.dataDir, databaseCompany.getId() + ".png");
				if (logo.exists()) {
					filesToBeCompressed.add(logo);
				} else {
					System.out.println("File " + logo.getAbsolutePath() + " not found, not being zipped");
				}

			}

			if (!hashTemplate.equalsIgnoreCase(databaseCompany.getHashTemplate())) {
				File template = new File(TktUtil.dataDir, databaseCompany.getId() + ".html");
				if (template.exists()) {
					filesToBeCompressed.add(template);
				} else {
					System.out.println("File " + template.getAbsolutePath() + " not found, not being zipped");
				}
			}
		}

		// -------------------------------------------------------------

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		for (File ff : filesToBeCompressed) {
			Zip.addToZipFile(ff.getAbsolutePath(), zos);
		}
		zos.close();
		baos.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		return ResponseEntity.ok().headers(headers).contentLength(bais.available())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(new InputStreamResource(bais));
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public Iterable<Company> findAll(JSONObject inputJsonObj) {
		System.out.println("----------------COUNT ----------" + dao.count());
		return dao.findAll();
	}

	@RequestMapping(value = "/retrieveTestTicket", method = { RequestMethod.GET, RequestMethod.POST })
	public String retrieveTestTicket() {
		return "H4sIAAAAAAAAAL2YS3ObMBCA/0qGc0YDeoDIzXEezUyapkk6PXR6UECxNeXhCtFJmvF/7wo/aoTpAez6YiHBLt8+tIvevSuRmFoL6Z29e/dCGyUrO3yUWSa1nXizl7dyJrLLwqjV5bTUi1ILI+9EDk96U6G1fClr7S1PvSfxepNKuPdFJcKosrBPdCbv6vxZanh2EnES0Qgzb7mEp2+KX6VKVi+xHu8Mn0ojsmatGX2qTWVEkapiNsnLujDeGQ8R8U9Xy9eykFpkj7VO5kLPrFBn6UJViX1utbKrpC3p8lUmtRHPmdyrR5dVtVo4BzNoCbRWGYtRxNb3wBS87aK2qiii+O+0rL4qM5/LLPXOAuTTrtC1JGsdI/Nd09yqAszz7d37XIu1d3jjAVlttTXGX/9t5K3tt7mM4YVW1gf5F7JKtFqsPGddCypOva2lJkX6IJ/B943ozfTueCM1QIxboY2qaVnBFAkb9BYb9lEMbLsMwQAGisKoh+FWJvPBDIHLEIQopC4DQZi0GcIBDIFvxeyH+AiC5c9aZZkYhoJRFLsoPgp9B4Uj3/EGG0ASQXYcwxuUOQgQuswhgCBjQRshGoDAUHCEgMKIdgKKIRI4DKtIGJ0UHEVhD8N9mWXl4MSmLkOMGHGTAkLJH88A2vpyYtTmxCOHgSHcyWvWQRgWSoQf3g0YhW4oYQj92GFgFLGwzUAHMGBEaB+DKE5SeZKXWTo8KzoRBbEbuSiwrYTjIypGYfx/MpsgHrq7E0P8ALsTR3Efw8TUxVAE5rsRRRHubLDU3je6RuAmBY8WUXzPHkXcBKdgR9pGGdJA/aNWjEcJmu6s7RWOeCfPI8QPULljhI9SubnLQMFobtkjBBqS8e4Ap/aWvQNEVqf6gT43RyBv3D5qWOXw+yrHmDSPSbdwdHOD2Ab+ALkR9RaOMcWPuQwUtip3t6VB5+OCDGAAS/R9II2rGG47G3TbWdY0cS0EPKgjB0McLydw6JLAXKd+Y+QHB0nvvi1qREe4p/gR6GDdrpbAJ/ryO9w3SVNltYrsQhjRHIuIaq6aQ417uVBGnFyXufwNb2MXHuRMVcauBvbcQCU/pHlSuayMyBcwSXngs4hH3A8hAqZlvhDF200KtA3qlcrkBylSK+DdOxcmmW8Hm5OVRrW//Z0H3NseakxrrWWRvE3L1J7YXH558HrPNhxze81Zhz2eWS7/AAbmuXwxEgAA";
	}

	@RequestMapping(value = "/post", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
	public GenTicket postTicket(@RequestParam("ticket") String b64TicketContent, HttpServletRequest request) {
		String uid = UUID.randomUUID().toString();
		GenTicket genTicket = new GenTicket();
		genTicket.setId(uid);
		b64TicketContent = b64TicketContent.replaceAll(" ", "+");

		byte[] b64Decoded = Base64.decodeBase64(b64TicketContent);
		String jsonContent = null;
		try {
			jsonContent = Gzip.decompress(b64Decoded);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> map = JsonFlattener.flattenAsMap(jsonContent);
		Ticket t = Ticket.generateTicketFromMap(map);

		genTicket.setAmount(t.getTotalAmount());
		genTicket.setBatchId(t.getBatchTicketId());
		genTicket.setCompanyId(t.getCompanyId());
		genTicket.setShopId(t.getShopId());

		genTicket.setContent(b64TicketContent);
		genTicket.setCreated(new Date());
		genTicketDao.save(genTicket);
		return genTicket;
	}

	@RequestMapping(value = "/get", method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json")
	public GenTicket getTicket(@RequestParam("ticketId") String ticketId) {
		GenTicket ticket = genTicketDao.findById(ticketId);
		return ticket;
	}

}
