package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.service.pm.PackageTransferService;

@Service("packageTransferService")
public class PackageTransferServiceImpl implements PackageTransferService {
	@Autowired
	private BaseDao baseDao;
	@Autowired 
	private  VerifyApplyDao verifyApplyDao;


	@Override
	public Map<String, Object> generateNewPackage(double pa_totalqtynew,
			String pa_oldcode) {
		// TODO Auto-generated method stub
		Object ob = baseDao.getFieldDataByCondition("package left join product on pr_code=pa_prodcode", "pr_id", "pa_outboxcode='"+pa_oldcode+"' and pa_status='0'");
		if(ob != null){//根据原有箱号生成新的箱号
			String pa_code = verifyApplyDao.outboxMethod(ob.toString(),"1");
			baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_totalqty,pa_status,pa_makecode,pa_prodcode,pa_packdate,pa_whcode,pa_indate)" +
					" select Package_seq.nextval,'"+pa_code+"','"+pa_totalqtynew+"','0',pa_makecode,pa_prodcode,sysdate,pa_whcode,sysdate from packge where pa_outboxcode='"+pa_oldcode+"'");
	       Map<String,Object> map = new HashMap<String, Object>();
	       map.put("pa_code", pa_code);
	       map.put("pa_totalqtynew", pa_totalqtynew);
		}else{
			BaseUtil.showError("箱号:"+pa_oldcode+"无效，或者不存在!");
		}
	    return null;
	}

	@Override
	public void getPackageDetailSerial(String condition) {
		// TODO Auto-generated method stub
		Map<Object,Object> formStore =  BaseUtil.parseFormStoreToMap(condition);
		//判断序列号是否在原箱号内
		Object  ob1 = baseDao.getFieldDataByCondition("packagedetail ", "pd_id", "pd_outboxcode='"+formStore.get("pa_oldcode")+"' and pd_barcode='"+formStore.get("serialcode")+"'");
		if(ob1 == null){
			BaseUtil.showError("序列号:"+formStore.get("serialcode")+"不存在箱号:"+formStore.get("pa_oldcode")+"内!");
		}
		Object ob2 = baseDao.getFieldDataByCondition("packagedetail ", "pd_id", "pd_outboxcode='"+formStore.get("pa_newcode")+"' and pd_barcode='"+formStore.get("serialcode")+"'");
		if(ob2 != null){
			BaseUtil.showError("序列号:"+formStore.get("serialcode")+"已存在目标箱号:"+formStore.get("pa_newcode")+"内，请勿重复采集!");
		}
		//当前装箱数量是否已经达到了总容量
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn  from package left join packagedetail on pa_id=pd_paid  where pa_outboxcode='"+formStore.get("pa_newcode")+"' group by pa_totalqty having  pa_totalqty = SUM(pd_innerqty)" );
		if(rs.next() && rs.getInt("cn") > 0){
			BaseUtil.showError("箱号:"+formStore.get("pa_newcode")+"当前装箱数量已经达到总容量!");
		}
		int id = baseDao.getSeqId("packagedetail_seq");
		//从原箱号明细表删除记录，插入到新箱号明细表，更新原箱号新箱号的主表总数
		baseDao.deleteByCondition("packageDetail", "pd_id="+ob1);
		baseDao.updateByCondition("package", "pa_totalqty=pa_totalqty-1,pa_packageqty=pa_packageqty-1", "pa_outboxcode='"+formStore.get("pa_oldcode")+"'");
        baseDao.execute("insert into packagedetail(pd_id,pd_outboxcode,pd_innerqty,pd_barcode,pd_paid) " +
        		"  select "+id+",'"+formStore.get("pa_newcode")+"',1,'"+formStore.get("serialcode")+"',pa_id from package where pa_outboxcode='"+formStore.get("pa_newcode")+"'");
        baseDao.updateByCondition("package", "pa_packageqty=pa_packageqty+1", "pa_outboxcode='"+formStore.get("pa_newcode")+"'");
	}

	@Override
	public Map<String, Object> getFormTStore(String condition) {
		// TODO Auto-generated method stub
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(condition);
		//判断原箱号是否存在，判断目标箱号是否存在,并且是同一工单号
		Object ob1[] = baseDao.getFieldsDataByCondition("package", new String[]{"pa_makecode","pa_totalqty"}, "pa_outboxcode='"+map.get("pa_outboxcode")+"' and pa_status='0'");
		if(ob1 == null){
			BaseUtil.showError("箱号:"+map.get("pa_outboxcode")+"不存在或者状态无效!");
		}
		Object ob2[] = baseDao.getFieldsDataByCondition("package", new String[]{"pa_makecode","pa_totalqty"}, "pa_outboxcode='"+map.get("pa_outboxnew")+"' and pa_status='0'");
		if(ob2 == null){
			BaseUtil.showError("目标箱号:"+map.get("pa_outboxnew")+"不存在或者状态无效!");
		}
		if(ob1 != null && ob2[0] != null && !ob1[0].toString().equals(ob2[0].toString())){
			BaseUtil.showError("原箱号和目标箱号工单不一致!");
		}
		Map<String,Object> mapr = new HashMap<String, Object>();
		mapr.put("pa_outboxcode", map.get("pa_outboxcode"));
		mapr.put("pa_totalqty", ob1[1]);
		mapr.put("pa_outboxnew", map.get("pa_outboxnew"));
		mapr.put("pa_totalqtynew", ob2[1]);
		return mapr;
	}
}
