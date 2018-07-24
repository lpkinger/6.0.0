package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaInMaterialService;



@Service("pdaInMaterialServiceImpl")
public  class PdaInMaterialServiceImpl implements PdaInMaterialService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;

	/**
	 * 针对输入的入库单号，和仓库进行模糊查询
	 */
	@Override
	public List<Map<String, Object>> fuzzySearch(String inoutNo, String whcode) {
		SqlRowList rs;
		inoutNo = inoutNo.toUpperCase();
		//不考虑加仓库编号模糊查询，速度太慢，优化也要8秒
		rs = baseDao
				.queryForRowSet("select * from (select  pi_inoutno from prodinout left join documentsetup  on pi_class=ds_name where pi_inoutno like ? "
						+ "  and (ds_inorout = 'IN' OR ds_inorout = '-OUT') order by pi_id desc) where rownum<=10","%"+inoutNo+"%");	
		if (rs.next()) {
			return rs.getResultList();
		}
		return null;
	}

	/**
	 * 获取入库单据的数据
	 */
	@Override
	public List<Map<String, Object>> getProdIn(String inoutNo, String whcode) {
		return pdaCommonDao.getProdInOut("pd_inqty", inoutNo, whcode);
	}

	@Override
	public Map<String,Object> getNeedGetList(Integer id, String whcode) {
		 Map<String,Object> map = new HashMap<String,Object>();
		Object ob = baseDao.getFieldDataByCondition("prodinout", "pi_id", "pi_id="+id);
		if(ob == null){
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"单据不存在");
		}
		String sql = " select count(1)cn from prodiodetail left join product on pr_code=pd_prodcode where pd_piid=? and pd_whcode=? and pr_tracekind>0 and nvl(pd_inqty,0)>nvl(pd_barcodeinqty,0)";
		SqlRowList rs = baseDao.queryForRowSet(sql,id,whcode);
		if(rs.next() && rs.getInt("cn")>0){
			map.put("message", "success");
		}else{
			map.put("message", "没有需要采集的明细，或已经采集完成");
		}
		sql = "select pd_prodcode,pd_restqty,pr_detail,pr_spec,pr_tracekind,case when pr_tracekind=1 then 1 else pr_zxbzs end pr_zxbzs,pr_ifbarcodecheck,pd_piid,pd_inoutno,pd_whcode,pr_id from (select pd_prodcode,sum(pd_inqty)- nvL(sum(pd_barcodeinqty),0) pd_restqty,pd_inoutno,pd_whcode,pd_piid from prodiodetail where pd_piid=? and pd_whcode=? group by pd_piid,pd_inoutno,pd_whcode,pd_prodcode ) T left join product on pr_code=pd_prodcode where pr_tracekind>0 ";
		rs = baseDao.queryForRowSet(sql,id,whcode);
		if(rs.next()){
			map.put("data", rs.getResultList());
		}else{
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"没有需要采集的明细，或已经采集完成");
		}
		return map;
	}

	@Override
	public String saveBarcode(String data) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		Object bi_piid = map.get("bi_piid");
		int  cn = baseDao.getCount("select count(1) from prodinout where pi_id="+bi_piid+" and nvl(pi_pdastatus,'未入库')<>'已入库'");			
		if (cn == 0) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据必须是未入库的状态");
		}	
		
		SqlRowList rs;
		String bi_whcode;
		StringBuffer str = new StringBuffer(); //返回数据
		StringBuffer sql = new StringBuffer(); //更新语句
		
		Object bi_barcode = map.get("re_barcode");
		Object bi_outboxcode = map.get("re_outboxcode"),
			   bi_prodcode = map.get("re_prodcode");
			   /*bi_whcode = map.get("bi_whcode");*/
	    Object re_vendbarcode =map.get("re_vendbarcode");
	    Object re_madedate = map.get("re_madedate");
	    Object re_oldbarcode = map.get("re_oldbarcode");
	    Object re_location = map.get("re_location");
	    
	    if (bi_barcode != null && !"".equals(bi_barcode)) {	       	
	    	rs = baseDao.queryForRowSet("select bi_id,pr_code,bi_whcode from barcodeio left join product on pr_code=bi_prodcode where bi_barcode=? and bi_piid=?  and nvl(bi_status,0)=0 and pr_statuscode='AUDITED'",bi_barcode,bi_piid);
			if(rs.next()){
				bi_whcode = rs.getString("bi_whcode");
				str.append("条码:"+bi_barcode+"采集成功").append(".");
				sql.append("update barcodeio set ");
				if(re_vendbarcode != null && !"".equals(re_vendbarcode)){//lotno
					sql.append("bi_vendbarcode='"+re_vendbarcode+"', ");
					str.append("LotNo:"+re_vendbarcode).append(".");
				}
				
				if(re_madedate != null && !"".equals(re_madedate)){//生产日期
					sql.append("bi_made = '"+re_madedate +"',");
					String IS_DATE = "((((19|20)\\d{2})-(0?(1|[3-9])|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))$";
					String IS_DATE1 = "((((19|20)\\d{2})/(0?(1|[3-9])|1[012])/(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})/(0?[13578]|1[02])/31)|(((19|20)\\d{2})/0?2/(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))/0?2/29))$";
				    String IS_DATE2 = "((((19|20)\\d{2})(0?(1|[3-9])|1[012])(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})(0?[13578]|1[02])31)|(((19|20)\\d{2})(0?2)(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))(0?2)29))$";
					if((((String)re_madedate).matches(IS_DATE) ||((String)re_madedate).matches(IS_DATE1) || ((String)re_madedate).matches(IS_DATE2)) && ((String)re_madedate).length()>7 ){
						sql.append("bi_madedate=to_date('"+re_madedate+"','yyyy-mm-dd'), ");
					}else{
						sql.append("bi_madedate='', ");
					}
					str.append("DateCode:"+re_madedate).append(".");
				}
				
				if(bi_prodcode != null && !"".equals(bi_prodcode)){//料号校验	
					if(bi_prodcode.equals(rs.getString("pr_code"))){
						sql.append("bi_ifcheck=-1, ");
						str.append("物料校验成功.");
					}else{
						sql.append("bi_ifcheck=0, ");
						str.append("物料校验失败.");
					}					
				}
				
				if(re_oldbarcode != null && !"".equals(re_oldbarcode)){//绑定旧条码
					SqlRowList rs1 = baseDao.queryForRowSet("select bar_prodcode from barcode where bar_code=?",re_oldbarcode);
					if(rs1.next()){
						if(rs.getString("pr_code").equals(rs1.getString("bar_prodcode"))){//判断旧条码料号与新条码料号必须是一致的
							sql.append("bi_oldbarcode='"+re_oldbarcode+"', ");
							str.append("成功绑定旧条码"+re_oldbarcode+",");
						}else{
							str.append("绑定旧条码失败，旧条码对应的物料与现条码物料必须一致.");
						}
					}else{
					    str.append("绑定旧条码失败，旧条码不存在.");
					}
				}
				
				if(re_location != null && !"".equals(re_location)){//绑定仓位
					sql.append("bi_location='"+re_location+"', ");
					str.append("绑定仓位成功,").append("仓位:"+re_location+".");
				}
				
				sql.append("bi_pdaget=1 where bi_id="+rs.getInt("bi_id"));
			    baseDao.execute(sql.toString());	
			}else{
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码不属于此入库单或者已经确认入库不允许修改数据");
			}							
	    }else if (bi_outboxcode != null && !"".equals(bi_outboxcode)) { //箱号
			rs = baseDao.queryForRowSet("select * from barcodeio where bi_outboxcode=? and bi_piid=? and nvl(bi_status,0)=0",bi_outboxcode,bi_piid);
			if(rs.next()){
				bi_whcode = rs.getString("bi_whcode");
				str.append("箱号:"+bi_outboxcode+"采集成功").append(".");
				sql.append("update barcodeio set ");
				if(re_vendbarcode != null && !"".equals(re_vendbarcode)){
					sql.append("bi_vendbarcode='"+re_vendbarcode+"', ");
					str.append("LotNo:"+re_vendbarcode).append(".");
				}
				if(re_madedate != null && !"".equals(re_madedate)){	//生产日期
					sql.append("bi_made = '"+re_madedate +"',");
					String IS_DATE = "((((19|20)\\d{2})-(0?(1|[3-9])|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))$";
					String IS_DATE1 = "((((19|20)\\d{2})/(0?(1|[3-9])|1[012])/(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})/(0?[13578]|1[02])/31)|(((19|20)\\d{2})/0?2/(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))/0?2/29))$";
				    String IS_DATE2 = "((((19|20)\\d{2})(0?(1|[3-9])|1[012])(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})(0?[13578]|1[02])31)|(((19|20)\\d{2})(0?2)(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))(0?2)29))$";
					if((((String)re_madedate).matches(IS_DATE) ||((String)re_madedate).matches(IS_DATE1) || ((String)re_madedate).matches(IS_DATE2)) && ((String)re_madedate).length()>7 ){
						sql.append("bi_madedate=to_date('"+re_madedate+"','yyyy-mm-dd'), ");
					}else{
						sql.append("bi_madedate='', ");
					}
					str.append("DateCode:"+re_madedate).append(".");
				/*//生产日期
					sql.append("bi_madedate=to_date('"+re_madedate+"','yyyy-mm-dd'), ");
					str.append("DateCode:"+re_madedate).append(".");*/
				}
				
				if(bi_prodcode != null && !"".equals(bi_prodcode)){
					sql.append("bi_ifcheck=-1, ");;
				}
				if(re_oldbarcode != null && !"".equals(re_oldbarcode)){
					SqlRowList rs1 = baseDao.queryForRowSet("select bar_code from barcode where bar_code=? and bar_prodcode=?",re_oldbarcode,bi_prodcode);
					if(rs1.next()){
						sql.append("bi_oldbarcode='"+re_oldbarcode+"', ");
						str.append("成功绑定旧条码:"+re_oldbarcode+",");
					}else{
					    str.append("绑定旧条码失败，不存在或者旧条码与新条码物料不一致.");
					}
				}
				if(re_location != null && !"".equals(re_location)){//绑定仓位
					sql.append("bi_location='"+re_location+"', ");
					str.append("绑定仓位成功,");
					str.append("仓位:"+re_location+".");
				}
				sql.append(" bi_pdaget=1 where bi_piid="+bi_piid+ " and bi_outboxcode ='"+bi_outboxcode+"' and bi_whcode='"+bi_whcode+"'");
				baseDao.execute(sql.toString());
			}else{
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号不属于此入库单或者已经确认入库不允许修改数据");
			}	
	    }else{
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"请传递必要数据条码号或者箱号");
		}
		return str.toString();
	}

/*	@Override
	public void deleteDetail(Integer bi_piid, String barcode, String outboxcode, String whcode) {
		Object ob = baseDao.getFieldDataByCondition("prodinout ", "pi_statuscode", "pi_id=" + bi_piid);
		if (ob != null) {
			if (ob.toString().equals("POSTED")) {
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该单据已过帐，不允许删除已采集数据!");
			}
			ob = baseDao.getFieldDataByCondition("barcodeio", "bi_prodcode", "bi_barcode='" + barcode + "'");
			if (ob == null) {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的条码：" + barcode + "未采集!");
			}
			Object[] obs = null;
			if (barcode != null) {
				obs = baseDao.getFieldsDataByCondition("barcodeIo", new String[] { "bi_pdaget", "bi_inqty" }, "bi_piid="
						+ bi_piid + " and bi_barcode='" + barcode + "' and bi_whcode='" + whcode + "'");
				if (obs != null) {
					if(obs[0] == null){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的条码,请在ERP中操作！");
					}else{
					if (Integer.valueOf(obs[0].toString())!=1){
					}
					baseDao.deleteByCondition("barcodeIo", "bi_piid=" + bi_piid + " and bi_barcode='" + barcode + "'"+"and bi_whcode='"+whcode+"'");
					}
				}
			} else if (outboxcode != null) {
				obs = baseDao.getFieldsDataByCondition("barcodeIo", new String[] { "bi_pdaget", "bi_inqty" }, "bi_piid="
						+ bi_piid + " and bi_barcode='" + outboxcode + "' and bi_whcode='" + whcode + "'");
				if (obs != null) {
					if (Integer.valueOf(obs[0].toString())!=1){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"PDA不能删除从ERP中生成的箱号,请在ERP中操作！");
					}
					baseDao.deleteByCondition("barcodeIo", "bi_piid=" + bi_piid + " and bi_outboxcode='" + outboxcode + "'"+"and bi_whcode='"+whcode+"'");
				}
			}
			if (obs != null) {
				double bi_inqty = Double.valueOf(obs[1].toString());
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select pd_id,pd_barcodeinqty from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=? and nvl(pd_barcodeinqty,0)>0 order by pd_pdno asc ,pd_barcodeinqty desc",
								bi_piid, ob.toString(), whcode);
				while (rs.next()) {
					if (bi_inqty > 0) {
						if (bi_inqty > rs.getDouble("pd_barcodeinqty")) {
							baseDao.execute("update prodiodetail set PD_BARCODEINQTY =0  where pd_id=" + rs.getInt("pd_id"));
							bi_inqty = NumberUtil.sub(bi_inqty, rs.getDouble("pd_barcodeinqty"));
						} else {
							baseDao.execute("update prodiodetail set PD_BARCODEINQTY = NVL(pd_barcodeinqty,0) -" + bi_inqty
									+ " where pd_id=" + rs.getInt("pd_id"));
							bi_inqty = 0;
						}
					}
				}
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"需要删除的条码未采集或者未采集至仓库[" + whcode + "]中!");
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该单据不存在或者已删除!");
		}
	}*/

	@Override
	public List<Map<String, Object>> getCheckProdIn(String inoutNo, String whcode) {
		//获取需要入库抽检校验的数据
		return pdaCommonDao.getCheckProdInOut("pd_inqty", inoutNo, whcode);
	}

	@Override   //确认入库
	public String confirmIn(Integer bi_piid, String whcode) {
		String pi_inoutno=null;
		String pi_class=null;
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="
				+ bi_piid + " and pd_status= 99 and nvl(pi_pdastatus,'未入库')<>'已入库'");
		if (cn == 0) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据必须是在已过账未确认入库的状态");
		}
		SqlRowList rs=baseDao.queryForRowSet("select pi_class,pi_inoutno from prodinout where pi_id="+bi_piid);
		int cn2 = baseDao.getCount("select count(1) from barcodeio where bi_piid="+bi_piid);
		if(cn2<=0){
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"没有已采集的信息不允许确认入库");
		}
		if(rs.next()){
			pi_class=rs.getString("pi_class");
			pi_inoutno=rs.getString("pi_inoutno");
		}
		String res=baseDao.callProcedure("sp_barcodeio_in",new Object[] {pi_class, pi_inoutno,""});
		if (res != null && !("").equals(res.trim())) {
			BaseUtil.showError(res);
		}else{
			rs=baseDao.queryForRowSet("select ds_table from documentsetup where ds_name=?",pi_class);
			// 记录操作
			if(rs.next()){
				baseDao.execute(baseDao.logger.getMessageLog("确认入库","确认入库成功", rs.getString("ds_table"), "pi_id", bi_piid).getSql());
			}
			return "确认入库成功!";
		}
		return null; 
	}

	@Override   //撤销入库
	public String deleteDetail(Integer bi_piid, String barcode,String outboxcode, String whcode) {
		SqlRowList  rs11 = null;
		String str=null;
		String on_whcode;
		SqlRowList rs4 = baseDao.queryForRowSet("select bi_inoutno from barcodeio where bi_piid="+bi_piid);

		if(rs4.next()){
			List<String> sqls = new ArrayList<String>();
		    if(barcode!=null && !("").equals(barcode)){
		    	  SqlRowList rs = baseDao.queryForRowSet("select bi_barcode from barcodeio where bi_piid = ? and bi_barcode = ?",bi_piid,barcode);
		    	  if(!rs.next()){
		    		  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销入库的条码不存在");
		    	  }
		    	   rs=baseDao.queryForRowSet("select bar_id,bar_code,bar_remain,bar_piid,bar_prodcode,nvl(bar_status,1) bar_status,bar_outno, bar_batchqty,bar_whcode,bar_batchid  from barcode where bar_code='"+barcode+"' and bar_status = 1");
		    	  if(rs.next()){
		    		  on_whcode = rs.getString("bar_whcode");
		    		  if(rs.getInt("bar_piid")!=bi_piid){
		    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码不属于该入库单"+rs4.getString("bi_inoutno"));
		    		  }else if(rs.getGeneralInt("bar_status")==2){
		    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码已被出库单"+rs.getString("bar_outno")+"使用不能撤销入库");
		    		  }
		    	     double a= NumberUtil.sub(rs.getDouble("bar_remain"),rs.getDouble("bar_batchqty"));
		    		 if(a==0){
		    			    sqls.add("delete from barcode where bar_id="+rs.getInt("bar_id"));
		    			    sqls.add("update barcodeio set  bi_status=0  where bi_barcode='"+barcode+"' and bi_piid="+bi_piid);
		    			    sqls.add("update prodinout set pi_pdastatus='未入库' where pi_id="+bi_piid +" and pi_pdastatus='已入库'");
							//更新批号是否有条码
		    			    sqls.add("update batch set ba_hasbarcode=0 where ba_id="+rs.getInt("bar_batchid")+" and not exists(select 1 from barcode where bar_batchid=ba_id)");
		    			    //增加操作日志记录
		    				sqls.add("insert into barcodelogger(bl_id,bl_barcode,bl_barid,bl_prodcode,bl_whcode,bl_date,bl_inman,bl_action,bl_description,bl_inoutno)"
		    				 		+ "values(barcodelogger_seq.nextval,'"+barcode+"',"+rs.getInt("bar_id")+",'"+rs.getString("bar_prodcode")+"','"+on_whcode+"',sysdate,'"+SystemSession.getUser().getEm_name()+"','撤销入库','入库单号："+rs4.getString("bi_inoutno")+",数量："+rs.getDouble("bar_remain")+"','"+rs4.getString("bi_inoutno")+"')");
		    			    baseDao.execute(sqls);
		    			    str="撤销成功";
					}else{    					
						throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码已被使用不能撤销入库");
					}
				}else{
		    		throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销入库条码不存在或者不是在库状态");
				}
	       }else if(outboxcode!=null && !("").equals(outboxcode)){
	    	   SqlRowList rs = baseDao.queryForRowSet("select bi_outboxcode from barcodeio where bi_piid = ? and bi_outboxcode = ?",bi_piid,outboxcode);
		    	  if(!rs.next()){
		    		  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销入库的箱号不存在");
		    	  }
	    	  rs=baseDao.queryForRowSet("select bar_status,bar_batchid from barcode where bar_outboxcode1='"+outboxcode+"'");
	    	  if(rs.next()){
	    		  if(rs.getGeneralInt("bar_status")!=2  && rs.getGeneralInt("bar_status")!=1){
	    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"请检查箱号是否有效");
	    		  }
	    		  rs11 = baseDao.queryForRowSet("select * from barcodeio where bi_outboxcode='"+outboxcode+"'");
	    		  if(rs11.next()){
	    			  on_whcode = rs11.getString("bi_whcode");
	    			  if(rs11.getInt("bi_piid")!=bi_piid){
		    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"箱号不属于该入库单"+rs4.getString("bi_inoutno"));
		    		  }else if(rs11.getGeneralInt("bi_status")!=99){
		    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该箱号未确认入库，无需撤销入库");
		    		  }
	    		    	SqlRowList rs0=baseDao.queryForRowSet("select count(1) cn,wm_concat(bar_code) from barcode where bar_piid="+bi_piid+" and bar_outboxcode1='"+outboxcode+"' and bar_whcode='"+on_whcode+"' and bar_status <>1  and rownum<20");
	    		    	if(rs0.next()){
	    		    		if(rs0.getInt("cn")==0){
	    		    			sqls.add("delete from barcode where bar_outboxcode1="+outboxcode);
	    		    			sqls.add("update barcodeio set  bi_status=0  where bi_outboxcode="+outboxcode+" and bi_piid="+bi_piid);
	    		    			sqls.add("update prodinout set pi_pdastatus='未入库' where pi_id="+bi_piid+" and pi_pdastatus='已入库'");
	    		    			sqls.add("update batch set ba_hasbarcode=0 where ba_id="+rs.getInt("bar_batchid")+" and not exists(select 1 from barcode where bar_batchid=ba_id)");
	  							baseDao.execute(sqls);
	    		    			str="撤销成功";
	    		    		}else{  
	    		    			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"箱号内的条码"+rs0.getString("wm_concat(bar_code)")+"已被使用不能撤销入库");
	    		    		}
	    		    	}
	    		  }else{
	        		       throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销入库的箱号不存在");
	        		   }
	    		  }else{
	    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销入库的箱号不存在或者不是在库状态");
	    	  }
	      }
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"不存在需要撤销的备料任务");
		}
		return str;
	}
		
	@Override
	public Map<String, Object> getCodeData(String type, Integer id,
			String whcode, String code) {
		int cn = baseDao.getCount("select count(1) from prodinout left join prodiodetail on pi_id=pd_piid where pi_id="
				+ id + " and nvl(pi_pdastatus,'未入库')<>'已入库'");
		if (cn == 0) {
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据必须是未确认入库的状态");
		}
		SqlRowList rs4=baseDao.queryForRowSet("select bi_inoutno from barcodeio where bi_piid=?",id);
		SqlRowList rs=null;
		if(rs4.next()){
			if(("barcode").equals(type)){
				rs=baseDao.queryForRowSet("select bi_barcode,pr_code,bi_whcode,bi_piid,bi_status from barcodeio left join product on pr_code=bi_prodcode where bi_barcode=? ",code);			
				if(rs.next()){	
					  if(rs.getInt("bi_piid")!=id){
		    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码不属于该入库单"+rs4.getString("bi_inoutno"));
		    		  }else if(rs.getGeneralInt("bi_status")==99){
		    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该条码已确认入库");
		    		  }else{
		    			  return rs.getCurrentMap();
		    		  }
	   			}else{
	   				  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码"+code+"不存在");
	   			}
		 }else if(("package").equals(type)){
				rs=baseDao.queryForRowSet("select bi_outboxcode,pr_code,bi_whcode,bi_piid,bi_status from barcodeio left join product on pr_code=bi_prodcode where bi_outboxcode=?",code);			
				if(rs.next()){
			       if(rs.getInt("bi_piid")!=id){
	    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"箱号不属于该入库单"+rs4.getString("bi_inoutno"));
	    		   }else if(rs.getGeneralInt("bi_status")==99){
	    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该箱号已确认入库");
	    		   }else{
	    				return rs.getCurrentMap();
	    		   }
			  	}else{
		  			 throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"箱号"+code+"不存在");
			  	}
		    }
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"单据不存在需要维护的条码");
		}
		return null;
	}
}
