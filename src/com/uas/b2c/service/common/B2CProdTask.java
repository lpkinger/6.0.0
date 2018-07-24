package com.uas.b2c.service.common;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import com.uas.api.b2c_erp.seller.model.Prod;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

/**
 * 将UAS物料信息同步到商城
 * 
 * @author hy
 *
 */

@Component("b2cProdTask")
@EnableAsync
@EnableScheduling
public class B2CProdTask{
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	@Autowired
    private B2CProdService b2cProductService;
	
	private static List<Master> masters = null;
	
	public void execute() {
		if (masters == null) {
			masters = Collections.synchronizedList(enterpriseService.getMasters());
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (master.b2bEnable()) {
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				SpObserver.putSp(master.getMa_name());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				uploadProduct();//上传任务
			}
		}
		SpObserver.putSp(sob);
	}

	/**
	 * 将未上传的物料传到商城
	 */
	public void uploadProduct() {
				List<Prod> prods = getProducts();
				String idStr = getIdStr(prods);
				onProductUploadSuccess(idStr);
				if(!CollectionUtil.isEmpty(prods)){/*			
					try {
						String req = b2cProductService.upProductB2C(prods);					
						if(req.equals("success")){							
							onProductUploadSuccess(idStr);
							uploadProduct();							
						}else{
							onProductUploadFail(idStr);
						}
					} catch (Exception e) {
						onProductUploadFail(idStr);
						e.printStackTrace();
					}
				*/}
	}
	
	/**
	 *	获取未上传的物料信息
	 */
	private List<Prod> getProducts() {
		try {
			List<Prod> prods = baseDao.getJdbcTemplate().query(
					"select * from (select pr_id,pr_code,pr_detail,pr_brand,pr_orispeccode,pr_uuid from product where  nvl(pr_b2csendstatus,' ')<>'已上传' and  nvl(pr_status, ' ') = '已审核' order by pr_id) where rownum <= 1000",
					new BeanPropertyRowMapper<Prod>(Prod.class));
			return prods;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**	
	 *	上传成功
	 */
	private void onProductUploadSuccess(String idStr) {
		baseDao.execute("update product set pr_b2csendstatus='已上传' where pr_id in (" + idStr + ")");
	}
	
	/**	
	 *	上传失败
	 */
	private void onProductUploadFail(String idStr) {
		baseDao.execute("update product set pr_b2csendstatus='待上传' where pr_id in (" + idStr + ")");
	}
	
	/**	
	 *	得到idStr
	 */
	private String getIdStr(List<Prod> prods){
		String idStr = "";
		for (Prod prod : prods) {
			idStr+=prod.getPr_id().toString();
			idStr+=",";
		}
		if(idStr!=""){
			idStr = idStr.substring(0,idStr.length() - 1);
		}
		return idStr;
	}
}
