package com.uas.b2c.service.seller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.uas.api.b2c_erp.buyer.model.AcceptNotify;
import com.uas.api.b2c_erp.buyer.model.AcceptNotifyDetail;
import com.uas.api.b2c_erp.buyer.service.InvoiceNotifyService;
import com.uas.api.crypto.util.SecretUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.ProductBatchUUIdService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

/**
 * 获取B2C平台中的发货通知单，转为UAS的收货通知单过渡表
 * @author XiaoST  2016-6-27 下午5:33:12
 *
 */
@Service
public class B2CPurchaseAcceptNotifyService {

	@Resource(name = "api.invoiceNotifyService")
	private InvoiceNotifyService invoiceNotifyService;
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;
	
    static final String INSERTPURCHASEACCEPTNOTIFY = "insert into B2C$PURCHASEACCEPTNOTIFY(pan_id,pan_puid,pan_pucode,pan_qty,pan_price,pan_logisticscode,pan_b2ccode,pan_date,pan_code,pan_status)values(?,?,?,?,?,'',?,sysdate,?,?)";
    static final String INSERTPURACCEPTNOTIFYDET = "insert into B2C$PURACCEPTNOTIFYDET(pnd_id,pnd_panid,pnd_puid,pnd_qty,pnd_price,pnd_prodcode,pnd_b2cbatchcode,pnd_detno)values(B2C$PURACCEPTNOTIFYDET_SEQ.nextval,";

	/**
	 * 获取单据
	 * @return
	 */
	public void getAllInvoiceNoitify(){
		List<AcceptNotify> notifies = invoiceNotifyService.getAllInvoiceNoitify();
		if (!CollectionUtils.isEmpty(notifies)) {
			long[]ids = new long[notifies.size()];
			int n = 0;
			for(AcceptNotify notify:notifies){	
				long id = notify.getId();
				//判断是否已经生成过收料订单，已经生成过的不允许再次生成
				Object ob = baseDao.getFieldDataByCondition("B2C$PURCHASEACCEPTNOTIFY", "pan_id", "pan_b2ccode='"+id+"'");
				if(ob == null){
					int pan_id = baseDao.getSeqId("B2C$PURCHASEACCEPTNOTIFY_SEQ");
					String code = baseDao.sGetMaxNumber("PurchaseAcceptNotify", 2);
					SqlRowList rs = baseDao.queryForRowSet("select distinct pu_code,pu_id from purchase left join purchasedetail on pd_puid=pu_id where pd_b2ccode=?",notify.getPurchaseId());
					if(rs.next()){	
						List<String>sqls = new ArrayList<String>();
						int detno = 1;
						for(AcceptNotifyDetail ad : notify.getNotifyDetail()){
							//根据批号转为对应的ERP物料
							Object []obs = baseDao.getFieldsDataByCondition("B2C$PURCHASEBATCH left join b2c$goodsonhand on go_prodcode=pb_prodcode", new String[]{"PB_PRODCODE","pb_erpunit","go_unit"}, "PB_B2BBATCHCODE='"+ad.getBatchcode()+"' and PB_PUID="+rs.getLong("pu_id"));
							if(obs != null){
								double rate = productBatchUUIdService.getUnitRate(String.valueOf(obs[2]),String.valueOf(obs[1]));
								sqls.add(INSERTPURACCEPTNOTIFYDET+pan_id+","+rs.getLong("pu_id")+","+ad.getQty()*rate+","+ad.getPrice()/rate+",'"+obs[0]+"','"+ad.getBatchcode()+"',"+detno+")");
								detno++;
							}
						}
						if(detno>1){							
							baseDao.execute(INSERTPURCHASEACCEPTNOTIFY,new Object[]{pan_id,rs.getLong("pu_id"),rs.getString("pu_code"),notify.getQty(),notify.getPrice(),id,code,"待收料"});
							baseDao.execute(sqls);
							ids[n] = id;
							n++;
						}
					}
				}				
			}
			if(n>0){
				long[] b= Arrays.copyOf(ids,n);
				//请求通知B2C，将B2C中的获取的数据改为已下载状态，防止重复请求
				backSuccess(b);	
			}
		}
	}
	
	/**
	 * 返回B2C平台已经获取的单据
	 * @param ids
	 */
	public void backSuccess(long[] ids){
		invoiceNotifyService.backSuccess(ids);
	}
	
	/**
	 * 确认收料，回写出货单状态
	 */
	public void agreeAccept(long[] ids) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.b2bEnable()) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			invoiceNotifyService.agreeInvoice(ids);
		}
	}
}
