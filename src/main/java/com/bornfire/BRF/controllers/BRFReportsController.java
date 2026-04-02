package com.bornfire.BRF.controllers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ConfigurationProperties("default")
@RequestMapping(value = "Reports")
public class BRFReportsController {

}
