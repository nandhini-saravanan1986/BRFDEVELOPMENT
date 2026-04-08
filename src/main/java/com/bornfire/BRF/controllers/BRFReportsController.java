package com.bornfire.BRF.controllers;

import java.security.PrivateKey;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bornfire.BRF.services.ReportServices;

@Controller
@ConfigurationProperties("default")
@RequestMapping(value = "Reports")
public class BRFReportsController {
	
	private ReportServices reportService;
	
	public BRFReportsController(ReportServices reportService) {
		super();
		this.reportService = reportService;
	}



	@GetMapping("/reportTemplate")
	@ResponseBody
	public Map<String,Object> getTemplate(@RequestParam String code) throws Exception {

	    return reportService.readTemplate(code);

	}
	
	@GetMapping("/glHeads")
	@ResponseBody
	public List<String> getGLHeads(@RequestParam String dataType) {
	    return reportService.getGLHeads(dataType);
	}

	@GetMapping("/glSubHeads")
	@ResponseBody
	public List<Map<String, String>> getGLSubHeads(@RequestParam String dataType,
	                                                @RequestParam String glHead) {
	    return reportService.getGLSubHeads(dataType, glHead);
	}
	
	
	
	
}




