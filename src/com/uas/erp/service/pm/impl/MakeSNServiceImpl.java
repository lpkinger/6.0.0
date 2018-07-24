package com.uas.erp.service.pm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeSNService;

@Service("makeSBService")
public class MakeSNServiceImpl implements MakeSNService{
	@Autowired
	private BaseDao baseDao;	
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteMakeSN(int ma_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ma_id });		
		//判断序列号是否已经上料
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn,max(msl_sncode) msl_sncode from makesnlist where msl_maid="+ma_id+" and nvl(msl_status,0)<>0");
		if(rs.next() && rs.getInt("cn") > 0){
			BaseUtil.showError("序列号["+rs.getString("msl_sncode")+"]已经使用，不允许清空所有!");
		}
		// 删除MakeSerial
		baseDao.deleteById("MakeSNList", "msl_maid", ma_id);
		// 记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { ma_id });
	}
	
	final static String INSERTMAKESN = "Insert into MakeSNList (msl_id, msl_maid, msl_sncode, msl_indate, msl_printstatus"
			+ ",msl_status,msl_makecode) values (MAKESNLIST_SEQ.NEXTVAL,?,?,sysdate,0,0,?)";
	@Override
	@Transactional
	public void occurCode(int id, String prefixcode, String suffixcode, String startno, int number) {		
		String code=null,macode = null;
		//判断制造单是否批准和完工
		Object[] obj = baseDao.getFieldsDataByCondition("make", "ma_checkstatuscode,ma_finishstatuscode,ma_code", "ma_id="+id);
		if(obj[0] != null&&obj[1] != null){
			macode = obj[2].toString();
			if(!"APPROVE".equals(obj[0].toString())&&!"UNCOMPLET".equals(obj[1].toString())){
				BaseUtil.showError("制造单:"+macode+"未批准或已完工!");
			}
		}
		int cn = baseDao.getCount("select count(1) cn  from make left join makeSNList on ma_id=msl_maid where NVL(msl_status,0)<>-1 and ma_id="+id+" group by ma_id,ma_qty having count(msl_id)>ma_qty-"+number);
		if(cn > 0 ){
			BaseUtil.showError("制造单有效序列号数量超过了制造单数量!");
		}
		for (int i = 0; i < number; i++) {				
			code = suffixcode == null ? prefixcode + startno : prefixcode + startno + suffixcode;
			//startno ++;
			String num1=Long.parseLong(startno)+1+"";
			if(num1.length() > startno.length()){
				startno = num1;
			}else{				
				startno = startno.substring(0,startno.length()-num1.length())+num1;
			}
			//判断是否存在重复的序列号
			cn = baseDao.getCount("select count(1)  from makeSerial where ms_sncode='"+code+"'");
			if(cn > 0){
				BaseUtil.showError("序列号:"+code+"重复!");
			}
			cn = baseDao.getCount("select count(1)  from makeSNList where msl_sncode='"+code+"'");
			if(cn > 0){
				BaseUtil.showError("序列号:"+code+"重复!");
			}
			baseDao.execute(INSERTMAKESN, new Object[]{ id, code, macode});			
		}
	}

	@Override
	public String checkOrNewBarcode(boolean newSerial, String serialCode,
			int ma_id) {
		if(newSerial){//新生成，将输入的条码插入表
			String macode = null; 
			SqlRowList rs0=baseDao.queryForRowSet("select * from Make where ma_id="+ma_id);
			if(rs0.next()){
				macode = rs0.getString("ma_code");
				//判断制造单是否完工,完工则不让生成			
				if(!"APPROVE".equals(rs0.getString("ma_checkstatuscode").toString())&&!"UNCOMPLET".equals(rs0.getString("ma_finishstatuscode").toString())){
					BaseUtil.showError("制造单:"+macode+"未批准或已完工!");
				}				
				//判断是否存在重复的序列号
				int cn = baseDao.getCount("select count(1) from makeSNList where msl_sncode='"+serialCode+"'");
				if(cn > 0){
					BaseUtil.showError("序列号:"+serialCode+"重复!");
				}
				cn = baseDao.getCount("select count(1) from makeSerial where ms_sncode='"+serialCode+"'");
				if(cn > 0){
					BaseUtil.showError("序列号:"+serialCode+"重复!");
				}
				baseDao.execute(INSERTMAKESN, new Object[]{ma_id, serialCode, macode});	
				Object ob = baseDao.getFieldDataByCondition("makeSNList", "msl_id", "msl_sncode='"+serialCode+"'");
				return ob.toString();
			}
		}else{//原有序列号
			//判断序列号是否存在
			Object ob = baseDao.getFieldDataByCondition("makeSNList", "msl_id", "msl_sncode='"+serialCode+"'");
			if(ob == null){
				BaseUtil.showError("序列号:"+serialCode+"不存在!");
			}else{
				return ob.toString();
			}
		}
		return null;
	}
}

