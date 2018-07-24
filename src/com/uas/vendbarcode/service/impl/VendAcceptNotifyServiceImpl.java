package com.uas.vendbarcode.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AcceptNotifyDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.vendbarcode.service.VendAcceptNotifyService;
@Service
public class VendAcceptNotifyServiceImpl implements VendAcceptNotifyService{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AcceptNotifyDao acceptNotifyDao;
    @Autowired
	private VerifyApplyDao verifyApplyDao;
	
	@Override
	public Map<String, Object> getAcceptNotifyList(Integer page,String condition, Integer start, Integer pageSize, String vendcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object total;
		if(condition == null || ("").equals(condition)){
			condition = " 1=1";
		}
		int start1 = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		total = baseDao.getFieldDataByCondition("acceptNotify", "count(1)", " an_vendcode='"+vendcode+"' and "+condition);
		SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select AN_ID,AN_CODE,to_char(AN_DATE,'yyyy-MM-dd') AN_DATE,AN_STATUS,AN_SENDCODE,AN_VENDCODE,"
						+"AN_VENDNAME,AN_RECORDER,to_char(AN_INDATE,'yyyy-MM-dd') AN_INDATE from acceptNotify where an_vendcode=?  and "+condition+" order by an_id desc)tt "
				 +" where rownum<=? )where rn>=?",vendcode,end,start1);
		if(rs.next()){			
			modelMap.put("datas", rs.getResultList());
			modelMap.put("total", total);
		}
		return modelMap;
	}
	@Override
	public List<Map<String, Object>> getPurchaseData(String caller, String condition,String vendcode) {
		if(("Delivery!Deal").equals(caller)){
			SqlRowList rs = baseDao.queryForRowSet("select pd_id,nvl(pd_qty,0)-nvl(v_pd_turnacceptnotify,0) pd_tqty,pd_code,pd_detno,pd_prodcode,pu_vendname,'' mantissapackage,case when nvl(vpz_zxbzs,0) <=0 then pr_zxbzs else vpz_zxbzs end  unitpackage,'' boxqty,'' lotcode,'' madedate, pr_detail, "
					+ " pr_spec, pd_qty,'' readyqty,nvl(pd_qty,0)-nvl(v_pd_turnacceptnotify,0) finishqty,pu_receivecode, pu_currency,pu_paymentscode, pu_buyercode,pr_orispeccode,pr_brand from purchasedetail left join product on pr_code = pd_prodcode left join purchase on pu_id = pd_puid left join scm_purchaseturnqty_view on pd_id = v_pd_id left join vendprodzxbzs on vpz_prodcode = pd_prodcode and vpz_vendcode = pu_vendcode where pu_vendcode = '"+vendcode+"'"
							+ "and nvl(pu_statuscode,' ')='AUDITED' and nvl(pd_status,' ')<>'已结案' and nvl(pd_qty,0)>nvl(pd_acceptqty,0) and nvl(pd_qty,0)>nvl(pd_yqty,0) and "
							+ "nvl(pd_qty,0)-nvl(v_pd_turnacceptnotify,0)>0 and "+condition);
			if(rs.next()){
				return rs.getResultList();
			}
		}
		return null;
	}
	@Override
	public Map<String, Object> getAcceptNotifyForm(String caller, Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select an_id,an_code,an_vendcode,an_vendname,an_recorder,an_buyer,an_sendcode, "
					+" to_char(an_indate,'yyyy-MM-dd') an_indate,an_currency,an_payment,to_char(an_paydate,'yyyy-MM-dd') an_paydate,an_status,an_statuscode from acceptnotify where an_id = ?",id);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			return null;
		}
	}
	@Override
	public List<Map<String, Object>> getAcceptNotifyGrid(String caller, Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from acceptnotifydetail left join product on and_prodcode = pr_code where and_anid = ?",id);
		if(rs.next()){
			return rs.getResultList();
		}else{			
			return null;
		}
	}
	@Override
	public void update(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + store.get("an_id"));
		StateAssert.updateOnlyEntering(status);
		//更新主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "acceptnotify", "an_id");
		baseDao.execute(formSql);
        //更新从表
		for (Map<Object, Object> s : grid) {			
			s.remove("pr_detail");
			s.remove("pr_spec");
			s.remove("pr_unit");
			s.remove("pr_brand");
			s.remove("pr_orispeccode");
		}
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "acceptnotifydetail","and_id");
		baseDao.execute(gridSql);
		//日志的记录作用
		baseDao.logger.update(caller, "an_id", store.get("an_id"));
	}
	@Override
	public void submit(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition("acceptNotify", "an_statuscode", "an_id=" +id);
		StateAssert.submitOnlyEntering(status);	
		//通知单数量不能大于采购单数量-已转通知数之和
		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(and_detno) and_detno from acceptNotifydetail where and_anid = ? and exists (select 1 from purchasedetail "
			+			"	where pd_code = and_ordercode and pd_detno = and_orderdetno and (pd_qty - pd_ypurcqty) < and_inqty) ",id);
		if(rs.next() && rs.getString("and_detno") !=null){
			BaseUtil.showError("通知单数量不能大于采购单数量-已转通知数之和,序号"+rs.getString("and_detno"));
		}
		rs = baseDao.queryForRowSet("select wm_concat(and_detno) and_detno from acceptNotifydetail left join purchasedetail on pd_code = and_ordercode and pd_detno = and_orderdetno left join purchase on pd_puid = pu_id "
				+ " where and_anid =? and not exists(select 1 from acceptnotify where an_vendcode = pu_vendcode and  an_vendname = pu_vendname and an_currency = pu_currency and pu_buyername = an_buyer and pu_payments = an_payment and pu_receivename = an_receivename and an_id=?)",id,id);
		if(rs.next() && rs.getString("and_detno") != null){
			BaseUtil.showError("请检查采购单+序号对应的供应编号、应付供应商、币别、采购员、付款方式、应付供应商是否和主表一致,序号"+rs.getString("and_detno"));
		}
		rs = baseDao.queryForRowSet("select wm_concat(and_detno) and_detno from acceptnotifydetail where and_anid =? and and_barqty>0 and nvl(and_barqty,0)<>nvl(and_inqty,0)",id);
		if (rs.next() && rs.getString("and_detno") != null){
			BaseUtil.showError("序号"+rs.getString("and_detno")+"存在条码,条码数量不等于采购数量");
		}
		rs = baseDao.queryForRowSet("select wm_concat(and_detno) and_detno from acceptnotifydetail left join product on pr_code = and_prodcode where and_anid =? and nvl(pr_tracekind,0) <> 0 and nvl(and_barqty,0) = 0",id);
		if (rs.next() && rs.getString("and_detno") != null){
			BaseUtil.showError("序号"+rs.getString("and_detno")+"是管控物料,条码数量不允许为0");
		}
		updatePrice(id);
		// 判断是否超采购数收料
		acceptNotifyDao.checkQty(id);
		// 判断批号是否重复
		String errRows = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(and_detno) from acceptnotifydetail where and_anid=? and (and_batchcode,and_prodcode,and_whcode) in (select and_batchcode,and_prodcode,and_whcode from (select count(1) c, and_batchcode,and_prodcode,and_whcode from acceptnotifydetail where (and_batchcode,and_prodcode,and_whcode) in (select and_batchcode,and_prodcode,and_whcode from acceptnotifydetail where and_anid=?) group by and_batchcode,and_prodcode,and_whcode) where c > 1)",
						String.class, id, id);
		if (errRows != null) {
			BaseUtil.showError("批号重复,行:" + errRows);
		} else {
			errRows = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(and_detno) from acceptnotifydetail where and_anid=? and (and_batchcode,and_prodcode,and_whcode) in (select ba_code,ba_prodcode,ba_whcode from batch)",
							String.class, id);
			if (errRows != null) {
				BaseUtil.showError("批号重复,行:" + errRows);
			}
		}
		String notEnoughSale = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('<br>行:'||and_detno||',PO数:'||nvl(pd_qty,0)||',通知单已提交数:'||nvl(and_commitedqty,0)||',本次数:'||nvl(and_inqty,0)) from (select and_detno,and_inqty,(select sum(nvl(and_inqty,0)) from acceptnotifydetail left join acceptnotify on an_id=and_anid where and_ordercode=A.and_ordercode and and_orderdetno=A.and_orderdetno and an_statuscode<>'ENTERING') and_commitedqty,nvl(pd_qty,0)+nvl(pd_backqty,0) pd_qty from acceptnotifydetail A left join purchasedetail on pd_code=and_ordercode and pd_detno=and_orderdetno where and_anid=?) where nvl(pd_qty,0)<nvl(and_commitedqty,0)+nvl(and_inqty,0)",
						String.class, id);
		if (notEnoughSale != null) {
			BaseUtil.showError("数量超过了PO数量,提交失败:<br>" + notEnoughSale);
		}
		// 执行提交操作
		baseDao.submit("acceptNotify", "an_id=" + id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "an_id", id);
	}
	@Override
	public void resSubmit(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition("acceptNotify", "an_statuscode", "an_id=" +id);		
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("acceptNotify", "an_id=" + id, "an_status", "an_statuscode");	
		// 记录操作		
		baseDao.logger.resSubmit(caller,"an_id", id);
	}
	@Override
	public void delete(String caller, int id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("acceptNotify", "an_statuscode", "an_id=" + id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{id});
		//删除Dispatch
		baseDao.deleteById("acceptNotify", "an_id", id);
		//删除DispatchDetail
		baseDao.deleteById("acceptNotifyDetail", "and_anid", id);
		//删除DispatchDetail
		baseDao.deleteById("baracceptNotify", "ban_anid", id);
		//记录操作
		baseDao.logger.delete(caller, "an_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{id});
	}

	
	
	
	private void updatePrice(Object id) {
		baseDao.execute(
				"update AcceptNotify set an_rate=(SELECT nvl(cm_crrate,0) from Currencysmonth where an_currency=cm_crname and cm_yearmonth=to_char(an_date,'yyyymm')) where an_id=?",
				id);
		baseDao.execute(
				"update AcceptNotifydetail set and_price=(select round(price+price*amount/case when total=0 then 1 else total end,8) from (select and_id,(and_orderprice*an_rate/(1+and_taxrate/100)) price,(select sum(and_inqty*and_orderprice*an_rate*(1+nvl(and_taxrate,0)/100)) from AcceptNotifyDetail pp1 left join AcceptNotify p1 on pp1.and_anid=p1.an_id where p1.an_id=acceptnotifydetail.and_anid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetailAN A where A.pd_anid=acceptnotifydetail.and_anid),0) amount from AcceptNotifyDetail left join AcceptNotify on and_anid=an_id where and_anid=?) B where B.and_id=acceptnotifydetail.and_id) where and_anid=? and nvl(and_price,0)=0",
				id, id);
		baseDao.execute(
				"update AcceptNotifydetail set and_total=round(and_price*and_inqty,2),and_ordertotal=round(and_orderprice*and_inqty,2),and_plancode=round(and_orderprice*and_inqty*(select an_rate from AcceptNotify where an_id=and_anid),2) where and_anid=?",
				id);
		baseDao.execute("update AcceptNotifydetail set and_barcode=round(and_total-and_plancode,2) where and_anid=?", id);
		baseDao.execute(
				"update AcceptNotify set an_total=round((select sum(and_orderprice*and_inqty) from acceptnotifydetail where an_id=and_anid),2) where an_id=?",
				id);
	}
	@Override
	public void confirmDelivery(String caller, int id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核操作
		baseDao.audit("AcceptNotify", "an_id=" + id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "an_id", id);
	}
	@Override
	public void cancelDelivery(String caller, int id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AcceptNotify", "an_statuscode", "an_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		int count = baseDao.getCount("select count(1) from AcceptnotifyDetail where and_anid = "+id+" and exists(select 1 from verifyapplydetail where and_id = vad_andid)");
		if(count>0){
			BaseUtil.showError("已有明细转收料单,不允许取消送货");
		}
		// 执行反审核操作
		baseDao.resOperate("AcceptNotify", "an_id=" + id, "an_status", "an_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "an_id", id);
	}
	
	 final static  String INSERTBARCODEIO = "insert into barAcceptNotify(ban_id,ban_barcode,ban_barid,ban_anid,ban_ancode,ban_andid,ban_anddetno,ban_prodcode,ban_prodid, "
			 	+" ban_qty,ban_vendcode,ban_outboxcode,ban_outboxid,ban_printstatus)values(barAcceptNotify_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,0)";
	    /** 
	    * 批量生成条码
	    */
		@Override
	    @Transactional
	    public void batchGenBarcode(String caller,int id,String data,HttpSession session) {
		 List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		 double zxbzs=0;
		 int and_id=0,pa_id=0;
		 double box_zxbzs=0,rest=0,temp=0;
		 String outBox=null;
		 //获取单据时候已过账
		 boolean status = baseDao.checkIf("acceptNotify", "an_id="+id+" and nvl(an_statuscode,' ') <> 'ENTERING'");
		 if(status){
		    		BaseUtil.showError("非在录入状态不允许生成条码");
		    }
		 Object ve_id = baseDao.getFieldDataByCondition("Vendor left join AcceptNotify on an_vendcode=ve_code", "ve_id", "an_id="+id);
		 for (Map<Object, Object> s : gstore) {
		    and_id = Integer.valueOf(s.get("and_id").toString());     //从表id	
		    SqlRowList rs0 = baseDao.queryForRowSet("select an_id,pr_id,an_code,and_id,pr_tracekind,and_detno,and_prodcode,an_vendcode,and_inqty,(and_inqty-nvl(and_barqty,0)) restqty from acceptNotify "
		    			+" left join acceptNotifyDetail on and_anid=an_id left join product on pr_code=and_prodcode where an_id=" + id + " and and_id=" +and_id );
		    if(rs0.next()){
	        	if(s.get("pr_zxbzs") != null && !("").equals(s.get("pr_zxbzs"))){
		    		 temp = Double.valueOf(s.get("pr_zxbzs").toString());
		    		 if(((temp%1)<1 &&(temp%1)>0 )  || temp<=0 ){
		    			 BaseUtil.showError("分装数量错误！");
		    		 }
		    		 zxbzs = temp;  //最小包装数
		    		 int count = baseDao.getCount("select  count(1) cn from  vendprodzxbzs  where vpz_vendcode ='"+ session.getAttribute("ve_code")+"' and vpz_prodcode = '"+s.get("and_prodcode")+"'");
			    		if(count==0){
			    			count = baseDao.getCount("select count(1) cn from product where pr_code ='"+s.get("and_prodcode")+"' and nvl(pr_zxbzs,0) <>"+temp);
			    			if(count > 0){				    				
			    				baseDao.execute("insert into vendprodzxbzs( vpz_vendcode , vpz_prodcode,vpz_zxbzs,vpz_lastupdatetime) values('"+ session.getAttribute("ve_code")+"','"+s.get("and_prodcode")+"','"+temp+"',sysdate)");
			    			}
			    		}else{
			    			count = baseDao.getCount("select count(1) cn from vendprodzxbzs where vpz_prodcode ='"+s.get("and_prodcode") +"'and vpz_vendcode = '"+session.getAttribute("ve_code")+"' and nvl(vpz_zxbzs,0) <>"+temp );
			    			if(count > 0){				    				
			    				baseDao.execute("update vendprodzxbzs set vpz_zxbzs ="+temp+" ,vpz_lastupdatetime = sysdate where vpz_vendcode ='"+ session.getAttribute("ve_code")+"' and vpz_prodcode = '"+s.get("and_prodcode")+"'");
			    			}
			    		}
	    		 }else{
	    			 BaseUtil.showError("分装数量错误!");
	    		 }
		    	 if(s.get("rest") != null && !("").equals(s.get("rest"))){
		    		 temp = Double.valueOf(s.get("rest").toString());
		    		 if(temp<=0 ){
		    			 BaseUtil.showError("本次数量错误！");
		    		 }
		    		 rest =temp;  //本次数量
	    		 }else{
	    			 BaseUtil.showError("本次数量错误！");
	    		 }  	
		    	 if(rs0.getDouble("restqty")< rest){
		    			BaseUtil.showError("本次数量不能大于剩余未生成条码数量");  
		    		}
	    		 if((s.get("pr_boxqty") != null || !("").equals(s.get("pr_boxqty"))) && s.get("pr_boxqty")!="0" ){
	     			  box_zxbzs=Double.valueOf((s.get("pr_boxqty").toString()));  //外箱容量
	     			 if(((box_zxbzs%1)<1 &&(box_zxbzs%1)>0 )|| box_zxbzs<0 ){
		    			 BaseUtil.showError("外箱容量错误！");
		    		 }
	 	    	}else{
	 	    		 BaseUtil.showError("外箱容量错误！");
	 	    	}
	    		 SqlRowList rs = baseDao.queryForRowSet("select wm_concat(and_detno) and_detno from (select max(and_inqty) and_inqty,and_detno,sum(ban_qty) ban_qty,and_id from acceptnotifydetail left join barAcceptnotify on and_id = ban_andid "
	    				 	+" where and_id = "+and_id+" group by and_detno,and_id) where nvl(and_inqty,0) < nvl(ban_qty,0)+"+s.get("rest"));
	    		 if(rs.next() && rs.getString("and_detno") != null){
	    			 BaseUtil.showError("序号"+rs.getString("and_detno")+"已生成数量+本次数量>入库数");
	    		 }
	    			 int bqty = 0;
				     double aqty = 0;
				     Map<Object,Object> store1 = new HashMap<Object,Object>();
		             store1.put("ban_prodid", rs0.getGeneralInt("pr_id"));
		             store1.put("ban_anid",id);
		             store1.put("ban_andid",and_id);
		             store1.put("ban_anddetno",rs0.getGeneralInt("and_detno"));
		             store1.put("ban_ancode",rs0.getString("an_code"));
		             store1.put("ban_prodcode",rs0.getString("and_prodcode"));
		             store1.put("lotcode",s.get("vendercode"));
		             store1.put("ban_vendcode", rs0.getString("an_vendcode"));
					 store1.put("ve_id", ve_id);
					 if(s.get("madedate") != null && !("").equals(s.get("madedate"))){		
		            	 DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		            	 Date date;
		            	 Timestamp time =Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
		            	 try {
							date = inputFormat.parse(s.get("madedate").toString());
							time = Timestamp.valueOf(inputFormat.format(date));
						 } catch (ParseException e) {
							e.printStackTrace();
						 }
		            	 store1.put("ban_madedate", time); 
		             }
					 //（针对条码）
					 Double sum = 0.0;
					 int arlength = 0;
					 double [] mantissa = new double[]{};
					 int count = 0;
					 if (s.get( "mantissapackage" ) != null && !("").equals( s.get( "mantissapackage" ) )) {
			    		 	String str = (String) s.get("mantissapackage");
			    		 	String []ar = str.split(",");
			    		 	arlength = ar.length;
			    		    mantissa = new double[arlength];
			    		 	for(int i=0;i<arlength;i++){
			    		 		Double num = Double.valueOf(ar[i]);
			    		 		if(num <= 0){
			    		        	count++;
			    		        }
			    		 		mantissa[i] = num;
			    		 		sum+=num;
			    		 	};	
			    		 	if(count > 0 ){
			    		 		BaseUtil.showError("尾数分装数量有误,必须大于0");
			    		 	}
			    		 	if((rest-sum) % zxbzs !=0){
			    		 		BaseUtil.showError("本次数量-尾数数量不是分装数量的整数倍!");
			    		 	}
			    		 }
					 rest = rest - sum;
		             bqty = (int) (rest / zxbzs); //本次数量除以最小包装数，最小包装数件数
		             aqty = (new BigDecimal(Double.toString(rest))).subtract(new BigDecimal(
		                        Double.toString(NumberUtil.mul(Double.valueOf(bqty), zxbzs)))).doubleValue(); //取余数，相当于pd_qty- (num*pr_zxbzs) 			       			            
		            //如果外箱容量不存在，则只插入条码即可
					 if(box_zxbzs == 0 || box_zxbzs == -1){
						if (bqty >= 1) {//先将整的插入barcodeio
			                for (int i = 0; i < bqty; i++) {
		                    	 insertBar(caller,store1,zxbzs);  
		                    	 store1.put("ve_id", ve_id);
			                }
			            }
						if(arlength>0){
							for(int i=0;i<arlength;i++){//如果存在尾箱，就将解析的尾箱数赋值
			    		 		insertBar(caller,store1,mantissa[i]); 
								store1.put("ve_id", ve_id);
			    		 	}
						}else if (aqty > 0) {//没有尾箱，则将余数赋值
						        insertBar(caller,store1,aqty); 
							 	store1.put("ve_id", ve_id);
						} 	 
					}else{
						double temzxbzs = zxbzs;
						if(arlength>0){ //如果尾箱数存在，则增加
							 bqty += arlength;
						}else{
							 if(aqty > 0){//如果没有尾箱数，增长1就可以了，装不满一箱
					        	 bqty +=1;
					         }
						}
				         for(int i=0;i<bqty;i++){
				        	 if(i%box_zxbzs == 0){//整箱
				        		 outBox = verifyApplyDao.outboxMethod(rs0.getString("pr_id"),"2");
				        	 }
				        	 if(arlength>0 && i>=bqty-arlength){//存在尾箱数
				        		 temzxbzs = mantissa[i+arlength-bqty];
				        	 }else{ //不存在尾箱数剩余的装不满一箱
					        	 if( i+1 == bqty && aqty>0){//
					        		 temzxbzs = aqty;
					        	 }
				        	 }
			        	 	insertBarPd(caller,pa_id,outBox,temzxbzs,store1);
							store1.put("ve_id", ve_id);
				         }
					}	
				//更新通知单的已生成数
		        baseDao.execute("update AcceptNotifyDetail set and_barqty=(select NVL(sum(nvl(ban_qty,0)),0)from baracceptnotify where ban_andid=and_id )" +
			        				" where  and_anid=" + id+"and and_id="+and_id );		 
		    }
		 }
		}

	    //清除明细
	    @Override
	    @Transactional
	    public void deleteAllBarDetails(String caller, Integer an_id,String biids) {
	    	 boolean status = baseDao.checkIf("acceptNotify", "an_id="+an_id+" and nvl(an_statuscode,' ') <> 'ENTERING'");
			 if(status){
			    		BaseUtil.showError("非在录入状态不允许删除条码");
			    }
	        if(biids == null || "".equals(biids)){  //没有值全部删除    
	        	int count = baseDao.getCount("select count(1) cn from BarAcceptNotify where ban_anid="+an_id);
	        	if(count ==0){
	        		BaseUtil.showError("没有数据不需要清除");
	        	}
	        	baseDao.deleteByCondition("BarAcceptNotify","ban_anid=" + an_id);
	        	baseDao.logger.others("清除通知单条码数据", "清除成功,删除"+count+"条", caller,"an_id",an_id);
	        }else{//有值勾选删除
	        	 String[] array = biids.split(",");
	        	 int length = array.length;
	        	 for(int i=0;i<length; i++){
	        		 baseDao.deleteByCondition("BarAcceptNotify","ban_id = '"+array[i]+"'");
	        		 baseDao.logger.others("清除通知单条码数据", "清除成功,ban_id ="+array[i], caller, "ban_id", array[i]);  
	        	 }       	 
	        	}
	        //更新通知单的已生成数
	        baseDao.execute("update AcceptNotifyDetail set and_barqty=(select NVL(sum(nvl(ban_qty,0)),0)from baracceptnotify where ban_andid=and_id )" +
		        				" where  and_anid=" + an_id);
	      }
	    
	    private void insertBar(String caller, Map<Object, Object> store,Double inqty) {
	        String bar_code;
	        Object ve_id = store.get("ve_id");
	        if ((ve_id != null) && (ve_id != "")) {
	            bar_code = verifyApplyDao.barcodeMethod(store.get("ban_prodcode").toString(),ve_id.toString(),0);
	        } else {
	            bar_code = verifyApplyDao.barcodeMethod(store.get("ban_prodcode").toString(), "",0);
	        }
	        int ban_id = baseDao.getSeqId("BARACCEPTNOTIFY_SEQ");
	        //生成BARCODEIO		
	        store.put("ban_id", ban_id);
	        store.put("ban_barcode", bar_code);
	        store.put("ban_printstatus", "0");
	        store.remove("ve_id");
	        store.put("ban_qty", inqty);
	        String formSql = SqlUtil.getInsertSqlByFormStore(store, "BarAcceptNotify",
	                new String[] {  }, new Object[] {  });
	        baseDao.execute(formSql);
	    }
	    
	    private void insertBarPd(String caller, int pa_id, String out_boxcode,
	            Double qty,Map<Object, Object> store) {
	            List<String> sqls = new ArrayList<String>();   
	            String bar_code;
	            Object ve_id = store.get("ve_id");
	            if ((ve_id != null) && (ve_id != "")) {
	                bar_code = verifyApplyDao.barcodeMethod(store.get("ban_prodcode").toString(),ve_id.toString(),0);
	            } else {
	                bar_code = verifyApplyDao.barcodeMethod(store.get("ban_prodcode").toString(), "",0);
	            }
	            int ban_id = baseDao.getSeqId("BARACCEPTNOTIFY_SEQ");
	            //生成BARCODEIO		
	            store.put("ban_id", ban_id);
	            store.put("ban_barcode", bar_code);
	            store.put("ban_printstatus", "0");
	            store.put("ban_qty", qty);
	            store.remove("ve_id");
	            String formSql = SqlUtil.getInsertSqlByFormStore(store, "BarAcceptNotify",
	                    new String[] {}, new Object[] {});
	            baseDao.execute(formSql);
	            sqls.add("update BarAcceptNotify set ban_outboxcode='" + out_boxcode +
	                "',ban_outboxid=" + pa_id + " where ban_id=" + ban_id);
	            baseDao.execute(sqls);
	        }
	    //批量转送货通知单
		@Override
		@Transactional
		public String vastTurnAccptNotify(String caller, String data,HttpSession session) {
			List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
			Object vendcode = session.getAttribute("ve_code");
			 double box_zxbzs=0,rest=0,temp=0,zxbzs=0;
	    	 String outBox=null;
			Map<Object, List<Map<Object, Object>>> groupM = BaseUtil.groupsMap(gridStore, new String[] { "PU_RECEIVECODE", "PU_CURRENCY",
					"PU_PAYMENTSCODE", "PU_BUYERCODE" });
			StringBuilder sb = new StringBuilder();
			List<String> sqls = new ArrayList<String>();
			List<Map<Object, Object>> items;
			int detno = 0;
				for (Object key : groupM.keySet()) {	
					items = groupM.get(key);
					SqlRowList rs = baseDao.queryForRowSet("select pu_currency,max(pu_rate) pu_rate,pu_receivecode,max(pu_receivename) pu_receivename,pu_paymentscode,max(pu_payments) pu_payments,pu_buyercode,max(pu_buyername) pu_buyername,max(pu_buyerid) pu_buyerid "
							+" from purchase where pu_receivecode=? and pu_currency =? and pu_paymentscode=? and pu_buyercode =? group by pu_receivecode,pu_currency,pu_paymentscode,pu_buyercode",items.get(0).get("PU_RECEIVECODE"),items.get(0).get("PU_CURRENCY"),items.get(0).get("PU_PAYMENTSCODE"),items.get(0).get("PU_BUYERCODE"));
					if(rs.next()){
					int id = baseDao.getSeqId("AcceptNotify_seq");
					String code = baseDao.sGetMaxNumber("VendAcceptNotify", 2);
					detno = 1;
					SqlRowList vendor = baseDao.queryForRowSet("select ve_name,ve_id from vendor where ve_code = ?",vendcode);
					if(vendor.next()){
					for (Map<Object, Object> map : groupM.get(key)) {
						SqlRowList rs2 = baseDao.queryForRowSet("select wm_concat(distinct pu_code) pu_code from purchasedetail left join purchase on pd_puid= pu_id where pd_id = ? and pu_statuscode <>'AUDITED'",map.get("PD_ID"));
						if(rs2.next() && rs2.getString("pu_code") != null){
							BaseUtil.showError("采购单: "+rs2.getString("pu_code")+"不是审核状态");
						}
						rs2 = baseDao.queryForRowSet("select wm_concat(distinct pu_code) pu_code from purchasedetail left join purchase on pd_puid= pu_id where pd_id = ? and pd_status ='已结案'",map.get("PD_ID"));
						if(rs2.next() && rs2.getString("pu_code") != null){
							BaseUtil.showError("采购单: "+rs2.getString("pu_code")+"存在已结案明细");
						}
						rs2 = baseDao.queryForRowSet("select wm_concat(distinct pu_code) pu_code from purchasedetail left join purchase on pd_puid= pu_id where pd_id =? and pu_vendcode <>'"+vendcode+"'",map.get("PD_ID"));
						if(rs2.next() && rs2.getString("pu_code") != null){
							BaseUtil.showError("采购单: "+rs2.getString("pu_code")+"的供应商与当前供应商"+vendcode+"不一致");
						}
						rs2 = baseDao.queryForRowSet("select wm_concat(distinct pd_code) pu_code from purchasedetail left join scm_purchaseturnqty_view on pd_id = v_pd_id "
		                    +" where pd_id = "+map.get("PD_ID")+" and nvl(pd_qty,0)-nvl(v_pd_turnacceptnotify,0)-nvl('"+map.get("PD_TQTY")+"',0) <0");
						if(rs2.next() && rs2.getString("pu_code") != null){					
							BaseUtil.showError("采购单 :"+rs2.getString("pu_code")+"本次数量大于采购数量-已转送货数量-已投放送货通知提醒数量");
						}
						if(map.get("UNITPACKAGE") != null && !("").equals(map.get("UNITPACKAGE"))){
				    		 temp = Double.valueOf(map.get("UNITPACKAGE").toString());
				    		 if(((temp%1)<1 &&(temp%1)>0 )  || temp<0 ){
				    			 BaseUtil.showError("采购单"+map.get("PD_CODE")+",序号"+map.get("PD_DETNO")+"分装数量错误!");
				    		 }
				    		 zxbzs = temp;  //最小包装数
				    		 //  vendcode    
				    		int count = baseDao.getCount("select  count(1) cn from  vendprodzxbzs  where vpz_vendcode ='"+vendcode+"' and vpz_prodcode = '"+map.get("PD_PRODCODE")+"'");
				    		if(count==0){
				    			count = baseDao.getCount("select count(1) cn from product where pr_code ='"+map.get("PD_PRODCODE")+"' and nvl(pr_zxbzs,0) <>"+temp);
				    			if(count > 0){				    				
				    				baseDao.execute("insert into vendprodzxbzs( vpz_vendcode , vpz_prodcode,vpz_zxbzs,vpz_lastupdatetime) values('"+vendcode+"','"+map.get("PD_PRODCODE")+"','"+temp+"',sysdate)");
				    			}
				    		}else{
				    			count = baseDao.getCount("select count(1) cn from vendprodzxbzs where vpz_prodcode ='"+map.get("PD_PRODCODE") +"'and vpz_vendcode = '"+vendcode+"' and nvl(vpz_zxbzs,0) <>"+temp );
				    			if(count > 0){				    				
				    				baseDao.execute("update vendprodzxbzs set vpz_zxbzs ="+temp+" ,vpz_lastupdatetime = sysdate where vpz_vendcode ='"+vendcode+"' and vpz_prodcode = '"+map.get("PD_PRODCODE")+"'");
				    			}
				    		}
			    		 }
						if(map.get("PD_TQTY") != null && !("").equals(map.get("PD_TQTY"))){
				    		 temp = Double.valueOf(map.get("PD_TQTY").toString());
				    		 if(temp<=0 ){
				    			 BaseUtil.showError("采购单"+map.get("PD_CODE")+",序号"+map.get("PD_DETNO")+"本次数量错误！");
				    		 }
				    		 rest =temp;  //本次数量
			    		 }else{
			    			 BaseUtil.showError("采购单"+map.get("PD_CODE")+",序号"+map.get("PD_DETNO")+"本次数量错误!");
			    		 }  
						 if((map.get("BOXQTY") != null && !("").equals(map.get("BOXQTY"))) && map.get("BOXQTY")!="0"  ){
			     			  box_zxbzs=Double.valueOf((map.get("BOXQTY").toString()));  //外箱容量
			     			 if(((box_zxbzs%1)<1 &&(box_zxbzs%1)>0 )|| box_zxbzs<0 ){
				    			 BaseUtil.showError("采购单"+map.get("PD_CODE")+",序号"+map.get("PD_DETNO")+"外箱容量错误！");
				    		 }
						 }
						int and_id = baseDao.getSeqId("AcceptnotifyDetail_seq");
						sqls.add("insert into AcceptnotifyDetail (and_id,and_anid,and_detno,and_ordercode,and_orderdetno,and_prodcode,and_inqty,and_price,and_orderid,and_taxrate,and_beipin ) values "
											+ " ("+and_id+","+id+","+detno+",'"+map.get("PD_CODE")+"',"+map.get("PD_DETNO")+",'"+map.get("PD_PRODCODE")+"',"+map.get("PD_TQTY")+","+map.get("PD_PRICE")+","+map.get("PD_ID")+","+map.get("PD_RATE")+","+map.get("BEIPINQTY")+")");
						String bar_code;
			            Object ve_id = vendor.getString("ve_name");
			           /* if ((ve_id != null) && (ve_id != "")) {
			                bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(),ve_id.toString(),0);
			            } else {*/
			               /* bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(), "",0);*/
			            /*}*/
			            int pr_id = 0;
			            SqlRowList rs1 = baseDao.queryForRowSet("select pr_id from product where pr_code = ?",map.get("PD_PRODCODE"));
			            if(rs1.next()){
			            	pr_id = rs1.getInt("pr_id");
			            }else{
			            	BaseUtil.showError("物料"+map.get("PD_PRODCODE")+"不存在");
			            }
			             Double sum = 0.0;
						 int arlength = 0;
						 double [] mantissa = new double[]{};
			            if (map.get( "MANTISSAPACKAGE" ) != null && !("").equals( map.get( "MANTISSAPACKAGE" ) ) && map.get("UNITPACKAGE") != null && !("").equals(map.get("UNITPACKAGE"))) {
			    		 	String str = map.get("MANTISSAPACKAGE").toString();
			    		 	String []ar = str.split(",");
			    		 	arlength = ar.length;
			    		    mantissa = new double[arlength];
			    		    int count = 0;
			    		 	for(int i=0;i<arlength;i++){
			    		 		Double num = Double.valueOf(ar[i]);
			    		        if(num <= 0){
			    		        	count++;
			    		        }
			    		 		mantissa[i] = num;
			    		 		sum+=num;
			    		 	};	
			    		 	if(count > 0 ){
			    		 		BaseUtil.showError("尾数分装数量有误,必须大于0");
			    		 	}
			    		 	if((rest-sum) % zxbzs !=0 && sum>0){
			    		 		BaseUtil.showError("本次数量-尾数数量不是分装数量的整数倍!");
			    		 	}
			    		 }
			             int bqty = 0;
					     double aqty = 0;
			             rest = rest - sum;
			             bqty = (int) (rest / zxbzs); //本次数量除以最小包装数，最小包装数件数
			             aqty = (new BigDecimal(Double.toString(rest))).subtract(new BigDecimal(
			                        Double.toString(NumberUtil.mul(Double.valueOf(bqty), zxbzs)))).doubleValue(); //取余数，相当于pd_qty- (num*pr_zxbzs)
			               //如果外箱容量不存在，则只插入条码即可
			             if(map.get("UNITPACKAGE") != null && !("").equals(map.get("UNITPACKAGE")) && Integer.valueOf(map.get("UNITPACKAGE").toString())>0){
			            	 if(box_zxbzs == 0 || box_zxbzs == -1){
									if (bqty >= 1) {
						                for (int i = 0; i < bqty; i++) {
						                	bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(), "",0);
						                	sqls.add("insert into baracceptnotify(ban_id,ban_barcode,ban_anid,ban_ancode,ban_andid,ban_anddetno,ban_prodcode,ban_prodid,ban_qty,ban_vendcode) "
													+ " values (baracceptnotify_seq.nextval,'"+bar_code+"',"+id+","+code+","+and_id+","+detno+",'"+map.get("PD_PRODCODE")+"',"+pr_id+","+zxbzs+",'"+vendcode+"')");
						                }
						            }
									if(arlength>0){
										for(int i=0;i<arlength;i++){//如果存在尾箱，就将解析的尾箱数赋值
											bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(), "",0);
											sqls.add("insert into baracceptnotify(ban_id,ban_barcode,ban_anid,ban_ancode,ban_andid,ban_anddetno,ban_prodcode,ban_prodid,ban_qty,ban_vendcode) "
													+ " values (baracceptnotify_seq.nextval,'"+bar_code+"',"+id+","+code+","+and_id+","+detno+",'"+map.get("PD_PRODCODE")+"',"+pr_id+","+mantissa[i]+",'"+vendcode+"')");
						    		 	}
									}else if (aqty > 0) {//没有尾箱，则将余数赋值
										bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(), "",0);
										sqls.add("insert into baracceptnotify(ban_id,ban_barcode,ban_anid,ban_ancode,ban_andid,ban_anddetno,ban_prodcode,ban_prodid,ban_qty,ban_vendcode) "
												+ " values (baracceptnotify_seq.nextval,'"+bar_code+"',"+id+","+code+","+and_id+","+detno+",'"+map.get("PD_PRODCODE")+"',"+pr_id+","+aqty+",'"+vendcode+"')");
									} 	 
								}else{
									double temzxbzs = zxbzs;
									if(arlength>0){ //如果尾箱数存在，则增加
										 bqty += arlength;
									}else{
										 if(aqty > 0){//如果没有尾箱数，增长1就可以了，装不满一箱
								        	 bqty +=1;
								         }
									}
							         for(int i=0;i<bqty;i++){
							        	 if(i%box_zxbzs == 0){//整箱
							        		 outBox = verifyApplyDao.outboxMethod(rs1.getString("pr_id"),"2");
							        	 }
							        	 if(arlength>0 && i>=bqty-arlength){//存在尾箱数
							        		 temzxbzs = mantissa[i+arlength-bqty];
							        	 }else{ //不存在尾箱数剩余的装不满一箱
								        	 if( i+1 == bqty && aqty>0){//
								        		 temzxbzs = aqty;
								        	 }
							        	 }
							        	 bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(), "",0);
							        	 sqls.add("insert into baracceptnotify(ban_id,ban_barcode,ban_anid,ban_ancode,ban_andid,ban_anddetno,ban_prodcode,ban_prodid,ban_qty,ban_vendcode,ban_outboxcode) "
													+ " values (baracceptnotify_seq.nextval,'"+bar_code+"',"+id+","+code+","+and_id+","+detno+",'"+map.get("PD_PRODCODE")+"',"+pr_id+","+temzxbzs+",'"+vendcode+"','"+outBox+"')");
							         }
								}
			             }else{			 
			            	 //  20180528  如果分装数量为0或者为空   不生成条码
			            	/* bar_code = verifyApplyDao.barcodeMethod(map.get("PD_PRODCODE").toString(), "",0);
			            	 sqls.add("insert into baracceptnotify(ban_id,ban_barcode,ban_anid,ban_ancode,ban_andid,ban_anddetno,ban_prodcode,ban_prodid,ban_qty,ban_vendcode) "
			            			 + " values (baracceptnotify_seq.nextval,'"+bar_code+"',"+id+","+code+","+and_id+","+detno+",'"+map.get("PD_PRODCODE")+"',"+pr_id+","+map.get("PD_TQTY")+",'"+vendcode+"')");*/
			             }
						
						//更新通知单的已生成数
			             if(map.get("MADEDATE") != null && !("").equals(map.get("MADEDATE"))){	
			            	 sqls.add("update baracceptnotify set ban_madedate = to_date('"+map.get("MADEDATE").toString().replace("T"," ")+"','yyyy-MM-dd hh24:mi:ss') where ban_andid = "+and_id);
			             }
			             if(map.get("LOTCODE") != null && !("").equals(map.get("LOTCODE"))){
					 	 sqls.add("update baracceptnotify set lotcode = '"+map.get("LOTCODE")+"' where ban_andid = "+and_id);
			             }
				        sqls.add("update AcceptNotifyDetail set and_barqty=(select NVL(sum(nvl(ban_qty,0)),0)from baracceptnotify where ban_andid=and_id )" +
					        				" where  and_anid=" + id+" and and_id="+and_id );
						detno++;
					}
					    sqls.add("insert into acceptnotify(an_id,an_code,an_vendcode,an_vendname,an_buyerid,an_buyer,an_payment,an_paymentcode, an_currency,an_rate,an_status,an_statuscode,an_recorder,an_indate,an_source,an_date,an_receivename) "
									+ " values("+id+",'"+code+"','"+vendcode+"','"+vendor.getString("ve_name")+"','"+rs.getString("pu_buyerid")+"','"+rs.getString("pu_buyername")+"','"+rs.getString("pu_payments")+"',' "
									+ rs.getString("pu_paymentscode")+"','"+rs.getString("pu_currency")+"','"+rs.getString("pu_rate")+"','在录入','ENTERING','"+session.getAttribute("em_name")+"@vendor',sysdate,'VENDOR',sysdate,'"+rs.getString("pu_receivename")+"')");
					    sb.append( "<br/>转入成功,送货通知单:<a href=\"javascript:openUrl('jsps/vendbarcode/vendAcceptNotify.jsp?formCondition=an_idIS"
								+ id + "&gridCondition=and_anidIS" + id + "&whoami=VendAcceptNotify')\">" + code + "</a>&nbsp;<hr/>");
				}else{
					 BaseUtil.showError("请检查当前供应商"+vendcode+"是否存在");
				}
				}
			}
				baseDao.execute(sqls);
			return sb.toString();
		}
		@Override
		public Map<String, Object> getAcceptNotifyListDetail(Integer page,String condition, Integer start, Integer pageSize,
				String vendcode) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			Object total;
			if(condition == null || ("").equals(condition)){
				condition = " 1=1";
			}
			int start1 = ((page - 1) * pageSize + 1);
			int end = page * pageSize;
			total = baseDao.getFieldDataByCondition("acceptnotifydetail left join acceptnotify "
							+"on and_anid = an_id left join purchasedetail on pd_code = and_ordercode and and_orderdetno = pd_detno", "count(1)", " an_vendcode='"+vendcode+"' and "+condition);
			SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select AN_ID,AN_CODE,to_char(AN_DATE,'yyyy-MM-dd') AN_DATE,AN_STATUS,AN_SENDCODE,AN_VENDCODE,"
							+"AN_VENDNAME,AN_RECORDER,to_char(AN_INDATE,'yyyy-MM-dd') AN_INDATE,and_ordercode,and_orderdetno,and_prodcode,and_inqty from acceptnotifydetail left join acceptnotify"
							+" on and_anid = an_id  where an_vendcode=?  and "+condition+"order by an_id desc)tt "
					 +" where rownum<=? )where rn>=?",vendcode,end,start1);
			if(rs.next()){			
				modelMap.put("datas", rs.getResultList());
				modelMap.put("total", total);
			}
			return modelMap;
		}

}
