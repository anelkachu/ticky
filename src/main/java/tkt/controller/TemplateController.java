package tkt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tkt.dao.CompanyDao;
import tkt.model.Company;

@RestController
public class TemplateController {

	@Autowired
	CompanyDao dao;

	@RequestMapping(value = "/getTemplate", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json")
	public Company getTemplate(@RequestParam("companyId") Long companyId) {
		return dao.findOne(companyId);
	}

}
