package com.uas.b2c.service.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.seller.model.Prod;
import com.uas.api.b2c_erp.seller.model.ProductDetailERP;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.model.B2CUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;
/**
 * 
 * B2C物料、交易信息信息更新
 * 
 * @author wuyx
 *
 */
@Service
public class B2cProdUpdateService {
	
	@Autowired
	private B2CUtil b2cUtil;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private BaseDao baseDao;
	@Autowired
    private B2CProdService b2cProductService;
	
	@Autowired
	private B2CStoreService b2CStoreService;
	private static boolean tranprodstatus = true;
	private static boolean transproddetailstatus = true;
	private static List<Master> masters = null;
	private static Master currentMaster = null;
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	private ArrayList<String> idStrList = new ArrayList<String>();
	private ArrayList<String> codeStrList = new ArrayList<String>();
	public Map<String, Object>  execute() {
		Map<String, Object>  res = new HashMap<String, Object>();
		if (masters == null) {
			masters = Collections.synchronizedList(enterpriseService.getMasters());
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			res = onExecute(master);
		}
		SpObserver.putSp(sob);
		return res;
	}
	/**
	 * 根据master 处理相关操作
	 * */
	private Map<String, Object>  onExecute(Master master) {
		Map<String, Object>  res = new HashMap<String, Object>();
		if (b2cUtil.isB2CMAll(master)) {
			String masterName = master.getMa_name();
			SpObserver.putSp(masterName);
			if(baseDao.isDBSetting("Mall", "prodUpload")){
				logger.info("master: "+masterName+" : "+this.getClass() +" uploadingProductAndDetailMsg start");
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				//初始化库存
				//b2cUtil.initWarehouseStatus();
			    tranprodstatus = true;
			    b2cUtil.onUploadFail("prod",masterName);//恢复上传超时败物料状态
			    //res = b2cUtil.getProductsCount();//计数
			    //if(!res.isEmpty()){
			    	//int acount = Integer.valueOf(res.get("acount").toString());
			    	//int ecount = Integer.valueOf(res.get("ecount").toString());
			    	//if(acount-ecount>0){
			    		idStrList = new ArrayList<String>();
			 		    b2cUtil.insertB2CActionTaskLog("uploadingProductAndDetailMsg", "start","start uploadingProductAndDetailMsg",masterName);
			 		    boolean transtatus = uploadProduct(masterName);//恢复上传超时败物料交易状态
			 			if(transtatus&&tranprodstatus){
			 				/*if(!CollectionUtil.isEmpty(idStrList)&&idStrList.size()>=1){
			 					res.put("scount", (idStrList.size()-1)*1000+idStrList.get(idStrList.size()-1).toString().split(",").length);
			 				}else{
			 					res.put("scount", 0);
			 				}*/
			 				//更新物料明细    
			 				transproddetailstatus = true;
			 				codeStrList = new ArrayList<String>();
			 				logger.info(masterName+" "+this.getClass().getName() + " uploadingProductDetailmsg start");
			 				b2cUtil.onUploadFail("proddetail",masterName);//恢复上传失败交易信息状态
			 				boolean transdetailtatus = uploadProductDetail(masterName);//上传交易信息
			 				if(transdetailtatus&&transproddetailstatus){
			 					b2cUtil.onUploadSuccess(codeStrList,"proddetail",masterName);
			 					logger.info(this.getClass() + " uploadingProductDetailmsg sucess end");
			 					try{
			 						b2cProductService.TransEndB2C(masterName);
			 					}catch (Exception e) {
			 						//e.printStackTrace();
			 					}finally {
			 						b2cUtil.onUploadSuccess(idStrList,"prod",masterName);
			 						logger.info(masterName+" "+this.getClass().getName() + " uploadingProductDetailmsg sucess end");	
			 					}
			 				}else{
			 					logger.info(masterName+" "+this.getClass().getName() + " uploadingProductmsg success and uploadingProductDetailmsg fail");
			 			    }
			 			}else{
			 				logger.info(masterName+" "+this.getClass().getName() + " uploadingProductmsg fail end");
			 			}
			 			b2cUtil.insertB2CActionTaskLog("uploadingProductAndDetailMsg", "end", "end uploadingProductAndDetailMsg",masterName);
			 			//res.put("success", true);
			 			//return res;
			    	//}else{
			    	//	res.put("success", true);
			 		//	return res;
			    	//}
			    //}
			    logger.info(masterName+" "+this.getClass().getName() + " uploadingProductAndDetailMsg end");
			}
		}
		res.put("success", false);
		return res;
	}
	/**
	 * 将未上传的物料传到商城
	 * @throws Exception 
	 */
	
	private boolean uploadProduct(String masterName) {
		List<Prod> prods = b2cUtil.getProducts(masterName);
		if(!CollectionUtil.isEmpty(prods)){	
			String idStr = b2cUtil.getIdStr(prods);
			idStrList.add(idStr);
			String req = b2cProductService.upProductB2C(prods,masterName);					
			if(req.equals("success")){	
				b2cUtil.onUploading(idStr,"prod",masterName);
				if(prods.size()==1000){
					uploadProduct(masterName);
				}
			}else{
				tranprodstatus = false;
			}
		}else if(idStrList.size()==0){
			tranprodstatus = false;
		}
		return true;
	}
	
	//初始化交易信息传到商城
	public boolean uploadProductDetail(String masterName) {
		List<ProductDetailERP> prods = b2cUtil.getProductDetail(masterName);
		if(!CollectionUtil.isEmpty(prods)){		
			String codeStr = b2cUtil.getDetailCodeStr(prods);
			codeStrList.add(codeStr);
			boolean  uploadStatus = b2CStoreService.uploadStoreB2C(prods,masterName);	
			if(uploadStatus){
				b2cUtil.onUploading(codeStr,"proddetail",masterName);
				if(prods.size()==1000){
					uploadProductDetail(masterName);
				}
			}else{
				transproddetailstatus = false;
			}
		}else if(idStrList.size()==0){
			transproddetailstatus = false;
		}
		return true;
	}
}
