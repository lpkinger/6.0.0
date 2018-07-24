package com.uas.b2c.service.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

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
 * B2C库存信息更新
 * 
 * @author wuyx
 *
 */
@Service
public class B2cReserveUpdateService {
	@Autowired
	private B2CUtil b2cUtil;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private B2CStoreService b2cStoreService;
	
	private static List<Master> masters = null;
	private static boolean transtatus = true;
	private ArrayList<String> codeStrList = new ArrayList<String>();
	protected static final Logger logger = Logger.getLogger("SchedualTask");
	
	public void execute() {
		if (masters == null) {
			masters = Collections.synchronizedList(enterpriseService.getMasters());
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			onExecute(master);
		}
		SpObserver.putSp(sob);
	}
	private void onExecute(Master master){
		if (b2cUtil.isB2CMAll(master)) {
			String masterName =  master.getMa_name();
			SpObserver.putSp(masterName);
			if(baseDao.isDBSetting("Mall", "prodReserveUpload")){
				String className = this.getClass().getName();
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				transtatus = true;
				codeStrList = new ArrayList<String>();
				b2cUtil.onUploadFail("prodreverse",masterName);//复原所有上一次上传超时的物料库存状态
				logger.info(masterName+"  "+className + " uploadingProductReserve start");
			    b2cUtil.insertB2CActionTaskLog(masterName+" uploadingProductReserve", "start", "start uploadingProductReserve",masterName);
				boolean Transres = updateStore(masterName);// 上传任务
				
				if (Transres && transtatus) {
					b2cUtil.onUploadSuccess(codeStrList,"prodreverse",masterName);
					logger.info(masterName+"  "+className + " uploading ProductReserve end sucess");
				} else {
					logger.info(masterName+"  "+className + " uploading ProductReserve end failure");
				}
				b2cUtil.insertB2CActionTaskLog("uploadingProductReserve", "end", "end uploadingProductReserve",masterName);
			}
		}
	}
	private boolean updateStore(String masterName) {
		List<ProductDetailERP> prods = b2cUtil.getProductReserve(masterName);
		
		if (!CollectionUtil.isEmpty(prods)) {
			String codeStr = b2cUtil.getDetailCodeStr(prods);
			codeStrList.add(codeStr);
			boolean res = b2cStoreService.updateReserve(prods,masterName);
			
			if(res){
				b2cUtil.onUploading(codeStr,"prodreverse",masterName);
				if(prods.size()==1000){
					updateStore(masterName);	
				}
			}else {
				transtatus = false;
			}
		}
		if (codeStrList.size() == 0) {
			transtatus = false;
		}
		return true;
	}
}
