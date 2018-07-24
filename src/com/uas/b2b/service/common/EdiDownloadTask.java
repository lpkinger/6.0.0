package com.uas.b2b.service.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.model.Master;
import com.uas.platform.warehouse.tools.client.Warehouse;

@Component
@EnableAsync
@EnableScheduling
public class EdiDownloadTask  extends AbstractTask {
	static final String ENDPOINT = "218.17.158.219:9443/warehouse/";
	//static final String ENDPOINT = "localhost:20320/";
    /**
     * App key of enterprise.
     */
    static final String EN_APP_KEY = "178698387fa34c9c969302641f773398";

    /**
     * Secret key of enterprise.
     */
    static final String EN_SECRET_KEY = "E707D934C24D74CCC5180E64B76176F9DF985855";
	
	@Async
	public void execute() {
		super.execute();
	}
	
	@Override
	protected void onExecute(Master master) {
		try {
			Warehouse client = new Warehouse(ENDPOINT,
				      EN_APP_KEY,EN_SECRET_KEY);
			/**
			 * 下载任务
			 */
			getReplyNotice(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取回传信息.
	 */
	public void getReplyNotice(Warehouse client) throws IOException, URISyntaxException {
		String reply = client.getReplyNotice();
		List<Map<Object,Object>> datas = BaseUtil.parseGridStoreToMaps(reply);
		Object type;
		boolean flag = true;		
		if(datas.size()>0){
			logger.info(this.getClass() + " downloadReply");
			logger.info(this.getClass() + " datas: " + reply);
			for(Map<Object,Object> data:datas){
				try{
					type = data.get("type");
					if(checkObjectsHasText(type)){
						if("packing-reply".equals(type)){
							doPackingReply(data);
						}else if("invoice-reply".equals(type)){
							doInvoiceReply(data);
						}else{
							flag = checkObjectsHasText(data.get("no"),data.get("state"),data.get("docType"));
							if(flag){
								doNormalReply(data);	
							}
						}	
					}
				}catch(Exception e){
					e.printStackTrace();
					logger.info(this.getClass() + e.getMessage());
				}
			}
		}
	}
	
	public void doNormalReply(Map<Object,Object> data){
		List<String> sqls = new ArrayList<String>();
		String sql;
		int id;
		sql = "update prodinout set pi_logisticsstatus='"+data.get("state")+"' where pi_class='"+data.get("docType")+"' and pi_inoutno='"+data.get("no")+"'";
		sqls.add(sql);
		id = baseDao.getSeqId("EDI_REPLY_SEQ");
		
		data.put("id", id);
		sql = SqlUtil.getInsertSqlByMap(data, "edi_reply");
		sqls.add(sql);
		baseDao.execute(sqls);
		String out = baseDao.callProcedure("SP_EDI_TASK", new Object[]{String.valueOf(id)});
		if(out!=null&&!"".equals(out)){
			//log
		}
	}
	
	public void doPackingReply(Map<Object,Object> data){
		String sql;
		int mainId = baseDao.getSeqId("EDI_PACKING_REPLY_SEQ");
		Object obj = data.get("details");
		if(obj!=null&&!"".equals(obj)){
			System.out.println(data.get("details"));
			List<Map<Object,Object>> details = BaseUtil.parseGridStoreToMaps(obj.toString());
			for (Map<Object, Object> detail : details) {
				detail.put("id", baseDao.getSeqId("EDI_PACKING_REPLY_DETAIL_SEQ"));
				detail.put("mainid", mainId);
			}
			List<String> sqls = SqlUtil.getInsertSqlbyGridStore(details, "edi_packing_reply_detail");
			
			data.remove("details");
			data.put("id", mainId);
			sql = SqlUtil.getInsertSqlByMap(data, "edi_packing_reply");
			sqls.add(sql);	
			
			baseDao.execute(sqls);
			
			String out = baseDao.callProcedure("SP_PACKINGINVOICE_REPLY", new Object[]{String.valueOf(mainId),"Packing"});
			if(out!=null&&!"".equals(out)){
				//log
			}
		}
	}
	
	public void doInvoiceReply(Map<Object,Object> data){
		String sql;
		int mainId = baseDao.getSeqId("EDI_INVOICE_REPLY_SEQ");
		Object obj = data.get("details");
		if(obj!=null&&!"".equals(obj)){
			System.out.println(data.get("details"));
			List<Map<Object,Object>> details = BaseUtil.parseGridStoreToMaps(obj.toString());
			for (Map<Object, Object> detail : details) {
				detail.put("id", baseDao.getSeqId("EDI_INVOICE_REPLY_DETAIL_SEQ"));
				detail.put("mainid", mainId);
			}
			List<String> sqls = SqlUtil.getInsertSqlbyGridStore(details, "edi_invoice_reply_detail");
			
			data.remove("details");
			data.put("id", mainId);
			sql = SqlUtil.getInsertSqlByMap(data, "edi_invoice_reply");
			sqls.add(sql);	
			
			baseDao.execute(sqls);
			
			String out = baseDao.callProcedure("SP_PACKINGINVOICE_REPLY", new Object[]{String.valueOf(mainId),"Invoice"});
			if(out!=null&&!"".equals(out)){
				System.out.println(out);
				//log
			}
		}
	}
	
	public void doProdioMainfestReply(Map<Object,Object> data){
		String sql;
		int mainId = baseDao.getSeqId("EDI_PRODINOUT_CUSTOMS_SEQ");
		System.out.println(data);
			data.put("id", mainId);
			sql = SqlUtil.getInsertSqlByMap(data, "EDI_PRODINOUT_CUSTOMS");
			baseDao.execute(sql);
	}
	
	public void doProductMainfestReply(Map<Object,Object> data){
		String sql;
		int mainId = baseDao.getSeqId("PRODUCT_CUSTOMS_SEQ");
		System.out.println(data);
			data.put("id", mainId);
			sql = SqlUtil.getInsertSqlByMap(data, "PRODUCT_CUSTOMS");
			baseDao.execute(sql);
	}
	
	public void doFeeReply(Map<Object,Object> data){
		String sql;
		int mainId = baseDao.getSeqId("PRODINOUT_FEE_SEQ");
		System.out.println(data);
			data.put("id", mainId);
			sql = SqlUtil.getInsertSqlByMap(data, "PRODINOUT_FEE");
			baseDao.execute(sql);
	}
	//判断多个字符串是否为空
	public boolean checkObjectsHasText(Object... args){
		boolean flag = true;
		for(Object obj:args){
			if(obj==null){
				flag = false;
				break;
			}else if("".equals(obj.toString())){
				flag = false;
				break;
			}
		}
		return flag;
	}
}
