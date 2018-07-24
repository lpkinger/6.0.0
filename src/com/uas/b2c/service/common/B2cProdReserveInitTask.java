package com.uas.b2c.service.common;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * 将UAS物料信息、物料交易信息和库存同步到商城
 * 
 * @author wuyx
 *
 */

@Component("b2cProdReserveInitTask")
@EnableAsync
@EnableScheduling
public class B2cProdReserveInitTask {
	@Autowired
	private B2cProdUpdateService b2cProdUpdateService;
	@Autowired
	private B2cReserveUpdateService b2cReserveUpdateService;
	static final String UPDATEINITSTATUS ="update configs set data = 1 where CODE='b2cInitStatus' AND CALLER='sys'";
	static final String INITCHECK ="update warehouse set wh_ismallstore = 1 where wh_code in ( ";
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	public void execute() {
		b2cProdUpdateService.execute();//初始化物料信息、交易信息
		b2cReserveUpdateService.execute();//初始化仓库信息
	}
}
