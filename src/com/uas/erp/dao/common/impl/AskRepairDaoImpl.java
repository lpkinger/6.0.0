package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AskRepairDao;
import com.uas.erp.model.Employee;
@Repository
public class AskRepairDaoImpl extends BaseDao implements AskRepairDao {
	static final String TURNREPAIRORDER="select crd_qty,crd_prodcode,crd_prodname,crd_spec,crd_unit,cr_id,cr_code,crd_remark,crd_isok,crd_faultt,crd_batchcode from CustomerRepairDetail left join CustomerRepair on crd_crid=cr_id where crd_id=?";
	static final String INSERTREPAIRORDER="INSERT INTO REPAIRORDER(RO_ID,RO_CODE,RO_STATUS,RO_REPAIREMNAME,RO_EMNAME,RO_RECORDDATE,RO_STATUSCODE,ro_cucode,ro_otherenname,ro_otherenid) values (?,?,?,?,?,?,?,?,?,?)";

	@Override
	public JSONObject turnRepairOrder(String language, Employee employee,
			String custcode,String repairman) {
		int ro_id = getSeqId("REPAIRORDER_SEQ");
		String rocode = sGetMaxNumber("RepairOrder", 2);
		Object[] objs = getFieldsDataByCondition("Customer", new String[] {"cu_name", "cu_uu"},
				"cu_code='" + custcode + "'");//	
		boolean bool = execute(
				INSERTREPAIRORDER,
				new Object[] { ro_id, rocode, BaseUtil.getLocalMessage("ENTERING", language), repairman,
						employee.getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),"ENTERING",custcode,objs[0],objs[1]});
	    if (bool) {
			JSONObject j = new JSONObject();
			j.put("ro_id", ro_id);
			j.put("ro_code", rocode);
			return j;
		}
		return null;
	}

	@Override
	public void toRepairOrderDetail(String rocode, int crid, String language,
			Employee employee) {
		Object roid = getFieldDataByCondition("RepairOrder", "ro_id", "ro_code='" + rocode + "'");
		SqlRowList rs = queryForRowSet(TURNREPAIRORDER, new Object[] { crid });
		if (rs.next()) {
			Object count = getFieldDataByCondition("RepairOrderDetail", "max(rod_detno)", "rod_roid='" + roid + "'");
			count = count == null ? 0 : count;
			int detno = Integer.parseInt(count.toString());		
			Double qty = Double.parseDouble(rs.getObject("crd_qty").toString());
			SqlMap map = new SqlMap("RepairOrderDetail");
			map.set("rod_id", getSeqId("RepairOrderDetail_SEQ"));
			map.set("rod_roid", roid);			
			map.set("rod_detno", ++detno);
			map.set("rod_prodcode", rs.getObject("crd_prodcode"));
			map.set("rod_prodname", rs.getObject("crd_prodname"));
			map.set("rod_spec", rs.getObject("crd_spec"));
			map.set("rod_unit", rs.getObject("crd_unit"));
			map.set("rod_qty", qty);
			map.set("rod_source", rs.getObject("cr_id"));
			map.set("rod_sourcecode", rs.getObject("cr_code"));
			map.set("rod_remark", rs.getObject("crd_remark"));			
			map.set("rod_isok", rs.getObject("crd_isok"));
			map.set("rod_sourcedetail", crid);
			map.set("rod_fault", rs.getObject("crd_faultt"));
			map.set("rod_batchcode", rs.getObject("crd_batchcode"));
			map.execute();
		}
	}

}
