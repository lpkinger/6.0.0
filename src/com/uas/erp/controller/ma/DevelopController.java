package com.uas.erp.controller.ma;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.DevelopService;

@Controller
@RequestMapping("ma/dev/")
public class DevelopController {
  @Autowired
  private DevelopService developService;
  
  @RequestMapping("/exec.action")
  @ResponseBody
  public Map<String,Object> exec(HttpServletResponse response,String statement) {  
	return developService.exec(statement);
  }

}
