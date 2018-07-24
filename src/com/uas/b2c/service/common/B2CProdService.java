package com.uas.b2c.service.common;

import java.net.SocketTimeoutException;
import java.util.List;
import javax.annotation.Resource;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.stereotype.Service;
import com.uas.api.b2c_erp.seller.service.ProdService;
import com.uas.b2c.model.B2CUtil;
import com.uas.erp.model.Master;
import com.uas.api.b2c_erp.seller.model.Prod;

/**
 * 
 * 连接B2C物料接口
 * 
 * @author hy
 *
 */
@Service
public class B2CProdService {
	
	@Resource(name = "api.ProdService")
	private ProdService prodService;
	@Autowired 
	private B2CUtil b2cUtil;
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	public String upProductB2C(List<Prod> prods,String masterName) {
		String errorId = b2cUtil.getIdStr(prods);
		try {
			String res = prodService.uploadProducts(prods);
			if(!res.equals("success")){
				b2cUtil.onUploadFailClose(errorId,"prod",masterName);
				b2cUtil.insertB2CActionTaskLog(masterName+"：物料初始化", "error", "物料传输失败！原因："+b2cUtil.saveError(res)+"物料id："+errorId,masterName);
				return "false";
			}else{
				return "success";
			}
		} catch (Exception e) {
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				b2cUtil.onUploadFailClose(b2cUtil.getIdStr(prods),"prod",masterName);
				logger.info(this.getClass() + " upload Productmsg 传输失败，传输物料资料失败");
				b2cUtil.insertB2CActionTaskLog("物料资料传输", "error", "原因："+b2cUtil.saveError(e.getMessage())+" 编号："+errorId,masterName);
				e.printStackTrace();
			}
			return "false";
		}
	}
	public void  TransEndB2C(String masterName) {
	try {
			prodService.callMatch();
		} catch (Exception e) {
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				logger.info(this.getClass() + "通知商城匹配失败");
				b2cUtil.insertB2CActionTaskLog("通知商城匹配", "error", b2cUtil.saveError(e.getMessage()),masterName);
				//e.printStackTrace();
			}
		}
	}
	public String  DecorationB2CUrl() {
	try {
			return prodService.returnStoreUrl();
		} catch (Exception e) {
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				logger.info(this.getClass() + " 我的店铺跳转地址获取失败");
				b2cUtil.insertB2CActionTaskLog("我的店铺跳转", "error", b2cUtil.saveError(e.getMessage()));
				e.printStackTrace();
			}
		}
		return null;
	}
	public String setB2cEnable(String pr_code, Integer isEnable) {
		return prodService.setB2cEnable(pr_code, isEnable);
	}
}
