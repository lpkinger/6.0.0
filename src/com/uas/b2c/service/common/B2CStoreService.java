package com.uas.b2c.service.common;

import java.net.SocketTimeoutException;
import java.util.List;
import javax.annotation.Resource;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteConnectFailureException;
import org.springframework.stereotype.Service;
import com.uas.api.b2c_erp.seller.service.ProductDetailERPService;
import com.uas.b2c.model.B2CUtil;
import com.uas.api.b2c_erp.seller.model.ProductDetailERP;

/**
 * 
 * 连接B2C库存接口
 * 
 * @author hy
 *
 */
@Service
public class B2CStoreService {
	
	@Resource(name = "api.ProductDetailERPService")
	private ProductDetailERPService productDetailERPService;
	@Autowired
	private B2CUtil b2cUtil;
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	//初始化物料交易详情
	public boolean uploadStoreB2C(List<ProductDetailERP> productDetail,String masterName) {
		String errorCode = b2cUtil.getDetailCodeStr(productDetail);
		try {
			boolean res = productDetailERPService.uploadProductDetailERP(productDetail);
			if(!res){
				b2cUtil.onUploadFailClose(errorCode,"proddetail",masterName);
				logger.info(this.getClass() + "物料交易信息传输失败");
				b2cUtil.insertB2CActionTaskLog("物料交易信息传输", "error", "原因："+b2cUtil.saveError(String.valueOf(res))+" 编号： "+errorCode,masterName);
				return false;	
			}else return true;
		} catch (Exception e) {
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				b2cUtil.onUploadFailClose(errorCode,"proddetail",masterName);
				logger.info(this.getClass() + "物料交易信息传输失败");
				b2cUtil.insertB2CActionTaskLog("物料交易信息传输", "error", "原因："+b2cUtil.saveError(e.getMessage())+" 编号： "+errorCode,masterName);
				e.printStackTrace();
				return false;
			}else return false;
		}
	}
	
	/**
	 * 更新空闲库存数量
	 * @param List<ProductDetailERP> productDetail
	 * @param masterName
	 * */
	public boolean updateReserve(List<ProductDetailERP> productDetail,String masterName) {
		try {
			productDetailERPService.updateReserve(productDetail);
			return true;
		} catch (Exception e) {
			if(!(e instanceof RemoteConnectFailureException|| e instanceof  ConnectTimeoutException|| e instanceof SocketTimeoutException )){
				String errorCode = b2cUtil.getDetailCodeStr(productDetail);
				b2cUtil.onUploadFailClose(errorCode, "prodreverse",masterName);
				logger.info(this.getClass() + "物料库存传输失败");
				b2cUtil.insertB2CActionTaskLog("物料库存传输", "error", "原因："+b2cUtil.saveError(e.getMessage())+" 编号：【"+errorCode+"】",masterName);
				e.printStackTrace();
			}
			return false;
		}		
	}
	//获取参考价格币别信息
	public String getCurrency(String masterName){
		try {
			return String.valueOf(productDetailERPService.getCurrency());
		} catch (Exception e) {
			logger.info(this.getClass() + " 获取商城币别失败");
			b2cUtil.insertB2CActionTaskLog("获取商城币别", "error", b2cUtil.saveError(e),masterName);
		}
		return null;
	}
}
