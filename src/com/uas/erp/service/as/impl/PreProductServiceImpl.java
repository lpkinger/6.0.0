package com.uas.erp.service.as.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.SqlServerDao;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Page;
import com.uas.erp.service.as.PreProductService;

@Service("ASPreProductService")
public class PreProductServiceImpl implements PreProductService{
	
	private SqlServerDao dao = SqlServerDao.getSqlServerDao();
	@Autowired
	private BaseDao baseDao;
	
	final static String REG_D = "\\d{4}-\\d{2}-\\d{2}";

	public static String parseFilter(Map<String, Object> filters) {
		List<String> conditions = new ArrayList<String>();
		if (filters != null) {
			for (String key : filters.keySet()) {
				Object val = filters.get(key);
				if (val instanceof Number) {
					conditions.add(key + "=" + val);
				} else if (val instanceof String) {
					if (val.toString().matches(REG_D)) {
						conditions.add("CONVERT(varchar(100)," + key + ",23)='" + val + "'");
					} else {
						conditions.add("upper(" + key + ") like '%" + val.toString().toUpperCase() + "%'");
					}
				}
			}
		}
		return CollectionUtil.toString(conditions, " and ");
	}
	
	@Override
	public List<?> getPreProduct(Map<String, Object> filters) {
		String condition = parseFilter(filters);
		if (StringUtil.hasText(condition))
			condition = " and " + condition;
		//dao.execute("update apply_table set status='',inoutno='',updateman='',class='' where updateman='马丹' ");
		SqlRowList list = dao.query("SELECT apply_no,store_name,CONVERT(varchar(100),apply_day,23) as apply_day,shenqing_op,objective_store,bill_detail,beizhu from apply_table left join service_store on apply_table.store_id= service_store.store_id where isnull(status,'')<>'已处理' and isnull(apply_runflags,'')='OK'"
				+ condition + " order by apply_no");
		return list.getResultList();
	}
	
	@Override
	public List<?> getPreProductDetail(String code) {
		SqlRowList list = dao.query("SELECT ID,apply_detail.fitting_code FITTING_CODE, FITTING_NAME,queren_quantity QUANTITY,UNIT,KEHEXIAO,BEIZHU from apply_detail left join fitting_table on apply_detail.fitting_code=fitting_table.fitting_code where apply_no='"+code+"' order by id");
		return list.getResultList();
	}
	
	@Override
	public String applyToProdIO(String data) {
		StringBuffer sb = new StringBuffer();
		int okNum = 0;
		int piid = 0;
		String inoutno = null;
		String piclass = null;
		String pitype = null;
		String piwhcode = null;
		if (StringUtil.hasText(data)) {
			String log = null;
			String codes = CollectionUtil.toSqlString(data.split(","));
			SqlRowList rs = dao
					.query("SELECT * from apply_table left join service_store on apply_table.store_id= service_store.store_id where apply_no in("
							+ codes + ") order by apply_no");
			while (rs.next()) {
				String apply_no = rs.getString("apply_no");
				if("买断型".equals(rs.getGeneralString("apply_type").trim())){
					piid = baseDao.getSeqId("PRODINOUT_SEQ");
					inoutno = baseDao.sGetMaxNumber("ProdInOut!OtherOut", 2);
		            piclass = "其它出库单";
		            pitype = "配件销售";
		            piwhcode = "142";
				} else {
					piid = baseDao.getSeqId("PRODINOUT_SEQ");
					inoutno = baseDao.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
		            piclass = "拨出单";
		            pitype = "网点调拨";
		            piwhcode = "142";
				}
				baseDao.execute("insert into prodinout(pi_id, pi_inoutno, pi_class, pi_whcode, pi_date, pi_recorddate, pi_currency, pi_rate, pi_status, pi_statuscode,pi_invostatus,pi_invostatuscode, pi_recordman, pi_type,pi_fromcode,pi_remark,PI_REFNO,pi_printstatus,pi_printstatuscode) values('" +
						piid + "','" + inoutno + "','" + piclass + "','" + piwhcode + "',sysdate,sysdate,'RMB',1,'未过帐','UNPOST','在录入','ENTERING','" + SystemSession.getUser().getEm_name() + "','" + pitype + "','" + rs.getGeneralString("apply_no").trim() + "','" + rs.getGeneralString("apply_no").trim() + "','" + rs.getGeneralString("store_id").trim() + "', '未打印','UNPRINT')");
				baseDao.execute("update prodinout set pi_whname=(select wh_description from warehouse where wh_code=pi_whcode) where pi_id=" + piid);
				baseDao.execute("update prodinout set (pi_cardcode,pi_purpose)=(select cu_erpcustcode,cu_location from customerfs where PI_REFNO=cu_code) where pi_id=" + piid);
				baseDao.execute("update prodinout set pi_purposename=(select wh_description from warehouse where wh_code=pi_purpose) where pi_id="
						+ piid);
				baseDao.execute("update prodinout set pi_purpose=null,pi_purposename=null where pi_id=" + piid + " and pi_class='其它出库单'");
				baseDao.execute("update prodinout set (pi_cardid,pi_title)=(select cu_id,cu_name from customer where cu_code=pi_cardcode) where pi_id=" + piid);
				int count = baseDao.getCountByCondition("prodinout", "pi_id=" + piid);
				if(count > 0){
					dao.execute("update apply_table set status='已处理',inoutno='" + inoutno + "',updateman='" + SystemSession.getUser().getEm_name() + "',updatedate=getdate(),class='" + piclass + "' where apply_no='" + apply_no + "' ");
					String sql = null;
					if("拨出单".equals(piclass)){
						sql = "Select * from apply_detail where apply_no='" + apply_no + "' and queren_quantity>0 order by fitting_code ";
					} else {
						sql = "Select apply_detail.* from apply_detail,fitting_table where apply_detail.fitting_code=fitting_table.fitting_code and apply_detail.apply_no='" + apply_no + "' and apply_detail.queren_quantity>0 order by fitting_table.fitting_name ";
					}
					SqlRowList rs2 = dao.query(sql);
					Double ftprice = 0.0;
					int index = 1;
					while(rs2.next()){
						SqlRowList rs3 = dao.query("Select onelevel_price from fitting_table where fitting_code='" + rs2.getObject("fitting_code") + "' ");
						if(rs3.next()){
							ftprice = rs3.getDouble("onelevel_price");
						}
						baseDao.execute("insert into prodiodetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_prodcode, pd_outqty, pd_status, pd_price,pd_sendprice) values( PRODIODETAIL_SEQ.NEXTVAL,"
								+ piid
								+ ",'"
								+ inoutno
								+ "','"
								+ piclass
								+ "',"
								+ index
								+ ",'"
								+ rs2.getGeneralString("fitting_code").trim()
								+ "',"
								+ rs2.getGeneralDouble("queren_quantity")
								+ ",0,0,"
								+ ftprice + ")");
						index ++ ;
					}
					baseDao.execute("update prodiodetail set (pd_whcode,pd_whname,pd_inwhcode,pd_inwhname)=(select pi_whcode,pi_whname,pi_purpose,pi_purposename from prodinout where pd_piid=pi_id) where pd_piid=" + piid);
					if("拨出单".equals(piclass)){
						log = "转入成功,拨出单:"
								+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ piid + "&gridCondition=pd_piidIS" + piid
								+ "&whoami=ProdInOut!AppropriationOut')\">"
								+ inoutno + "</a>";
					} else {
						log = "转入成功,其它出库单:"
								+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
								+ piid + "&gridCondition=pd_piidIS" + piid
								+ "&whoami=ProdInOut!OtherOut')\">"
								+ inoutno + "</a>";
					}
					okNum = okNum + 1;
					sb.append(okNum).append(": ").append(log).append("<hr>");
				}
			}
		}
		if (okNum > 0) {
			return sb.toString();
		}
		return null;
	}

	@Override
	public String applyDelete(String data) {
		if (StringUtil.hasText(data)) {
			String codes = CollectionUtil.toSqlString(data.split(","));
			dao.execute("update apply_table set status='已处理', inoutno='不处理', updateman='"
					+ SystemSession.getUser().getEm_name()
					+ "', updatedate=getdate(), class='' where apply_no in("
					+ codes + ")");
		}
		return "删除成功!";
	}

	@Override
	public Page<Map<String, Object>> getApplyList(int page, int start,
			int limit, Map<String, Object> filters) {
		String condition = parseFilter(filters);
		if (StringUtil.hasText(condition))
			condition = " and " + condition;
		final Integer total = dao
				.query("select count(*) from apply_table left join service_store on apply_table.store_id= service_store.store_id where apply_day>'2011-7-11' and isnull(status,'')='已处理' and updatedate>='2011-8-1'"
						+ condition, Integer.class);
		final SqlRowList rs = dao
				.query("SELECT apply_table.APPLY_NO, CONVERT(varchar(100),apply_table.APPLY_DAY,23) as APPLY_DAY, apply_table.STORE_ID, service_store.STORE_NAME, apply_table.SHENQING_OP, apply_table.OBJECTIVE_STORE, CONVERT(varchar(100),apply_table.APPLY_DATETIME,23) as APPLY_DATETIME, apply_table.APPLY_RESULT, apply_table.APPLY_OP, apply_table.INOUTNO, apply_table.CLASS, apply_table.UPDATEMAN, CONVERT(varchar(100),apply_table.UPDATEDATE,23) as UPDATEDATE FROM apply_table left join service_store on apply_table.store_id= service_store.store_id,(select top "
						+ page
						* limit
						+ " row_number() OVER (order by updatedate desc,apply_day desc,apply_no desc) n, apply_no from apply_table left join service_store on apply_table.store_id= service_store.store_id where apply_day>'2011-7-11' and isnull(status,'')='已处理' and updatedate>='2011-8-1'"
						+ condition
						+ ") w2 WHERE apply_table.apply_no = w2.apply_no AND w2.n > "
						+ start + " ORDER BY w2.n ASC");
		Page<Map<String, Object>> pageData = new Page<Map<String, Object>>() {

			@Override
			public int getTotalCount() {
				return total;
			}

			@Override
			public List<Map<String, Object>> getTarget() {
				return rs.getResultList();
			}
		};
		return pageData;
	}
}
