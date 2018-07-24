package com.uas.erp.dao.common.impl;

import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.dao.common.MakeDao;

@Repository("makeCraftDao")
public class MakeCraftDaoImpl extends BaseDao implements MakeCraftDao {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeDao makeDao;

	@Override
	public void updateMakeMessage(String type, String result, String ms_sncode, String mc_code, String step_code) {
		// 如果已经采集的工序，返修再次采集不需要更新，通过ms_paststep记录
		// boolean bo = checkHaveGetStep(ms_sncode,step_code);
		Object ob1 = baseDao.getFieldDataByCondition("makeSerial left join makeCraft on ms_craftcode=mc_craftcode", "ms_code",
				"ms_sncode='" + ms_sncode + "' and mc_code='" + mc_code + "'");
		if (ob1 == null) {// 返修工序
			// 插入makeprocess 采集日志
			baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname,"
					+ " mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"
					+ " Select MakeProcess_seq.nextval, mc_makecode,mc_maid,'"
					+ ms_sncode
					+ "','"
					+ ms_sncode
					+ "',ms_nextstepcode,sc_stepname,"
					+ " ms_craftcode,cr_name,'"
					+ type
					+ "','"
					+ result
					+ "',sysdate,'"
					+ SystemSession.getUser().getEm_name()
					+ "',mc_wccode,sc_linecode,sc_code from makeCraft "
					+ " left join makeSerial on ms_mccode=mc_code left join source on sc_stepcode=ms_nextstepcode left join craft on cr_code=ms_craftcode where mc_code='"
					+ mc_code + "' and ms_nextstepcode='" + step_code + "' and ms_sncode='" + ms_sncode + "'");
			// 获取下一工序
			Object ob = baseDao
					.getFieldDataByCondition(
							"makeserial left join craft on cr_code=ms_craftcode left join craftdetail A on cr_id= A.cd_crid left join craftdetail B on A.cd_stepno+1= B.cd_stepno and A.cd_crid=B.cd_crid",
							"B.cd_stepcode", "A.cd_stepcode='" + step_code + "' and ms_sncode='" + ms_sncode + "'");
			// 判断工序是否为最后一道工序如果是，则更新makeCraft 表中的 mc_madeqty+1,makeSerial
			// 中的ms_status=2 已完成
			if (ob == null) {// 没有下一工序，判断是最后一道工序
				baseDao.updateByCondition("makeCraft", "mc_madeqty=mc_madeqty+1", "mc_code='" + mc_code + "'");
				baseDao.updateByCondition("makeSerial", "ms_status=2", "ms_sncode='" + ms_sncode + "'");
				// 更新makeSerial 的下一工序
				baseDao.execute("Update makeserial set ms_nextstepcode='',ms_stepcode='" + step_code + "' where ms_sncode='" + ms_sncode
						+ "'");
			} else {
				// 更新makeSerial 的下一工序
				baseDao.execute("Update makeserial set ms_nextstepcode='" + ob + "',ms_status=1,ms_stepcode='" + step_code
						+ "' where ms_sncode='" + ms_sncode + "'");
			}
		} else {
			// 更新makeCraftDetail inqty outqty okqty
			baseDao.execute("Update makecraftdetail set mcd_inqty=mcd_inqty+1,mcd_outqty=mcd_outqty+1,mcd_okqty=mcd_okqty+1 "
					+ " where mcd_id=(select mcd_id from makeCraft left join makecraftdetail on mc_id=mcd_mcid where mc_code='" + mc_code
					+ "' and mcd_stepcode='" + step_code + "')");// 更新已经采集的工序
			// 更新已经采集的工序
			baseDao.execute("Update makeserial 	set ms_paststep = ms_paststep ||'," + step_code + "' where ms_sncode='" + ms_sncode + "'");
			// 插入makeprocess 采集日志
			baseDao.execute("insert  into MakeProcess(mp_id,mp_makecode,mp_maid,mp_mscode,mp_sncode,mp_stepcode,mp_stepname,"
					+ " mp_craftcode,mp_craftname,mp_kind,mp_result,mp_indate,mp_inman,mp_wccode,mp_linecode,mp_sourcecode)"
					+ " Select MakeProcess_seq.nextval, mc_makecode,mc_maid,'" + ms_sncode + "','" + ms_sncode
					+ "',mcd_stepcode,mcd_stepname," + " mc_craftcode,mc_craftname,'" + type + "','" + result + "',sysdate,'"
					+ SystemSession.getUser().getEm_name() + "',mc_wccode,sc_linecode,sc_code from makeCraft left join makeCraftDetail"
					+ " on mc_id=mcd_mcid left join source on sc_stepcode=mcd_stepcode where mc_code='" + mc_code + "' and mcd_stepcode='"
					+ step_code + "' and rownum=1");
			// 更新makeSerial 的下一工序
			baseDao.execute("Update makeserial set ms_nextstepcode=(select mcd_nextstepcode from makeCraft left join makeCraftdetail on mc_id=mcd_mcid where mc_code='"
					+ mc_code
					+ "' and mcd_stepcode='"
					+ step_code
					+ "'),ms_status=1,ms_stepcode='"
					+ step_code
					+ "' where ms_sncode='"
					+ ms_sncode + "'");
			// 判断工序是否为最后一道工序如果是，则更新makeCraft 表中的 mc_madeqty+1,makeSerial
			// 中的ms_status=2 已完成
			Object ob = baseDao.getFieldDataByCondition("makecraft left join makecraftdetail on mc_id=mcd_mcid", "mcd_nextstepcode",
					"mc_code='" + mc_code + "' and mcd_stepcode='" + step_code + "'");
			if (ob == null) {// 没有下一工序，判断是最后一道工序
				baseDao.updateByCondition("makeCraft", "mc_madeqty=mc_madeqty+1", "mc_code='" + mc_code + "'");
				baseDao.updateByCondition("makeSerial", "ms_status=2", "ms_sncode='" + ms_sncode + "'");
			}
		}
		// 判断采集点工序是否为扣料工序
		ob1 = baseDao.getFieldDataByCondition("makeSerial left join Craft on cr_code=ms_craftcode left join CraftDetail on cd_crid=cr_id",
				"cd_stepcode", "ms_sncode='" + ms_sncode + "' and cd_stepcode='" + step_code + "' and cd_ifreduce=-1");
		if (ob1 != null) {
			// 如果是采集点工序是扣料工序，更新makesmtlocation
			// 的msl_remainqty=msl_remainqty-NVL(msl_baseqty,0) where
			// msl_mccode=作业单号
			baseDao.updateByCondition("makesmtlocation", "msl_remainqty=msl_remainqty-NVL(msl_baseqty,0)", "msl_mccode ='" + mc_code
					+ "' and NVL(msl_status,0)=0");
			// 判断站位用量是否达到需求量，如果达到则自动生成领料单
			SqlRowList rs = baseDao.queryForRowSet("select sum(count(1)) sm from makeSMtlocation where msl_mccode='" + mc_code
					+ "' group by msl_location,msl_needqty having sum(msl_getqty)-sum(msl_remainqty)< msl_needqty order by msl_location");
			if (!rs.next() || rs.getInt("sm") == 0) {
				// 判断是否根据作业单生成了领料单
				ob1 = baseDao.getFieldDataByCondition("prodInOut", "pi_inoutno", "pi_sourcecode='" + mc_code + "'");
				if (ob1 == null) {
					turnProdOut(mc_code);
				}
			}
		}
	}

	@Override
	public boolean ifNextStepcode(String stepcode, String ms_sncode, String mc_code) {
		Object ob;
		SqlRowList rs;
		rs = baseDao
				.queryForRowSet("select ms_nextstepcode,NVL(st_ifsmtinout,0) st_ifsmtinout from makeserial left join step on st_code=ms_nextstepcode where ms_sncode='"
						+ ms_sncode + "' and ms_nextstepcode is not null");
		if (rs.next()) {// 判断采集的工序是否存在下一工序
			if (rs.getString("ms_nextstepcode").equals(stepcode)) {
				return true;
			}
			if (rs.getInt("st_ifsmtinout") == -1) {// 序列号的下一工序是SMT防呆工序,再进行判断
				// 判断下一工序的时候首先判断工艺路线是否修改过，为返修的工艺路线
				ob = baseDao.getFieldDataByCondition("makeSerial left join makeCraft on ms_craftcode=mc_craftcode", "ms_code",
						"ms_sncode='" + ms_sncode + "' and mc_code='" + mc_code + "'");
				if (ob == null) {// 返修工序
					ob = baseDao
							.getFieldDataByCondition(
									"makeserial left join craft on cr_code=ms_craftcode left join craftdetail A on cr_id= A.cd_crid left join craftdetail B on A.cd_stepno+1= B.cd_stepno and A.cd_crid=B.cd_crid",
									"B.cd_stepcode", "A.cd_stepcode='" + rs.getString("ms_nextstepcode") + "' and ms_sncode='" + ms_sncode
											+ "' and B.cd_stepcode ='" + stepcode + "'");
					if (ob != null) {
						return true;
					} else {
						return false;
					}
				} else {
					ob = baseDao
							.getFieldDataByCondition(
									"Make  left join makeCraft on mc_makecode=ma_code left join Craft on ma_craftcode=cr_code left join craftdetail A on cr_id= A.cd_crid left join craftdetail B on A.cd_stepno+1= B.cd_stepno and A.cd_crid=B.cd_crid",
									" B.cd_stepcode", "A.cd_stepcode='" + rs.getString("ms_nextstepcode") + "' and mc_code='" + mc_code
											+ "' and B.cd_stepcode ='" + stepcode + "'");
					if (ob != null) {
						return true;
					} else {
						return false;
					}
				}
			}
			if (rs.getInt("st_ifsmtinout") == 0) {// 当前工序不等于序列号的nextstepcode，错误工序
				return false;
			}
		} else {
			BaseUtil.showError("序列号：" + ms_sncode + "错误，不存在下一工序!");
		}
		return false;
	}

	/**
	 * 生成领料单
	 * 
	 * @param mc_code
	 */
	@Override
	public void turnProdOut(String mc_code) {
		// 每个站位用量都已达到需求用量，生成领料单 ,推式物料不需要生成领料单，针对拉式
		JSONObject j = null;
		String code = null;
		String piclass = "生产领料单";
		String whoami = "ProdInOut!Picking";
		SqlRowList rs0 = baseDao
				.queryForRowSet("select mm_id,mm_detno,bar_batchcode,bar_batchid,getqty,msl_location,mp_whcode from makeMaterial left join makePrepare "
						+ " on mp_maid=mm_maid left join makePrepareDetail on md_mpid=mp_id and md_mmdetno=mm_detno LEFT JOIN "
						+ " (select msl_location,bar_batchcode,sum(msl_getqty)-sum(msl_remainqty) getqty ,MSL_mccode,bar_batchid from makesmtlocation left join barcode on bar_code=msl_barcode where msl_mccode='"
						+ mc_code
						+ "'"
						+ " and msl_getqty-msl_remainqty>0  group by msl_location,bar_batchcode,bar_batchid,MSL_mccode)T on T.msl_location=md_location and msl_mccode=mp_mccode "
						+ " left join product on pr_code=mm_prodcode where msl_mccode='"
						+ mc_code
						+ "' and pr_supplytype='PULL' order by mm_detno");
		if (rs0.next()) {
			j = makeDao.newProdIO(rs0.getString("mp_whcode"), piclass, whoami,null);
			if (j != null) {
				code = j.getString("pi_inoutno");
				// 更新来源为作业单号
				baseDao.updateByCondition("prodinout", "pi_sourcecode='" + mc_code + "'", "pi_id=" + j.get("pi_id"));
				int detno = 1;
				for (Map<String, Object> p : rs0.getResultList()) {
					turnOut(code, detno++, piclass, Integer.parseInt(p.get("mm_id").toString()),
							Integer.parseInt(p.get("mm_detno").toString()), Double.parseDouble(p.get("getqty").toString()),
							p.get("bar_batchcode").toString(), Integer.parseInt(p.get("bar_batchid").toString()));
				}
				baseDao.execute(
						"update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pi_id=pd_piid) where pd_piid=? and nvl(pd_whcode,' ')=' '",
						j.get("pi_id"));
				// 针对每条明细行采集条码barcodeIo
				baseDao.execute("insert into barcodeio (bi_id,bi_barcode,bi_piid,bi_inoutno,bi_pdno,bi_pdid,bi_batchid,bi_batchcode,bi_status,bi_printstatus,"
						+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid) "
						+ " select BARCODEIO_SEQ.nextval,msl_barcode,pd_piid,pd_inoutno,pd_pdno,pd_id,pd_batchid,pd_batchcode,0,0,"
						+ " pd_prodcode,pd_whcode,msl_getqty-msl_remainqty,sysdate,pd_location,pd_prodid from makesmtlocation left join makePrepare on mp_mccode=msl_mccode "
						+ " left join makePrepareDetail on msl_location=md_location and mp_id=md_mpid left join barcode on bar_code=msl_barcode left join prodiodetail on pd_orderdetno=md_mmdetno and bar_batchcode=pd_batchcode"
						+ " where msl_mccode='" + mc_code + "' and pd_inoutno='" + code + "' and msl_getqty-msl_remainqty>0");
				// 更新条码采集数量
				baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty  where pd_piid=?", j.get("pi_id"));
			}
		}
	}

	final static String INSERT_DETAIL = "INSERT INTO ProdIODetail(pd_id, pd_piid, pd_inoutno, pd_piclass, pd_pdno, pd_status,pd_auditstatus,pd_prodcode, pd_outqty,"
			+ "pd_ordercode, pd_orderdetno, pd_plancode, pd_wccode, pd_orderid, pd_prodid, pd_whcode, pd_whname,pd_batchcode,pd_batchid,pd_location) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private void turnOut(String no, int detno, String piclass, int mmid, int mmdetno, double qty, String batchcode, int batchid) {
		Object[] pis = baseDao.getFieldsDataByCondition("ProdInOut", "pi_id,pi_whcode,pi_whname", "pi_inoutno='" + no + "' AND pi_class='"
				+ piclass + "'");
		Object[] objs = null;
		Object prid = null;
		objs = baseDao.getFieldsDataByCondition("MakeMaterial left join product on pr_code=mm_prodcode", new String[] { "mm_code",
				"mm_mdcode", "mm_prodcode", "mm_wccode", "pr_wiplocation" }, "mm_id=" + mmid);
		prid = baseDao.getFieldDataByCondition("Product", "pr_id", "pr_code='" + objs[2] + "'");
		baseDao.execute(INSERT_DETAIL, new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), pis[0], no, piclass, detno, 0, "ENTERING",
				objs[2], qty, objs[0], mmdetno, objs[1], objs[3], mmid, prid, pis[1], pis[2], batchcode, batchid, objs[4] });
		if ("生产领料单".equals(piclass)) {
			baseDao.updateByCondition("MakeMaterial", "mm_totaluseqty=nvl(mm_totaluseqty,0)+" + qty, "mm_id=" + mmid);
		}
	}

	@Override
	public boolean checkRep(String prod_code, String rep_code) {
		// TODO Auto-generated method stub
		String[] arr = rep_code.split(",");
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(prod_code)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkHaveGetStep(String ms_sncode, String st_code) {
		// 如果已经采集的工序，返修再次采集不需要更新，通过ms_paststep记录
		boolean bo = false;
		/*
		 * Object ob2 = baseDao.getFieldDataByCondition("makeSerial ",
		 * "ms_paststep", "ms_sncode='"+ms_sncode+"'"); if(ob2 != null){ String
		 * [] arrs = ob2.toString().split(","); for(int i =
		 * 0;i<arrs.length;i++){ if(arrs[i].equals(st_code)){//已经采集 bo = true;
		 * break; } } }
		 */
		return bo;
	}

	@Override
	public void updateStatus(Object mc_id) {
		execute("update MakeCraft set mc_turnstatuscode=null,mc_turnstatus=null where mc_id=" + mc_id + " and nvl(mc_yqty,0)=0");
		execute("update MakeCraft set mc_turnstatuscode='PART2VA',mc_turnstatus='" + BaseUtil.getLocalMessage("PART2VA") + "' where mc_id="
				+ mc_id + " and nvl(mc_turnstatuscode,' ')<>'PART2VA' and nvl(mc_yqty,0)>0");
		execute("update MakeCraft set mc_turnstatuscode='TURNIN',mc_turnstatus='" + BaseUtil.getLocalMessage("TURNVA") + "' where mc_id="
				+ mc_id + " and nvl(mc_turnstatuscode,' ')<>'TURNVA' and abs(nvl(mc_qty,0))=abs(NVL(mc_yqty,0))");
	}
}
