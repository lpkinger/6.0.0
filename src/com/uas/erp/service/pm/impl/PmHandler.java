package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AppMouldDao;
import com.uas.erp.dao.common.MakeCraftDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.MouldFeePleaseDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.pm.AppMouldService;
import com.uas.erp.service.scm.ProdInOutService;

@Service("PmHandler")
public class PmHandler {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private ProdInOutService prodInOutService;
	@Autowired
	private MouldFeePleaseDao mouldFeePleaseDao;
	@Autowired
	private AppMouldService pppMouldService;
	@Autowired
	private AppMouldDao appMouldDao;
	@Autowired
	private MakeCraftDao makeCraftDao;

	/**
	 * MRP
	 * */
	public void RunMrp(String code) {
		baseDao.callProcedure("MM_RUNMRP", new String[] { code, SystemSession.getUser().getEm_name() });
		return;
	}

	/**
	 * 备损数 ma_id
	 * */
	public void make_save_setbalanceByMake(Integer ma_id) {

	}

	/**
	 * 未填写的用料属性根据物料属性自动默认 工单ID maidstr 逗号隔开 用料表ID mmidstr 逗号隔开
	 * */
	public void make_save_default(String maidstr, String mmidstr) {
		makeDao.saveDefault(maidstr, mmidstr);
	}

	/**
	 * 备损数 mm_id
	 * */
	public void make_save_setbalanceByMaterial(Integer mm_id) {
		SqlRowList sl = baseDao.queryForRowSet("select mm_oneuseqty,mm_maid,mm_qty from MakeMaterial where mm_id=" + mm_id);
		float mm_qty = (float) 0.0;
		float mm_oneuseqty = (float) 0.0;
		int maid = 0;
		float orderqty = (float) 0.0;
		if (sl.next()) {
			mm_qty = sl.getFloat("mm_qty");
			mm_oneuseqty = sl.getFloat("mm_oneuseqty");
			maid = sl.getInt("mm_maid");
		}
		SqlRowList sl2 = baseDao.queryForRowSet("select ma_qty from make where ma_id=" + maid);
		if (sl2.next()) {
			orderqty = sl2.getFloat("ma_qty");
		}
		float count = mm_qty - (mm_oneuseqty * orderqty);
		if (count < 0 || count == 0) {
			count = 0;
		}
		baseDao.updateByCondition("MakeMaterial", "mm_balance='" + count + "'", "mm_id=" + mm_id);
	}

	/**
	 * 制造单/委外单:替代料可使用数量不能小于该替代料已经领料和已转领料数量 C1000001_2013030054
	 * */
	public void make_replace_editqty(int mm_id) {
		// 输出提示信息的方法: BaseUtil.showError(错误信息) 中断 //

	}

	/**
	 * 制造单/委外单:替代料可发料数量不能大于总需求-（主料和其它替代料已领料和已转数量） C1000001_2013030056
	 * */
	public void make_replace_overqty(int mm_id) {

	}

	/**
	 * 制造单/委外单:替代料可发料数量不能大于总需求-其它替代料维护数量 C1000001_2013030165
	 * */
	public void make_replace_overneedqty(int mm_id) {

	}

	/**
	 * 制造单/委外单:有出入库记录的不能删除明细行
	 * */
	public void make_edit_changeprcode(int mm_id) {
		Object[] objs = baseDao.getFieldsDataByCondition("makematerial", new String[] { "mm_code", "mm_detno" }, "mm_id=" + mm_id);
		if (objs != null && objs[0] != null) {
			Object[] objs2 = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_piclass", "pd_inoutno" }, "pd_ordercode='"
					+ objs[0] + "' and pd_orderdetno='" + objs[1] + "'");
			if (objs2 != null && objs2[0] != null) {
				BaseUtil.showError("已经发生了出入库！单据：" + objs2[0] + " 单号:" + objs2[1] + ",不能删除或修改物料！");
			}
		}
	}

	/**
	 * 计算本次可领料数 替代料可领料数(最大套料数)
	 * 
	 * @param maidlist
	 *            {String} Make表ID 用,隔开
	 */
	public void make_makematerial_setthisqty(String condition) {
		String maidlist = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ma_id) from (select distinct ma_id from make left join makematerial on ma_id=mm_maid where "
						+ condition + ")", String.class);
		makeDao.setThisQty(null, null, maidlist);
	}

	/**
	 * 完工入库单删除 还原制造单数量
	 */
	public void ProdInOut_Makein_delete(Integer id) {
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdIoDetail",
				new String[] { "pd_ordercode", "pd_inqty", "nvl(pd_qcid,0)" }, "pd_piid=" + id);
		for (Object[] obj : objs) {
			if (obj != null && obj[0] != null) {
//				if (Integer.valueOf(obj[2].toString()) == 0) {
					baseDao.updateByCondition("Make", " ma_tomadeqty=nvl(ma_tomadeqty,0)-" + Double.valueOf(String.valueOf(obj[1])),
							" ma_code='" + obj[0] + "'");
//				}
				baseDao.updateByCondition("QUA_VerifyApplyDetailDet",
						"ved_turnqty=nvl(ved_turnqty,0)-" + Double.valueOf(String.valueOf(obj[1])), "ved_id=" + obj[2]);
			}
		}
	}

	/**
	 * 完工入库单删除明细 还原制造单数量
	 */
	public void ProdInOut_Makein_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ProdIoDetail", new String[] { "pd_ordercode", "pd_inqty", "nvl(pd_qcid,0)" },
				"pd_id=" + id);
		if (objs != null && objs[0] != null) {
//			if (Integer.valueOf(objs[2].toString()) == 0) {
				baseDao.updateByCondition("Make", " ma_tomadeqty=nvl(ma_tomadeqty,0)-" + Double.valueOf(String.valueOf(objs[1])),
						" ma_code='" + objs[0] + "'");
//			}
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet",
					"ved_turnqty=nvl(ved_turnqty,0)-" + Double.valueOf(String.valueOf(objs[1])), "ved_id=" + objs[2]);
		}
	}

	/**
	 * kbt yaozx@13-08-23 打印次数的限制 printtimes
	 */
	public void ProdInOut_CheckprintTimes(Integer id) {
		if (!"admin".equals(SystemSession.getUser().getEm_type())) {
			Object status = baseDao.getFieldDataByCondition("printtimes", "pt_piname", "pt_piid=" + id);
			if (status != null) {
				BaseUtil.showError("该单据已经被打印过！打印人是:" + status + ".再次打印请找超级用户!");
			} else {
				String sql = "insert into printtimes(pt_id,pt_status,pt_piname,pt_piid)values(?,?,?,?)";
				baseDao.execute(sql, new Object[] { baseDao.getSeqId("printtimes_SEQ"), "1", SystemSession.getUser().getEm_name(), id });
			}
		}
	}

	/**
	 * 完工入库单更新 还原制造单数量
	 */
	public void ProdInOut_Makein_update(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Integer id = Integer.valueOf(store.get("pi_id").toString());
		List<Object[]> objs = baseDao.getFieldsDatasByCondition(" ProdIoDetail", new String[] { "pd_pdno", "pd_ordercode", "pd_prodcode",
				"pd_inqty", "pd_qcid" }, "pd_piid=" + id);
		// 与检验单合格数比较 入库数大于 检验数 不让更新
		for (Map<Object, Object> s : gstore) {
			if (Integer.valueOf(s.get("pd_id").toString()) != 0) {
				for (Object[] objects : objs) {
					if (objects != null) {
						if (String.valueOf(objects[0]).equals(String.valueOf(s.get("pd_pdno")))) {
							if (objects[1].toString().equals(s.get("pd_ordercode").toString())) {
								double qty = Double.valueOf(String.valueOf(objects[3])) - Double.valueOf(String.valueOf(s.get("pd_inqty")));
								if (s.get("pd_qcid") != null && Integer.valueOf(s.get("pd_qcid").toString()) != 0) {
									Object[] okqty = baseDao.getFieldsDataByCondition(
											"QUA_VerifyApplyDetailDet left join QUA_VerifyApplyDetail on ved_veid=ve_id", new String[] {
													"ved_okqty", "ve_criqty" }, "ved_id=" + s.get("pd_qcid"));
									SqlRowList sl = baseDao.queryForRowSet("select sum(pd_inqty) from prodiodetail where pd_qcid="
											+ s.get("pd_qcid") + " and pd_piclass in ('采购验收单','委外验收单','完工入库单')");
									if (sl.next()) {
										if (okqty != null && Integer.parseInt(okqty[1].toString()) == 0) {
											if (NumberUtil.compare(sl.getGeneralDouble(1) - (qty), Double.parseDouble(okqty[0].toString()),
													4) == 1) {
												BaseUtil.showError("已转完工入库数量大于对应检验合格数!序号:" + s.get("pd_pdno"));
											}
										}
									}
									baseDao.updateByCondition("QUA_VerifyApplyDetailDet", "ved_turnqty=NVL(ved_turnqty,0)-(" + qty + ")",
											"ved_id=" + objects[4]);
								} else {
									baseDao.updateByCondition("make", " ma_tomadeqty=NVL(ma_tomadeqty,0)-(" + qty + ")", "ma_code='"
											+ objects[1] + "'");
								}
							} else {
								baseDao.updateByCondition("make",
										"  ma_tomadeqty=NVL(ma_tomadeqty,0)-(" + Double.valueOf(String.valueOf(objects[3])) + ")",
										"ma_code='" + objects[1] + "'");
								baseDao.updateByCondition("make",
										"  ma_tomadeqty=NVL(ma_tomadeqty,0)+(" + Double.valueOf(s.get("pd_inqty").toString()) + ")",
										"ma_code='" + s.get("pd_ordercode") + "'");
							}
						}
					}
				}
			} else {
				baseDao.updateByCondition("make", " ma_tomadeqty=ma_tomadeqty+(" + Double.valueOf(String.valueOf(s.get("pd_inqty"))) + ")",
						"ma_code='" + s.get("pd_ordercode") + "'");
			}
		}
	}

	/**
	 * 制造单齐料套料查询 查询之前更新 工单最大领料套数
	 * 
	 * @author madan
	 */
	public void make_query_canmade(String condition) {
		SqlRowList rs = baseDao.queryForRowSet("select WMSYS.WM_CONCAT(tt.ma_id) from (select ma_id from make where " + condition + ") tt");
		if (rs.next()) {
			makeDao.setMaxCanMadeqty(rs.getString(1));
		}
	}

	/**
	 * BOM多级展开前，调用存储过程写入BOMStruct
	 */
	public void bom_query_struct(String condition) {
		String pr_code = null, bomid = null;
		if (condition != null && condition.length() > 0) {
			if (condition.indexOf("bs_topmothercode") != -1) {
				pr_code = condition.substring(condition.indexOf("bs_topmothercode")).split("=")[1].split("'")[1].trim();
			}
			if (condition.indexOf("bs_topbomid") != -1) {
				bomid = condition.substring(condition.indexOf("bs_topbomid")).split("=")[1].split("'")[1].trim();
			} else if (condition.indexOf("bs_sonbomid") != -1) {
				bomid = condition.substring(condition.indexOf("bs_sonbomid")).split("=")[1].split("'")[1].trim();
			}
			try {
				if (Integer.parseInt(bomid) <= 0) {
					BaseUtil.showError("请正确填写BOMID和母件料号");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("请正确填写BOMID和母件料号");
				return;
			}

			if (bomid != null && !bomid.equals("") && bomid.matches("\\d*") && pr_code != null && !pr_code.equals("")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select bo_id,pr_specdescription from product left join bom on (pr_code=bo_mothercode or pr_refno=bo_mothercode) where"
								+ " pr_code='" + pr_code + "' and bo_id=" + bomid);
				if (rs.next()) {
					String res = baseDao.callProcedure("MM_SetProdBomStruct",
							new Object[] { rs.getInt("bo_id"), rs.getString("pr_specdescription") });
					if (res != null && res.length() > 0) {
						BaseUtil.showError(res);
					}
				} else {
					BaseUtil.showError("找不到指定的父件编号或BOMID.");
				}
			}
		} else {
			BaseUtil.showError("请正确填写BOMID和母件料号");
		}
	}

	/**
	 * BOM多级展开后，提示未展开成功的物料
	 */
	public void bom_query_struct_result(String condition) {
		String pr_code = null, bomid = null;
		if (condition != null && condition.length() > 0) {
			if (condition.indexOf("bs_topmothercode") != -1) {
				pr_code = condition.substring(condition.indexOf("bs_topmothercode")).split("=")[1].split("'")[1].trim();
			}
			if (condition.indexOf("bs_topbomid") != -1) {
				bomid = condition.substring(condition.indexOf("bs_topbomid")).split("=")[1].split("'")[1].trim();
			} else if (condition.indexOf("bs_sonbomid") != -1) {
				bomid = condition.substring(condition.indexOf("bs_sonbomid")).split("=")[1].split("'")[1].trim();
			}
			if (!bomid.equals("") && bomid != null && !pr_code.equals("") && pr_code != null) {
				SqlRowList rs = baseDao
						.queryForRowSet("select bo_id,pr_specdescription from product left join bom on (pr_code=bo_mothercode or pr_refno=bo_mothercode) where"
								+ " pr_code='" + pr_code + "' and bo_id=" + bomid);
				if (rs.next()) {
					if (!rs.getString("pr_specdescription").equals("") && !rs.getString("pr_specdescription").equals(null)) {
						SqlRowList rs0 = baseDao
								.queryForRowSet("select WMSYS.WM_CONCAT(bs_soncode) from bomstruct,product where pr_code=bs_soncode and bs_topbomid="
										+ bomid + " and bs_topmothercode='" + pr_code + "' and pr_specvalue='NOTSPECIFIC'");
						if (rs0.hasNext()) {
							BaseUtil.showError("物料:" + rs0.getString(0) + "没有指定特征值对应的实体物料");
						}
					}
				}
			}
		}
	}

	/**
	 * 物料综合查询
	 * 
	 * @param condition
	 */
	public void product_query_before(String condition) {
		String pr_code = "";
		if (condition.indexOf("pr_code") != -1) {
			pr_code = condition.substring(condition.indexOf("pr_code")).split("=")[1].split("'")[1].trim();

			String res = baseDao.callProcedure("MM_RefreshProdQTY", new Object[] { pr_code, null });
			if (res != null && res.length() > 0) {
				BaseUtil.showError(res);
			}
		} else {
			BaseUtil.showError("请填写物料编号");
		}

	}

	/**
	 * 物料综合查询 有TAB页的
	 * 
	 * @param condition
	 */
	public void productWithTab_query_before(String condition) {

		if (condition.indexOf("pr_code") != -1) {
			condition = condition.substring(condition.indexOf("(") + 1, condition.indexOf(")")).trim();
		}
		condition = condition.substring(1, condition.length() - 1);
		String res = baseDao.callProcedure("MM_RefreshProdQTY", new Object[] { null, condition });
		if (res != null && res.length() > 0) {
			BaseUtil.showError(res);
		}

	}

	/**
	 * 集团物料综合查询
	 * 
	 * @param condition
	 */
	public void product_query_before_Group(String condition) {
		String pr_code = "";
		if (condition.indexOf("pr_code") != -1) {
			pr_code = condition.substring(condition.indexOf("pr_code")).split("=")[1].split("'")[1].trim();

			String res = baseDao.callProcedure("MM_RefreshProdQTY_G", new Object[] { pr_code, null });
			if (res != null && res.length() > 0) {
				BaseUtil.showError(res);
			}
		} else {
			BaseUtil.showError("请填写物料编号");
		}
	}

	/**
	 * BOM多级反查，调用存储过程MM_BomMutiBack
	 */
	public void bomStruct_manyQuery(String condition) {
		String pr_code = null;
		if (condition != null && condition.length() > 0) {
			if (condition.indexOf("bm_prcode") != -1) {
				pr_code = condition.substring(condition.indexOf("bm_prcode")).split("=")[1].split("'")[1].trim();
				if (pr_code != null && !pr_code.equals("")) {
					SqlRowList rs = baseDao.queryForRowSet("select pr_code from product where pr_code='" + pr_code + "'");
					if (rs.next()) {
						String res = baseDao.callProcedure("MM_BomMutiBack", new Object[] { rs.getString(1), 0 });
						if (res != null && res.length() > 0) {
							BaseUtil.showError(res);
						}
					} else {
						BaseUtil.showError("物料编号不存在");
					}
				}

			}
		}
	}

	/**
	 * MakeMaterialChange 提交前核对数量
	 * 
	 * @param mc_id
	 * @param language
	 * @param employee
	 */
	public void checkbeforeSubmit(Integer mc_id) {
		String sql = "SELECT  * from MakematerialChangeDet left join make on ma_code=md_makecode "
				+ "left join product on pr_code=md_prodcode left join makematerial on mm_code=md_makecode"
				+ " and mm_detno=md_mmdetno WHERE md_mcid=" + mc_id + " and NVL(md_didstatus,' ')<>'执行成功' and NVL(md_didstatus,' ')<>'已取消'";
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		StringBuffer errMessage = new StringBuffer();
		while (sqlRowList.next()) {
			if (sqlRowList.getString("md_type").equals("禁用 ") && sqlRowList.getInt("mm_havegetqty") > 0) {
				errMessage.append("工单编号:" + sqlRowList.getString("ma_code") + "序号为:" + sqlRowList.getInt("mm_detno") + " 已经领料不能禁用！\n");
			}
			if (sqlRowList.getString("md_type").equals("修改") && sqlRowList.getInt("md_qty") < sqlRowList.getInt("mm_havegetqty")) {
				errMessage.append("工单编号:" + sqlRowList.getString("ma_code") + "序号为:" + sqlRowList.getInt("mm_detno") + " 新数量小于已领数量");
			}
			/*
			 * if (sqlRowList.getObject("mm_materialstatus")!=null &&
			 * sqlRowList.getString("mm_materialstatus").length()>3){
			 * BaseUtil.showError("序号:" +
			 * sqlRowList.getInt("mm_detno")+"是跳层物料,不能直接禁用，需要对该料的下阶明细物料进行变更"); }
			 */
		}
		if (errMessage.length() != 0) {
			BaseUtil.showError(errMessage.toString());
		}
	}

	/**
	 * 委外加工单 审核之前要判定 物料编号是否存在
	 * */
	public void checkexsitproduct(Integer id) {
		SqlRowList sl = baseDao.queryForRowSet("select mm_prodcode  from MakeMaterial where mm_maid=" + id);
		while (sl.next()) {
			Object data = baseDao.getFieldDataByCondition("Product", "pr_code", "pr_code='" + sl.getObject(1) + "'");
			if (data == null) {
				BaseUtil.showError("加工单明细料号:" + sl.getObject(1) + " 不存在! 不能审核!");
			}
		}

	}

	/**
	 * 特征项 实体料号 为虚拟件时候 必须判断 bom 母件状态为 '已提交' 和 '已审核'
	 **/
	public void Featrue_check_bom(Integer id) {
		SqlRowList rs0 = baseDao.queryForRowSet("select fp_prodcode from FeatureProduct where fp_id=" + id);
		if (rs0.next()) {
			SqlRowList rs = baseDao
					.queryForRowSet("select pr_code,NVL(bo_id,0) bo_id ,bo_statuscode from product left join bom on bo_mothercode=pr_code where pr_code='"
							+ rs0.getString("fp_prodcode") + "' and  pr_supplytype='VIRTUAL' ");
			if (rs.next()) {
				if (rs.getInt("bo_id") <= 0) {
					BaseUtil.showError("实体物料为虚拟件，必须建立BOM！");
				} else if (!rs.getString("bo_statuscode").equals("COMMITED") && !rs.getString("bo_statuscode").equals("AUDITED")) {
					BaseUtil.showError("实体物料为虚拟件，对应的BOM状态必须是已提交或已审核！");
				}
			}
		}
	}

	/**
	 * 特征项 实体料号 为虚拟件时候 必须判断 bom 母件状态为 '已提交' 和 '已审核'
	 **/
	public void Featrue_check_bom_note(Integer id) {
		Object[] values = baseDao.getFieldsDataByCondition("FeatureProduct left join Product on fp_prodcode=pr_code",
				"pr_supplytype,pr_code", "fp_id=" + id);
		if (values[0].equals("VIRTUAL")) {
			// 如果虚拟件
			Object bo_statuscode = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_mothercode='" + values[1] + "'");
			if (!(bo_statuscode.equals("COMMITED") || bo_statuscode.equals("AUDITED"))) {
				BaseUtil.showErrorOnSuccess("所选实体料号为虚拟件且BOM不是已提交和已审核状态  不能提交!");
			}
		}

	}

	/**
	 * 生产变更单提交和审核之前，要求新加工数量不能小于已完工数量
	 * 
	 * @author madan
	 **/
	public void make_commit_before_newqtycheck(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select md_makecode from makeChangedetail makeChangedetail left join make on ma_code=md_makecode where md_mcid=? and md_newqty<ma_madeqty",
						id);
		while (rs.next()) {
			BaseUtil.showError("制造单[" + rs.getString("md_makecode") + "]更新后的数量小于已完工的数量，不能进行更新操作");
		}
	}

	/**
	 * 生产变更单提交和审核之前，要求已领数量大于变更后的需求数, 不能变更
	 * 
	 * @author madan
	 **/
	public void make_commit_before_needqtycheck(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("Select * from makeChangedetail left join make on ma_code=md_makecode where md_mcid=?", id);
		float newqty = 0;
		float oldqty = 0;
		while (rs.next()) {
			newqty = Float.parseFloat(rs.getString("md_newqty").toString());
			oldqty = Float.parseFloat(rs.getString("md_oldqty").toString());
			if (rs.getString("ma_statuscode") == "FINISH") {
				BaseUtil.showError("制造单[" + rs.getString("md_makecode") + "]已结案");
			}
			if (newqty > 0 && newqty < oldqty) {
				SqlRowList rs1 = baseDao
						.queryForRowSet("Select mm_detno from makematerial left join product on mm_prodcode=pr_code where mm_code='"
								+ rs.getString("md_makecode") + "' and NVL(pr_putouttoint,0)=0"
								+ " and ((nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-1>=mm_qty*" + newqty + "*1.00/" + oldqty
								+ " and round(mm_qty,0)=round(mm_qty,3)) or (nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)>mm_qty*" + newqty
								+ "*1.00/" + oldqty + " and round(mm_qty,0)<>round(mm_qty,3))) and nvl(mm_oneuseqty,0)*"
								+ rs.getDouble("ma_qty") + "-0.01<=mm_qty");
				while (rs1.next()) {
					BaseUtil.showError("制造单[" + rs.getString("md_makecode") + "],序号[" + rs1.getString("mm_detno") + "]已领数量大于变更后的需求数, 不能变更!");
				}
			}
		}
	}

	/**
	 * CheckECR 禁用物料必须填写旧料处理
	 * 
	 * @author zhongyl
	 **/
	public void CheckECR_oldproddealcheck(Integer ecr_id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select ecrd_detno from  ecrdetail  where ecrd_ecrid='" + ecr_id
				+ "' and ecrd_type in('BATCHSWITCH','DISABLE') and NVL(ecrd_oldproddeal,' ')=' '   ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			BaseUtil.showError("序号[" + rs.getString("ecrd_detno") + "]必须填写[旧料处理]!'");
		}
	}

	/**
	 * CheckECR BOM等级的参与MRP属性bl_ifmrp不一样的不能录入在同一张ECR评审表
	 * 
	 * @author zhongyl
	 **/
	public void CheckECR_BOMMrpCheck(Integer ecr_id) {
		String SQLStr = "";
		SQLStr = "select  count(distinct NVL(bl_ifmrp,0)) c from   ecrdetail,bom,bomlevel    where ecrd_ecrid='" + ecr_id
				+ "'  and ecrd_bomid>0 and ecrd_bomid=bo_id and bo_level=bl_code  ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("c") > 1) {
				BaseUtil.showError("参与MRP属性不一致的BOM录入在同一张ECR评审表'");
			}
		}
	}

	/**
	 * ECN 判断是否已经存在其它未执行ECN
	 * 
	 * @author zhongyl
	 **/
	public void ecn_check_otherECN(Integer ecn_id) {
		String SQLStr = "";
		SqlRowList rs, rs0;
		SQLStr = "select ed_detno,ed_boid,ed_soncode from  ecndetail,ecn  where ecn_id=ed_ecnid and ed_ecnid='" + ecn_id + "' ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			// 判断是否已经存在本BOM明细变更的其它未执行ECN
			SQLStr = "select * from  ecndetail,ecn where ecn_id=ed_ecnid and (ecn_checkstatuscode='COMMITED' and NVL(ed_didstatus,' ') in ('打开',' ') and ecn_type='NOW' or (ecn_checkstatuscode in('AUDITED','COMMITED')  and NVL(ed_didstatus,' ') in(' ','打开') and ecn_type='AUTO')) and ed_ecnid<>'"
					+ ecn_id + "' and ed_boid='" + rs.getInt("ed_boid") + "' and ed_soncode='" + rs.getString("ed_soncode") + "' ";
			rs0 = baseDao.queryForRowSet(SQLStr);
			if (rs0.next()) {
				BaseUtil.showError("行号[" + rs.getString("ed_detno") + "]已经存在未执行的ECN[" + rs0.getString("ecn_code") + "]序号:"
						+ rs0.getInt("ed_detno") + "!'");
			}
		}

	}

	/**
	 * MakeSendLS 拉式发料自动过帐领料单
	 * 
	 * @author zhongyl
	 **/
	public void Make_LSSend_autoLLpost(Integer llpiid) {
		String SQLStr = "";
		String Outpiclass = "";
		SqlRowList rs;
		SQLStr = "select pi_class from prodinout where pi_id=" + llpiid;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			Outpiclass = rs.getObject("pi_class").toString();
			if (Outpiclass.equals("生产领料单")) {
				prodInOutService.postProdInOut(Integer.parseInt(llpiid.toString()), "ProdInOut!Picking");
			} else if (Outpiclass.equals("委外领料单")) {
				prodInOutService.postProdInOut(Integer.parseInt(llpiid.toString()), "ProdInOut!OutsidePicking");
			}
		}

	}

	/**
	 * MakeSendLS 拉式发料 拨出单过账时自动过账领料单
	 * 
	 * @author zhongyl
	 **/
	public void Make_LSSend_BCAutoPostLL(Integer pi_id) {
		String SQLStr = "";
		String Outpiclass = "";
		SqlRowList rs;
		SQLStr = "select pi_inoutno from prodinout where pi_id=" + pi_id + " and pi_statuscode='POSTED'";
		SqlRowList rs0 = baseDao.queryForRowSet(SQLStr);
		if (rs0.next()) {
			SQLStr = "select pi_id,pi_class from prodinout where pi_fromcode='" + rs0.getString("pi_inoutno")
					+ "' and pi_class='生产领料单' and pi_statuscode='UNPOST' ";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				Outpiclass = rs.getObject("pi_class").toString();
				if (Outpiclass.equals("生产领料单")) {
					prodInOutService.postProdInOut(rs.getInt("pi_id"), "ProdInOut!Picking");
				} else if (Outpiclass.equals("委外领料单")) {
					prodInOutService.postProdInOut(rs.getInt("pi_id"), "ProdInOut!OutsidePicking");
				}
			}
		}
	}

	/**
	 * MakeSendLS 拉式发料 拨出单过账时自动过账领料单
	 * 
	 * @author zhongyl
	 **/
	public void Make_LSSend_BCUnPostCheck(Integer pi_id) {
		String SQLStr = "";
		String Outpiclass = "";
		SqlRowList rs;
		SQLStr = "select pi_inoutno from prodinout where pi_id=" + pi_id + " and pi_statuscode='POSTED'";
		SqlRowList rs0 = baseDao.queryForRowSet(SQLStr);
		if (rs0.next()) {
			SQLStr = "select pi_id,pi_class,pi_inoutno from prodinout where pi_fromcode='" + rs0.getString("pi_inoutno")
					+ "' and pi_class='生产领料单' and pi_statuscode='UNPOST' ";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				Outpiclass = rs.getObject("pi_class").toString();
				if (Outpiclass.equals("生产领料单")) {
					prodInOutService.postProdInOut(rs.getInt("pi_id"), "ProdInOut!Picking");
				} else if (Outpiclass.equals("委外领料单")) {
					prodInOutService.postProdInOut(rs.getInt("pi_id"), "ProdInOut!OutsidePicking");
				}
				SQLStr = "select pi_statuscode from prodinout where pi_id=" + rs.getInt("pi_id") + " and pi_statuscode='POSTED' ";
				rs0 = baseDao.queryForRowSet(SQLStr);
				if (rs0.next()) {
					BaseUtil.showErrorOnSuccess(Outpiclass + "：" + rs.getString("pi_inoutno") + "过账成功!");
				} else {
					BaseUtil.showErrorOnSuccess(Outpiclass + "：" + rs.getString("pi_inoutno") + "过账不成功!");
				}
			}
		}
	}

	/**
	 * 推式物料过账自动产生拉式物料的领料单
	 * 
	 * @author zhongyl
	 **/
	public void Make_LSSend_SendByPush(Integer id) {
		String SQLStr = "";
		String Newpiclass = "";
		String Newinoutno = "";
		String fromClass = "";
		String wipwhcode = "";
		String pi_cardcode = "";
		String pi_receivecode = "";
		SqlRowList rs, rs2;
		SQLStr = "select pi_class,pi_inoutno,pi_cardcode,nvl(pi_receivecode,pi_cardcode) pi_receivecode from prodinout where pi_id=" + id;
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			rs.getObject("pi_inoutno").toString();
			fromClass = rs.getObject("pi_class").toString();
			if (fromClass.equals("生产领料单") || fromClass.equals("完工入库单")) {
				Newpiclass = "生产领料单";
			} else if (fromClass.equals("委外领料单") || fromClass.equals("委外验收单")) {
				Newpiclass = "委外领料单";
				pi_cardcode = rs.getString("pi_cardcode");
				pi_receivecode = rs.getString("pi_receivecode");
			}
		} else {
			return;// 单号不存在
		}
		// 获取默认线边仓
		if (fromClass.equals("生产领料单") || fromClass.equals("完工入库单")) {
			SQLStr = "select wh_code,wh_description from warehouse where wh_ifwip<>0 and wh_description not like '%外发%' and wh_description not like '%委外%' order by wh_code";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				wipwhcode = rs.getString("wh_code");
				rs.getString("wh_description");
			}
		} else if (fromClass.equals("委外领料单") || fromClass.equals("委外验收单")) {
			// 取委外商定义的线边仓
			SQLStr = "select  wh_code,wh_description from vendor,warehouse where ve_code='" + pi_cardcode + "' and ve_whcode=wh_code";
			rs = baseDao.queryForRowSet(SQLStr);
			if (rs.next()) {
				wipwhcode = rs.getString("wh_code");
				rs.getString("wh_description");
			} else {// 取通用的外发线边仓
				SQLStr = "select wh_code,wh_description from warehouse where wh_ifwip<>0 and wh_ifoutmake<>0 order by wh_code";
				rs = baseDao.queryForRowSet(SQLStr);
				if (rs.next()) {
					wipwhcode = rs.getString("wh_code");
					rs.getString("wh_description");
				}
			}
		}
		// --判断是否需要产生领料单，条件:没有从线边仓的领料单、有拉式物料
		SQLStr = "SELECT distinct ma_id FROM ProdIODetail left join make on pd_ordercode=ma_code WHERE	pd_piid=" + id;
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			makeDao.setThisQty(null, rs.getInt("ma_id"), null);
		}
		// --判断是否需要产生领料单，条件:没有从线边仓的领料单、有拉式物料
		SQLStr = "SELECT pd_ordercode FROM ProdIODetail WHERE	pd_piid="
				+ id
				+ " and pd_ordercode  in (select mm_code from MakeMaterial  where mm_code=pd_ordercode and mm_supplytype in('PULL','拉式') and mm_thisqty>0 and mm_qty-NVL(mm_havegetqty,0)+NVL(mm_addqty,0)-NVL(mm_returnmqty,0)>0  and pd_ordercode <>' ' )"
				+ " and pd_ordercode not in (select pd_ordercode from ProdIODetail where pd_piclass='" + Newpiclass
				+ "' and pd_ordercode <>' ' and pd_whcode='" + wipwhcode + "' ) ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (!rs.hasNext()) {
			return;// 不需要产生拉式领料
		}
		JSONObject j = null;
		String newcaller = "";
		if (Newpiclass.equals("生产领料单")) {
			newcaller = "ProdInOut!Picking";
			j = makeDao.newProdIO(wipwhcode, Newpiclass, newcaller, null);
		} else {
			newcaller = "ProdInOut!OutsidePicking";
			j = makeDao.newProdIOWithVendor(wipwhcode, pi_cardcode, pi_receivecode, Newpiclass, newcaller, null);
		}
		if (j != null) {
			Newinoutno = j.getString("pi_inoutno");
			j.getInt("pi_id");
			int detno = 1;
			SQLStr = "SELECT distinct pd_ordercode FROM ProdIODetail WHERE	pd_piid="
					+ id
					+ " and pd_ordercode  in (select mm_code from MakeMaterial  where mm_code=pd_ordercode and mm_supplytype in('PULL','拉式') and pd_ordercode <>' ' )"
					+ " and pd_ordercode not in (select pd_ordercode from ProdIODetail where pd_piclass='" + Newpiclass
					+ "' and pd_ordercode <>' ' and pd_whcode='" + wipwhcode + "' ) ";
			rs = baseDao.queryForRowSet(SQLStr);
			while (rs.next()) {
				SQLStr = " SELECT	mm_id,ma_qty,mm_detno,mm_prodcode,mm_thisqty FROM	Make,MakeMaterial WHERE mm_maid=ma_id "
						+ "AND ma_code='" + rs.getString("pd_ordercode")
						+ "' AND mm_supplytype in('拉式','PULL') AND mm_oneuseqty>0.00000000001 and mm_thisqty>0 ORDER	BY mm_detno";
				rs2 = baseDao.queryForRowSet(SQLStr);
				while (rs2.next()) {
					makeDao.turnOutWh(Newinoutno, detno++, Newpiclass, Integer.parseInt(rs2.getString("mm_id")), rs2.getInt("mm_detno"),
							rs2.getDouble("mm_thisqty"));
				}
			}
		}

	}

	/***
	 * KYS 研发类的BOM控制不让反审核
	 * */
	public void IsCanresAuditBOM(Integer id) {
		Object kind = baseDao.getFieldDataByCondition("BOM", "bo_kind", "bo_id=" + id);
		if (!"研发".equals(kind)) {
			BaseUtil.showError("只有研发BOM才允许反审核!");
		}
	}

	/**
	 * 委外 加工单价不能为0
	 * */
	public void make_exp_pricezero(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		rs = baseDao.queryForRowSet("select nvl(ma_price,0) ma_price,ma_prodcode,NVL(ma_vendcode,' ') ma_vendcode  from make where ma_id="
				+ id + " and NVL(ma_servicer,0)=0 ");
		if (rs.next()) {
			if (rs.getDouble("ma_price") <= 0) {
				// 判断是否存在委外单价0的有效定价
				SQLStr = "select pp_id from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id where  ppd_prodcode='"
						+ rs.getString("ma_prodcode")
						+ "' and ppd_vendcode='"
						+ rs.getString("ma_vendcode")
						+ "' and ppd_price=0 and pp_kind='委外' and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' ";
				rs = baseDao.queryForRowSet(SQLStr);
				if (!rs.hasNext()) {
					BaseUtil.showError("委外加工单价不能为0");
				}
			}
		}
	}

	/**
	 * BOM提交前必须子件BOM都已经提交
	 * 
	 * @author zhongyl
	 * */
	public void bom_check_sonbomstatus_commited(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select wm_concat(bd_detno) as detnostr,NVL(max(bd_detno),0) as detno from bomdetail left join product on pr_code=bd_soncode left join bom on bo_mothercode=bd_soncode where bd_bomid="
				+ id
				+ " and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pr_specvalue,' ')<>'SPECIFIC' and pr_manutype in ('MAKE','OSMAKE') and NVL(bo_statuscode,' ') not in ('COMMITED','AUDITED') ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("detno") > 0) {
				BaseUtil.showError("序号：" + rs.getString("detnostr") + "子件BOM未建立或未提交，母件BOM不能提交");
			}

		}
	}

	/**
	 * BOM审核前必须子件BOM都已经审核
	 * 
	 * @author zhongyl
	 * */
	public void bom_check_sonbomstatus_audit(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select wm_concat(bd_detno) as detnostr,NVL(max(bd_detno),0) as detno from bomdetail left join product on pr_code=bd_soncode left join bom on bo_mothercode=bd_soncode where bd_bomid="
				+ id
				+ " and NVL(bd_usestatus,' ')<>'DISABLE' and NVL(pr_specvalue,' ')<>'SPECIFIC' and pr_manutype in ('MAKE','OSMAKE') and NVL(bo_statuscode,' ')<>'AUDITED' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("detno") > 0) {
				BaseUtil.showError("序号：" + rs.getString("detnostr") + "子件BOM未建立或未审核，母件BOM不能提交");
			}

		}
	}

	/**
	 * BOM反审核前父级BOM必须先反审核
	 * 
	 * @author zhongyl 2016 2 17
	 * */
	public void bom_check_motherbomstatus_resAudit(Integer id) {
		Object mothercode = baseDao.getFieldDataByCondition("BOM", "bo_mothercode", "bo_id=" + id);
		SqlRowList rs = baseDao.queryForRowSet("select 1 from product where pr_code='" + mothercode
				+ "' and (pr_manutype in ('MAKE','OSMAKE') OR pr_supplytype='VIRTUAL') ");
		if (rs.next()) {// 母件是制造件或者虚拟件的，如果有父级BOM的情况不能反审核
			Object errProds = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(bo_id) from bomdetail,bom where bo_id=bd_bomid and bd_soncode='" + mothercode
							+ "' and bo_statuscode in ('COMMITED','AUDITED') and NVL(bd_usestatus,' ')<>'DISABLE'  ", String.class);
			if (errProds != null) {
				BaseUtil.showError("母件已经在BOM:" + errProds + "中使用，需先反审核父级BOM");
			}
		}
	}

	/**
	 * BOM提交的时候出现在BOM配套表里面的子件必须出现在母件BOM里面
	 * 
	 * @author zhongyl
	 * */
	public void bom_check_bomset(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "SELECT count(1) detno ,wm_concat(bsd_prodcode) as detnostr FROM bom,Bomsetdetail,bomset WHERE  bo_id='"
				+ id
				+ "' and bo_mothercode=bs_mothercode and bsd_bsid=bs_id and (bsd_mothercode=bo_mothercode or NVL(bsd_mothercode,' ')=' ')  and bsd_prodcode not in (select bd_soncode from bomdetail where bd_bomid='"
				+ id + "' )  ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("detno") > 0) {
				BaseUtil.showError("物料：" + rs.getString("detnostr") + "是BOM配套表中的子件，但本BOM明细中未录入");
			}

		}
	}

	/**
	 * BOM提交的时候判断单位用量和位号数量是否一致
	 * 
	 * @author zhongyl
	 * */
	public void bom_check_locationQTY(Integer id) {
		String SQLStr = "", location = "", errstr = "";
		SqlRowList rs;
		SQLStr = "select * from bomdetail where bd_bomid=" + id + " and NVL(bd_location,' ')<>' ' and NVL(bd_usestatus,' ')<>'DISABLE'  ";
		rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {
			location = rs.getString("bd_location");
			if (location != null && location.toString().trim().length() > 0) {
				int num = 0;
				num = location.toString().split(",").length;
				if (rs.getDouble("bd_baseqty") - num != 0) {
					errstr += ",序号:" + rs.getInt("bd_detno") + "用量" + rs.getString("bd_baseqty") + "位号" + num + "个";
				}
			}
		}
		if (!errstr.equals("")) {
			BaseUtil.showError("单位用量和位号个数不匹配" + errstr);
		}
	}

	/**
	 * 委外单自动获取定价表的供应商和单价
	 * 
	 * @author mad
	 **/
	public void make_exp_setpricevendor(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String vendcode = "", currency = "";
		double taxrate = 0;
		Object ma_id = store.get("ma_id").toString();
		SqlRowList rs = baseDao.queryForRowSet("select * from Make where  ma_id=" + ma_id + " and ma_tasktype='OS' ");
		if (rs.next()) {
			// 到物料核价单取单价
			JSONObject obj = null;
			obj = purchaseDao.getPriceVendor(rs.getString("ma_prodcode"), "委外", rs.getDouble("ma_qty"));
			if (obj != null) {
				double price = obj.getDouble("price");
				vendcode = obj.getString("vendcode");
				currency = obj.getString("currency");
				taxrate = obj.getDouble("taxrate");
				baseDao.updateByCondition("Make", "ma_vendcode='" + vendcode + "', ma_currency='" + currency + "',ma_taxrate= " + taxrate
						+ ", ma_price=round(" + price + ",8), ma_total=round(" + price + "*ma_qty,2),ma_tag=" + obj.getLong("ppd_id"),
						"ma_id =" + ma_id);

				baseDao.execute(
						"update make set (ma_paymentscode,ma_payments,ma_vendname)=(select ve_paymentcode,ve_payment,ve_name from vendor where ve_code=ma_vendcode) where ma_id=?",
						ma_id);
				// 更新汇率
				baseDao.execute("update make set ma_rate=(select cm_crrate from currencysmonth where ma_currency=cm_crname and "
						+ "cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_id=?", ma_id);

				int argCount = baseDao.getCountByCondition("user_tab_columns",
						"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
				if (argCount == 2) {
					baseDao.execute("update make set (MA_APVENDCODE,MA_APVENDNAME)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=ma_vendcode) where ma_id="
							+ ma_id);
				}
			}
		}
	}

	/**
	 * 生产日报：保存、更新时工时计算 天派电子、天派科技
	 * 
	 * @author mad
	 **/
	public void dispatch_save_getHours(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select nvl(sum(ad_zgs),0),nvl(sum(ad_zgs-ad_jjgs),0) from attendancedetail left join Attendance on ad_atid=at_id where at_attenddate=? and at_department=? and at_lowdepartname=?",
						new Object[] { store.get("di_date"), store.get("di_lowdepartname"), store.get("di_groupcode") });
		if (rs.next()) {
			// 更新投入工时,生产工时
			baseDao.execute(
					"update dispatch set di_putinhours=" + rs.getObject(1) + ", di_makehours=" + rs.getObject(2) + " where di_id=?",
					store.get("di_id"));
		}
		if ("SMT".equals(store.get("di_lowdepartname"))) {
			// 生产点数
			baseDao.execute(
					"update DispatchDetail set did_manupoints=(nvl(did_putinqty,0)+nvl(did_overqty,0))*nvl(did_quota,0) where did_diid=?",
					store.get("di_id"));
			baseDao.execute(
					"update Dispatch set di_totalmanupoints=(select sum(nvl(did_manupoints,0)) from DispatchDetail where did_diid=di_id) where di_id=?",
					store.get("di_id"));
			baseDao.execute(
					"update Dispatch set di_totaldestpoints=(select sum(nvl(did_destpoints,0)) from DispatchDetail where did_diid=di_id) where di_id=?",
					store.get("di_id"));
			baseDao.execute(
					"update Dispatch set di_jiadongrate=ROUND(di_totalmanupoints/di_totaldestpoints,2)*100 WHERE di_totaldestpoints>0 and di_id=?",
					store.get("di_id"));
		}
		if ("板卡".equals(store.get("di_lowdepartname"))) {
			// 完成工时
			baseDao.execute(
					"update Dispatch set di_overhours=(select sum(nvl(did_overqty,0)*nvl(did_quota,0)) from DispatchDetail where did_diid=di_id) where di_id=?",
					store.get("di_id"));
			// 每个制造单的完成工时
			baseDao.execute("update DispatchDetail set did_overhours=(nvl(did_overqty,0)*nvl(did_quota,0)) where did_diid=?",
					store.get("di_id"));
		} else if ("A2车间".equals(store.get("di_lowdepartname")) || "A3车间".equals(store.get("di_lowdepartname"))) {
			// 完成工时
			baseDao.execute(
					"update Dispatch set di_overhours=(select sum((nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0))) from DispatchDetail where did_diid=di_id) where di_id=?",
					store.get("di_id"));
			// 每个制造单的完成工时
			baseDao.execute(
					"update DispatchDetail set did_overhours=(nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0)) where did_diid=?",
					store.get("di_id"));
		} else {
			baseDao.execute(
					"update Dispatch set di_overhours=(select sum((nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0))) from DispatchDetail where did_diid=di_id) where di_id=?",
					store.get("di_id"));
		}
		baseDao.execute(
				"update Dispatch set di_putinqtys=(select sum(nvl(did_putinqty,0)) from DispatchDetail where did_diid=di_id) where di_id=?",
				store.get("di_id"));
		if ("前加工".equals(store.get("di_groupcode"))) {
			baseDao.execute(
					"update Dispatch set di_overqtys=(select sum(nvl(did_overqty,0)) from DispatchDetail where did_diid=di_id and nvl(did_stepname,'')<>'改修' and nvl(did_stepname,'')<>'客退') where di_id=?",
					store.get("di_id"));
		}
	}

	/**
	 * 生产日报：更新损失工时计算 天派电子、天派科技
	 * 
	 * @author shenj
	 **/
	public void lossWorkTime_save_getHours(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String SQLStr = "update Dispatch set di_losscode='" + store.get("lw_dicode") + "',di_lostedhours=" + store.get("lw_lostedhours")
				+ " where di_code='" + store.get("lw_dicode") + "' ";
		baseDao.execute(SQLStr);
		String SQLStr2 = "update Dispatch set di_makehours=nvl(di_putinhours,0)-nvl(di_lostedhours,0) where di_code='"
				+ store.get("lw_dicode") + "' ";
		baseDao.execute(SQLStr2);

	}

	/**
	 * 生产日报：更新出勤日报工时计算 天派电子、天派科技
	 * 
	 * @author shenj
	 **/
	public void attendance_save_getHours(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		String atcode = store.get("at_code").toString();
		String sqlstr = "update Dispatch set di_kaoqincode='" + atcode + "' where di_code='" + store.get("at_dicode") + "' ";
		baseDao.execute(sqlstr);
		// 投入工时
		String sqlstr2 = "update Dispatch set di_putinhours=(select sum(nvl(ad_zbgs,0)+nvl(ad_jbgs,0)+nvl(ad_dr,0)-nvl(ad_dc,0)-nvl(ad_fj,0)-nvl(ad_qq,0)) from AttendanceDetail where ad_code='"
				+ atcode + "') where di_code='" + store.get("lw_dicode") + "' ";
		baseDao.execute(sqlstr2);
		// 间接工时
		String sqlstr3 = "update Dispatch set di_jianjiehours=(select sum(nvl(ad_jjgs,0)) from AttendanceDetail where ad_code='" + atcode
				+ "') where di_code='" + store.get("at_dicode") + "' ";
		baseDao.execute(sqlstr3);
		// 生产工时
		String sqlstr4 = "update Dispatch set di_makehours=nvl(di_putinhours,0)-nvl(di_lostedhours,0)-nvl(di_jianjiehours,0) where di_code='"
				+ store.get("at_dicode") + "' ";
		baseDao.execute(sqlstr4);
		// 每个制造单的完成工时
		String sqlstr5 = "update DispatchDetail set did_overhours=(nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0)) where did_code='"
				+ store.get("at_dicode") + "' ";
		baseDao.execute(sqlstr5);
		Object obj = baseDao.getFieldDataByCondition("Dispatch", "di_lowdepartname", "di_code='" + store.get("at_dicode") + "'");
		if (obj.equals("板卡")) {
			// 完成工时
			String sqlstr6 = "update Dispatch set di_overhours=(select sum(nvl(did_overqty,0)*nvl(did_quota,0)) from DispatchDetail where di_code=did_code) where di_code='"
					+ store.get("at_dicode") + "' ";
			baseDao.execute(sqlstr6);
			// 每个制造单的完成工时
			String sqlstr7 = "update DispatchDetail set did_overhours=(nvl(did_overqty,0)*nvl(did_quota,0)) where did_code='"
					+ store.get("at_dicode") + "' ";
			baseDao.execute(sqlstr7);
		} else if (obj.equals("A2车间") || obj.equals("A3车间")) {
			// 完成工时
			String sqlstr8 = "update Dispatch set di_overhours=(select sum((nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0))) from DispatchDetail where di_code=did_code) where di_code='"
					+ store.get("at_dicode") + "' ";
			baseDao.execute(sqlstr8);
			// 每个制造单的完成工时
			String sqlstr9 = "update DispatchDetail set did_overhours=(nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0)) where did_code='"
					+ store.get("at_dicode") + "' ";
			baseDao.execute(sqlstr9);
		} else {
			String sqlstr10 = "update Dispatch set di_overhours=(select sum((nvl(did_putinqty,0)*nvl(did_quota,0)+nvl(did_overqty,0)*nvl(did_quota,0))) from DispatchDetail where di_code=did_code) where di_code='"
					+ store.get("at_dicode") + "' ";
			baseDao.execute(sqlstr10);
		}
		// 能率
		String sqlstr11 = "update Dispatch set di_powermake=round(di_overhours/di_makehours,2)*100 where di_code='"
				+ store.get("at_dicode") + "' and di_makehours>0";
		baseDao.execute(sqlstr11);
		// 生产性
		String sqlstr12 = "update Dispatch set di_makeperform=round(di_overhours/di_putinhours,2)*100 where di_code='"
				+ store.get("at_dicode") + "' and di_putinhours>0";
		baseDao.execute(sqlstr12);
		// 计划达成率
		Object[] objs = baseDao.getFieldsDataByCondition("DispatchDetail", new String[] { "sum(nvl(did_overqty,0))",
				"sum(nvl(did_planqty,0))" }, "did_code='" + store.get("at_dicode") + "'");
		double sumoverqty = 0;
		double sumplanqty = 0;
		sumoverqty = Double.parseDouble(objs[0].toString());
		sumplanqty = Double.parseDouble(objs[1].toString());
		if (objs != null) {
			String sqlstr14 = "update Dispatch set di_totalplanqtys=ROUND(" + objs[0] + ",2) where di_code='" + store.get("at_dicode")
					+ "' ";
			baseDao.execute(sqlstr14);
			if (sumplanqty > 0) {
				String sqlstr15 = "update Dispatch set di_planreachrate=ROUND(" + sumoverqty / sumplanqty + ",2)*100 where di_code='"
						+ store.get("at_dicode") + "' ";
				baseDao.execute(sqlstr15);
			}
		}

	}

	/**
	 * 2014年2月24日17:17:01
	 * 
	 * @author madan 委外加工单：提交之前判断委外商号是否填写
	 * @param id
	 * @param language
	 * @param employee
	 */

	public void makeOS_submit_before_checkvend(Integer id) {
		boolean nullVend = baseDao.checkIf("Make", "ma_id=" + id + " and nvl(ma_vendcode,' ')=' '");
		if (nullVend) {
			BaseUtil.showError("委外商号为空,不允许提交！");
		}
	}

	/**
	 * 万利达 转BOM 等级或者BOM类型变更之后 BOM-turnbom->after
	 * */
	public void BOM_turnlevel_after(String data) {
		String relaboid = "";
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		Object bo_relativecode = baseDao.getFieldDataByCondition("bom", "bo_relativecode", "bo_id=" + map.get("boid"));
		if (bo_relativecode != null && !bo_relativecode.equals("")) {
			SqlRowList rs = baseDao.queryForRowSet("select bo_id from bom where bo_relativecode='" + bo_relativecode.toString()
					+ "' and bo_level<>'" + map.get("bolevel") + "' and bo_id<>" + map.get("boid"));
			while (rs.next()) {
				map.remove("boid");
				map.put("boid", rs.getInt("bo_id"));
				// 更新bom
				Object isprocess = map.get("isprocess");
				if (Integer.parseInt(isprocess.toString()) == 1) {
					// 重置流程
					baseDao.updateByCondition("BOM", "bo_style='" + map.get("bostyle") + "',bo_level='" + map.get("bolevel")
							+ "',bo_auditman=null,bo_commitman=null,bo_recorder='" + SystemSession.getUser().getEm_name()
							+ "',bo_recorderid=" + SystemSession.getUser().getEm_id() + ",bo_status='在录入',bo_statuscode='ENTERING'",
							"bo_id=" + map.get("boid"));
				} else {
					// 不重置流程
					baseDao.updateByCondition("BOM", "bo_style='" + map.get("bostyle") + "',bo_level='" + map.get("bolevel")
							+ "',bo_recorder='" + SystemSession.getUser().getEm_name() + "',bo_recorderid="
							+ SystemSession.getUser().getEm_id(), "bo_id=" + map.get("boid"));
				}
				relaboid += "," + rs.getString("bo_id");
			}
			if (relaboid.length() > 0) {
				relaboid = relaboid.substring(1);
				BaseUtil.showErrorOnSuccess("项目编号相同的BOM：" + relaboid + "已经已提交转BOM");
			}
		}
	}

	/**
	 * Make->Make->turnFQC 制造单批量转FQC前，判断所填批号是否重复(国扬)
	 * 
	 * @author madan 2014-5-5 19:22:50
	 */
	public void make_turnFQC_before(ArrayList<HashMap<Object, Object>> maps) {
		String batchCodesString = CollectionUtil.pluckSqlString(maps, "ma_contractcode");
		String existCodes = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ba_code) from batch where ba_code in(" + batchCodesString + ")", String.class);
		if (existCodes != null) {
			BaseUtil.showError("批号已经存在:" + existCodes);
		}
		existCodes = CollectionUtil.getRepeats(maps, "ma_contractcode");
		if (existCodes != null) {
			BaseUtil.showError("批号填写重复:" + existCodes);
		}
	}

	/**
	 * 判断销售单类型与BOM等级的约束关系
	 * 
	 * @author ZHONGYL
	 * */
	public void Sale_audit_bolevel(Integer sa_id) {
		String SQLStr = "";
		SqlRowList rs, rs0;
		SQLStr = "select sa_code,sa_kind,sd_prodcode,bo_id,pr_refno,bo_level,bl_id  from saledetail left join sale on sa_id=sd_said left join product on pr_code=sd_prodcode left join bom on (bo_mothercode=pr_code) left join bomlevel on bo_level=bl_code  where sa_id='"
				+ sa_id + "' and sa_kind<>' '  ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("bo_id") > 0 && rs.getInt("bl_id") > 0) {
				// 判断是否禁用的
				rs0 = baseDao.queryForRowSet("select count(1) num from Billtypedetail  where bd_blid='" + rs.getInt("bl_id")
						+ "' and NVL(bd_useable,0)=0 and Bd_Type='" + rs.getString("sa_kind") + "'");
				if (rs0.next()) {
					if (rs0.getInt("num") > 0) {
						BaseUtil.showError("BOM等级：" + rs.getString("bo_level") + "不允许下达类型【" + rs.getString("sa_kind") + "】的销售单");
					}
				}
				// 判断是否在设置允许范围内
				rs0 = baseDao.queryForRowSet("select count(1) num,sum((case when Bd_Type='" + rs.getString("sa_kind")
						+ "' then 1 else 0 end)) as alownum from Billtypedetail  where bd_blid='" + rs.getInt("bl_id")
						+ "' and NVL(bd_useable,0)<>0 ");
				if (rs0.next()) {
					if (rs0.getInt("num") > 0 && rs0.getInt("alownum") == 0) {
						BaseUtil.showError("类型【" + rs.getString("sa_kind") + "】的销售单 不在等级：" + rs.getString("bo_level") + "的BOM允许下达范围内");
					}
				}
				// 判断是否超出数量限制
				rs0 = baseDao
						.queryForRowSet("select  count(1) num,wm_concat(sd_prodcode) sd_prodcode from (select sd_prodcode,pr_level,sum(sd_qty)qty from saledetail left join product on (pr_code=sd_prodcode) left join bom on  (bo_mothercode=pr_code) left join bomlevel on bl_code=bo_level where sd_said="
								+ sa_id
								+ " group by sd_prodcode,pr_level)A where pr_level in (select bl_code from bomlevel,Billtypedetail where bl_id=bd_blid and NVL(bd_useable,0)<>0 and bd_type='"
								+ rs.getString("sa_kind") + "' and bd_maxnum>0 and bd_maxnum<A.qty and bl_code<>' ') ");
				if (rs0.next()) {
					if (rs0.getInt("num") > 0) {
						BaseUtil.showError("产品：" + rs.getString("sd_prodcode") + "超出当前的BOM等级允许下达数量");
					}
				}
			}
		}
	}

	/**
	 * 判断请购类型和物料等级的约束关系
	 * 
	 * @author ZHONGYL
	 * */
	public void application_audit_prlevel(Integer ap_id) {
		String SQLStr = "";
		SqlRowList rs, rs0;
		// 判断该类型的请购是否有被禁用的物料等级
		SQLStr = "select count(1) num,wm_concat(ad_detno) detno,max(ap_kind) ap_kind from(select ad_detno,ap_kind from applicationdetail left join  application on ap_id=ad_apid left join product on pr_code=ad_prodcode  where ad_apid="
				+ ap_id
				+ " and ap_kind<>' ' and pr_level||ap_kind in (select pl_levcode||pd_billtype from Purchasetypedetail,Productlevel where pl_id=pd_plid and NVL(pd_useable,0)=0 and pl_levcode<>' ') )tab where rownum<=200";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("序号：" + rs.getString("detno") + "的物料优选等级定义不允许下达类型【" + rs.getString("ap_kind") + "】的请购单");
			}
		}
		// 判断是否存在不在优选等级允许范围内的
		SQLStr = "select count(1) num,wm_concat(ad_detno) detno,max(ap_kind) ap_kind from(select ad_detno,ap_kind from applicationdetail left join  application on ap_id=ad_apid left join product on pr_code=ad_prodcode  where ad_apid="
				+ ap_id
				+ " and ap_kind<>' ' and pr_level in (select pl_levcode from Purchasetypedetail,Productlevel where pl_id=pd_plid and NVL(pd_useable,0)<>0 and pl_levcode<>' ')  and ap_kind not in (select pd_billtype from Purchasetypedetail,Productlevel where pl_id=pd_plid and NVL(pd_useable,0)<>0 and pl_levcode=pr_level ) )tab where rownum<=200";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.getInt("num") > 0) {
			BaseUtil.showError("序号：" + rs.getString("detno") + "的物料不在优选等级定义的允许请购的范围");
		}
		// 判断是否超出数量限制
		rs0 = baseDao
				.queryForRowSet("select  count(1) num,wm_concat(ad_prodcode) ad_prodcode from (select ad_prodcode,pr_level,sum(ad_qty)qty from applicationdetail left join application on ad_apid=ap_id left join product on pr_code=ad_prodcode where ad_apid="
						+ ap_id
						+ " group by ad_prodcode,pr_level)A where A.pr_level in (select pl_levcode from Purchasetypedetail,Productlevel where pl_id=pd_plid and NVL(pd_useable,0)<>0 and pd_maxnum>0 and pd_maxnum<A.qty and pl_levcode<>' ') ");
		if (rs0.next()) {
			if (rs0.getInt("num") > 0) {
				BaseUtil.showError("产品：" + rs.getString("ad_prodcode") + "超出当前的优选等级允许下达数量");
			}
		}

	}

	/**
	 * 从ECR转的ECN不允许保存
	 * 
	 * @author ZHONGYL
	 **/
	public void ecn_save_haveECR(HashMap<Object, Object> store) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(1) num from ecn left join ecr on ecn_ecrcode=ecr_code where ecn_code='" + store.get("ecn_code").toString()
				+ "' and ecr_code<>' ' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.getInt("num") > 0) {
			BaseUtil.showError("ECR生成的ECN不能修改");
		}
	}

	/**
	 * PM->mould->PriceMould_commit 模具报价单维护:明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品，不能操作！
	 */
	public void pricemould_commit_prodcode(Integer pd_id) {
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code",
				"pr_code IN (SELECT pmd_prodcode FROM PRICEMOULDDetail  WHERE " + "pmd_pdid=" + pd_id
						+ ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
			BaseUtil.showError("明细资料有[未审核]、[已禁用]、[已删除]或不存在的物料,不能进行当前操作!");
		}
	}

	/**
	 * 委外收料单删除前，还原委外单的数据 2014-6-12 16:12:24
	 * 
	 * @author madan
	 */
	public void verifyapply_delete(Integer id) {
		List<Object> ids = baseDao.getFieldDatasByCondition("VerifyApplyDetail", "vad_id", "vad_vaid=" + id);
		for (Object i : ids) {
			verifyapply_detatedetail(Integer.parseInt(i.toString()));
		}
	}

	/**
	 * 委外收料单明细删除前，还原委外单数据 2014-6-5 16:55:59
	 * 
	 * @author madan
	 */
	public void verifyapply_detatedetail(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select vad_pucode,nvl(vad_qty,0) vad_qty,nvl(va_intype,'正常委外') va_intype,vad_mcid from VerifyApplyDetail left join VerifyApply on vad_vaid=va_id where vad_id=? and va_class='委外收料单' and nvl(vad_pucode,' ')<>' '",
						id);
		if (rs.next()) {
			double qty = rs.getGeneralDouble("vad_qty");
			Object ordercode = rs.getObject("vad_pucode");
			if (qty > 0) {
				if ("正常委外".equals(rs.getGeneralString("va_intype"))) {
					baseDao.updateByCondition("Make", "ma_haveqty=nvl(ma_haveqty,0)-" + qty, "ma_code='" + ordercode + "'");
					baseDao.updateByCondition("Make", "ma_haveqty=0", "nvl(ma_haveqty,0)<=0 and ma_code='" + ordercode + "'");
				} else if ("工序委外".equals(rs.getGeneralString("va_intype"))) {
					Object mc_id = rs.getObject("vad_mcid");
					if (mc_id != null) {
						baseDao.updateByCondition("MakeCraft", "mc_yqty=nvl(mc_yqty,0)-" + qty, "mc_id=" + mc_id);
						baseDao.updateByCondition("MakeCraft", "mc_yqty=0", "nvl(mc_yqty,0)<=0 and mc_id=" + mc_id);
						makeCraftDao.updateStatus(mc_id);
					}
				}
			}
		}
	}

	/**
	 * 生产报废单提交之前更新累计报废数
	 * 
	 * @author ZHONGYL
	 **/
	public void MakeScrap_commit_LostQty(Integer ms_id) {
		String SQLStr = "";
		SQLStr = "UPDATE MakeScrapDetail set md_allscrapqty=(select sum(md_qty) from MakeScrapDetail A,MakeScrap where ms_id=md_msid and (ms_statuscode='COMMITED' or ms_id='"
				+ ms_id
				+ "') and A.md_mmcode=MakeScrapDetail.md_mmcode and A.md_mmdetno=MakeScrapDetail.md_mmdetno) where md_msid="
				+ ms_id;
		baseDao.execute(SQLStr);
		SQLStr = "UPDATE MakeScrapDetail set md_allscrapqty=NVL(md_allscrapqty,0)+(select NVL(SUM(NVL(mm_returnmqty,0)+NVL(mm_scrapqty,0)),0) qty from makematerial where mm_code=md_mmcode and mm_detno=md_mmdetno) where md_msid="
				+ ms_id;
		baseDao.execute(SQLStr);
	}

	/**
	 * 生产退料单过账或提交、之前更新累计报废数
	 * 
	 * @author ZHONGYL
	 **/
	public void ProdInOut_commit_LostQty(Integer pi_id) {
		String SQLStr = "";
		// 更新本单据的已提交未过账制程不良汇总数
		SQLStr = "UPDATE ProdIODetail set pd_orderqty=(select sum(pd_inqty) from prodiodetail A left join prodinout on A.pd_piid=pi_id  where ((pd_status=0 and pi_invostatuscode='COMMITED') or pi_id='"
				+ pi_id
				+ "')  and A.pd_ordercode=ProdIODetail.pd_ordercode and A.pd_orderdetno=ProdIODetail.Pd_Orderdetno and pd_description='制程不良'  ) where pd_piid="
				+ pi_id;
		baseDao.execute(SQLStr);
		// 本单据的制程不良汇总数+已经过账的制程不良汇总数
		SQLStr = "UPDATE ProdIODetail set pd_orderqty=NVL(pd_orderqty,0)+(select NVL(SUM(NVL(mm_returnmqty,0)+NVL(mm_scrapqty,0)),0) qty from makematerial where mm_code=pd_ordercode and mm_detno=pd_orderdetno) where pd_piid="
				+ pi_id;
		baseDao.execute(SQLStr);
	}

	/**
	 * 模具报价单：单据要求是同一个模具，同一个供应商不能存在多个报价(善领)
	 * 
	 * @author madan 2014-6-11 17:33:41
	 **/
	public void mouldquote_commit_vendpscheck(Integer mq_id) {
		Object vendcode = baseDao.getFieldDataByCondition("MouldQuote", "mq_vendcode", "mq_id=" + mq_id);
		if (vendcode != null && "".equals(vendcode)) {
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat('报价单号:'||mq_code||'序号:'||mqd_detno||'模具编号:'||mqd_pscode) from (select mq_code,mqd_detno,mqd_pscode from mouldquote left join mouldquotedetail on mq_id=mqd_mqid where mq_vendcode=? and mqd_pscode in (select mqd_pscode from mouldquotedetail where mqd_mqid=?) and mqd_mqid<>?)",
							String.class, vendcode, mq_id, mq_id);
			if (err != null) {
				BaseUtil.showError("供应商" + vendcode + "对应的同一个模具不能存在多个报价单!<br>" + err);
			}
		}
	}

	/**
	 * PM->make->commit_before 制造单,委外单:如果工单类型中选择包含“返修”两个字样的，限制必须填写备注！
	 */
	public void make_commit_before_remark(Integer ma_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ma_code) from make where nvl(ma_remark,' ')=' ' and ma_kind like '%返修%' and ma_id=" + ma_id,
				String.class);
		if (dets != null) {
			BaseUtil.showError("工单类型中选择包含“返修”两个字样的，备注必须填写!");
		}
	}

	/**
	 * 工艺资料维护：同一工厂同一产品只能做一个单
	 * 
	 * @author madan 2014-8-8 17:12:22
	 **/
	public void craft_save_before_factory(HashMap<Object, Object> store) {
		Object factory = store.get("cr_factory");
		Object prodcode = store.get("cr_prodcode");
		Object cr_id = store.get("cr_id");
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(cr_code) from Craft where cr_factory=? and cr_prodcode=? and cr_id <>?", String.class, factory,
				prodcode, cr_id);
		if (dets != null) {
			BaseUtil.showError("同一工厂同一产品只能做一个单!单号：" + dets);
		}
	}
	
	/**
	 * 工艺路线，删除明细行时要更新主表的【工序数】、【工价】
	 * @param store
	 */
	public void Craft_DeleteDetail(Integer id) {
		Object cr_id = baseDao.getFieldDataByCondition("craftdetail", "cd_crid", "cd_id="+id);
		baseDao.execute("update craft set cr_stepcount=(select count(1) from CRAFTDETAIL where cr_id =cd_crid and cd_id<>"+id+"),cr_price=(select sum(cd_price) from CRAFTDETAIL where cr_id =cd_crid and cd_id<>"+id+") where cr_id="+cr_id);
	}

	/**
	 * 生产日报维护：保存之前，同一流程单的同一个工序只能录入一次
	 * 
	 * @author madan 2014-8-8 17:18:41
	 **/
	public void dispatch_save_before_device(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		StringBuffer sb = new StringBuffer();
		String err = null;
		for (Map<Object, Object> gridmap : grid) {
			if (gridmap.get("did_id") != null) {
				err = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select di_code from Dispatch left join DispatchDetail on did_diid=di_id where did_devicecode=? and did_stepcode=?",
								String.class, gridmap.get("did_devicecode"), gridmap.get("did_stepcode"));
				sb.append("行:").append(gridmap.get("did_detno"));
				sb.append("单据编号:").append(err).append("<br>");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("同一流程单的同一个工序只能录入一次！<br>" + sb.toString());
	}

	/**
	 * 模具资料:提交前，判断模具明细物料是否在其它模具资料中已存在 2014-12-23 10:25:15
	 * 
	 * @author madan
	 */
	public void productset_prodcheck(Integer id) {
		String err = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat('行号:'||psd_detno||'物料编号:'||psd_prodcode||'模具号:'||psd_code) from (select  psd_detno, psd_prodcode,psd_code from  ProductSetDetail where psd_prodcode in (select psd_prodcode from ProductSetDetail where psd_psid="
								+ id + ") and psd_psid<>" + id + ")", String.class);
		if (err != null) {
			BaseUtil.showError("当前模具明细物料在其它模具资料中已存在，不允许进行当前操作!" + err);
		}
	}

	/**
	 * 模具出货单:有来源的明细行不允许删除 2014-12-24 19:52:34
	 * 
	 * @author madan
	 */
	public void deliveryorder_detatedetail(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(md_sourcecode) from mod_deliveryorder left join mod_deliveryorderdetail on md_id=mdd_mdid where nvl(md_sourcecode,' ')<>' ' and mdd_id="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("有来源的模具出货单不允许删除明细行!来源单号：" + dets);
		}
	}

	/**
	 * 模具销售单:有来源的明细行不允许删除 2014-12-24 20:28:26
	 * 
	 * @author madan
	 */
	public void mouldsale_detatedetail(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(msa_sourcecode) from mod_sale left join mod_saledetail on msa_id=msd_msaid where nvl(msa_sourcecode,' ')<>' ' and msd_id="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("有来源的模具销售单不允许删除明细行!来源单号：" + dets);
		}
	}

	/**
	 * 模具验收报告:删除时，还原模具采购单转单状态 2015-1-6 11:17:54
	 * 
	 * @author madan
	 */
	public void mouldYSReport_detate_updatestatus(Integer id) {
		baseDao.execute("update PURMOULD set pm_turnstatuscode=null, pm_turnstatus=null where pm_code=(select mo_source from MOD_YSREPORT where nvl(mo_source,' ')<>' ' and mo_id="
				+ id + ")");
	}

	/**
	 * 模具验收报告:有来源的明细行不允许删除 2015-1-6 11:15:24
	 * 
	 * @author madan
	 */
	public void mouldYSReport_detatedetail(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(mo_source) from MOD_YSREPORT left join MOD_YSREPORTdetail on yd_moid=mo_id where nvl(mo_source,' ')<>' ' and yd_id="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("有来源的模具验收报告不允许删除明细行!模具采购单号：" + dets);
		}
	}

	/**
	 * 模具委托保管书:删除时，还原模具验收单转单状态 2015-1-6 11:17:54
	 * 
	 * @author madan
	 */
	public void mouldMJProject_detate_updatestatus(Integer id) {
		baseDao.execute("update MOD_YSREPORT set mo_turnstatuscode=null, mo_turnstatus=null where mo_code=(select ws_sourcecode from MOD_MJPROTECT where nvl(ws_sourcecode,' ')<>' ' and ws_id="
				+ id + ")");
	}

	/**
	 * 模具委托保管书:有来源的明细行不允许删除 2015-1-6 11:31:40
	 * 
	 * @author madan
	 */
	public void mouldMJProject_detatedetail(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ws_sourcecode) from MOD_MJPROTECT left join MOD_MJPROTECTdetail on wd_wsid=ws_id where nvl(ws_sourcecode,' ')<>' ' and wd_id="
								+ id, String.class);
		if (dets != null) {
			BaseUtil.showError("有来源的模具委托保管书不允许删除明细行!模具验收单号：" + dets);
		}
	}

	/**
	 * 从ECR评审表转的ECN明细行不允许删除 2015-1-27 10:26:40
	 * 
	 * @author zhongyl
	 */
	public void ECN_detetedetail(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(1) num from ecndetail left join ecn on ed_ecnid=ecn_id left join ecr on ecn_ecrcode=ecr_code where ed_id="
				+ id + "  and ecr_code<>' ' ";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("ECR评审表生成的ECN不能删除明细行");
			}
		}
	}
	
	/**
	 * 提交、审核时判断是否有相应制造ECN 未执行
	 * @param id
	 * @author zhangjh
	 */
	public void MakeChange_CommitAndAudit_Check(Integer id){
		String SQLStr = "select count(1) cn,wm_concat(DISTINCT B.md_makecode) as makecode from makechangedetail A left join MakeMaterialChangeDet B on B.md_makecode=A.md_makecode"
						+" where A.md_mcid="+id+" and nvl(B.md_didstatus,' ')='待执行'";
		SqlRowList rs =baseDao.queryForRowSet(SQLStr);
		if(rs.next() && rs.getInt("cn")>0){
			BaseUtil.showError("制造单："+rs.getString("makecode")+",存在待执行制造单ECN，不允许提交/审核。请先执行或者取消制造单ECN");
		}
	}

	/**
	 * 线别重复时，限制保存、更新并提示；
	 * 
	 * @author XiaoST
	 */
	public void judge_linename_repeat(HashMap<Object, Object> store) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(0) cn from CUSTOMTABLE where CT_CALLER='TeamCode' AND CT_ID<>? AND  CT_VARCHAR50_1=?";
		rs = baseDao.queryForRowSet(SQLStr, store.get("CT_ID"), store.get("CT_VARCHAR50_1"));
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("线别重复");
			}
		}
	}

	/**
	 * 编号重复时，限制保存、更新并提示；
	 * 
	 * @author XiaoST
	 */
	public void judge_code_repeat(HashMap<Object, Object> store) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(0) cn from CUSTOMTABLE where  CT_CALLER='TeamCode' and CT_ID<>? AND CT_CODE=?";
		rs = baseDao.queryForRowSet(SQLStr, store.get("CT_ID"), store.get("CT_CODE"));
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("编号重复");
			}
		}
	}

	/**
	 * 线别被使用时限制删除；
	 * 
	 * @author XiaoST
	 */
	public void code_ifuse_delete(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(0) cn from make left join CUSTOMTABLE on  ma_teamcode=CT_VARCHAR50_1 where CT_ID='" + id
				+ "' and ma_statuscode='AUDITED'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("线别被使用时限制删除");
			}
		}
	}

	/**
	 * 工作中心被使用时限制反审核以及删除；
	 * 
	 * @author XiaoST 2015-1-30
	 */
	public void if_center_use(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(0) cn from make left join workcenter on  wc_code=ma_wccode where wc_id='" + id
				+ "' and ma_statuscode='AUDITED'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("工作中心被使用，不允许删除以及反审核");
			}
		}
	}

	/**
	 * 生产报废单：单据明细行报废原因必须相同 makeScrap->[commit,print]->before
	 * 
	 * @author XiaoST
	 * @param id
	 */
	public void scrap_commit_before_reasoncheck(Integer id) {
		String sql = "select count(0) from (select md_reason, count (ms_id)　from MakeScrapdetail  left join MakeScrap on md_msid=ms_id where md_msid="
				+ id + "group by md_reason) tt";
		int count = baseDao.getCount(sql);
		if (count > 1) {
			BaseUtil.showError("明细行退料原因不一致");
		}
	}

	/**
	 * 生产报废单：明细行存在物料编号不存在或不等于已审核，不允许当前操作 makeScrap->[print]->before
	 * 
	 * @author XiaoST
	 * @param id
	 */
	public void scrap_print_before_materialcheck(Integer id) {
		String str;
		str = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(md_prodcode) code from MakeScrapdetail where md_prodcode not in (select pr_code from product where pr_statuscode='AUDITED') and md_msid="
								+ id, String.class);
		if (str != null) {
			BaseUtil.showError("明细行存在物料编号[" + str + "]不存在或不等于已审核");
		}
	}

	/**
	 * 
	 */
	public void ecn_checkbefore_delete(Integer id) {
		String str;
		str = baseDao.getJdbcTemplate().queryForObject(
				"select mc_ecncode from MakeMaterialChange where mc_id=" + id + " and  mc_ecncode is not null", String.class);
		if (str != null) {
			BaseUtil.showError("通过工单ECN转入的ECN，限制删除");
		}
	}

	/**
	 * 删除BOM明细行，判断是否关联ECN[已提交，已审核的ECN单]有则限制删除 BOM->deleteDetail->before
	 * 
	 * @author XiaoST
	 */
	public void MakeEcn_deleteDetail(Integer id) {
		String str;
		str = baseDao.getJdbcTemplate().queryForObject(
				"select mc_ecncode from MakeMaterialChange,MakeMaterialChangeDet where md_mcid=mc_id and md_id=" + id
						+ " and  mc_ecncode is not null", String.class);
		if (str != null) {
			BaseUtil.showError("通过ECN转入的制造单ECN，限制删除明细行");
		}
	}

	/**
	 * 删除BOM明细行，判断是否关联ECN[已提交，已审核的ECN单]有则限制删除 BOM->deleteDetail->before
	 * 
	 * @author XiaoST
	 */
	public void deleteBomDetail_before(Integer id) {
		String str;
		Object[] objs = baseDao.getFieldsDataByCondition("bom left join bomdetail on bo_id=bd_bomid", new String[] { "bo_id", "bd_soncode",
				"bd_detno" }, "bd_id=" + id);
		if (objs != null) {
			str = baseDao.getJdbcTemplate().queryForObject(
					"select ecn_id from ecn left join ecndetail on ecn_id=ed_ecnid where ed_boid=" + objs[0] + " " + " and  ed_bddetno="
							+ objs[2] + "and ecn_checkstatuscode in('AUDITED','COMMITED')", String.class);
			if (str != null) {
				BaseUtil.showError("删除的明细行关联ECN，不允许删除");
			}
		}
	}
	/**
	 * BOM维护中-替代料维护界面：明细行删除时，更新BOM单中的“替代和替代料编号”；
	 * ProdReplace->deletedetail->before
	 */
	public void bomrep_before_delete(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("BOMDETAIL left join ProdReplace  on bd_id=pre_bdid", new String[] { "bd_id",
				"bd_bomid" }, "pre_id=" + id);
		if (objs != null) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) cn , wmsys.wm_concat(pre_repcode) repcode from BOMDETAIL left join ProdReplace  on bd_id=pre_bdid where bd_id="
							+ objs[0] + " and bd_bomid=" + objs[1] + " and pre_id<>" + id);
			if (rs.next()) {
				if (rs.getInt("cn") > 0) {
					baseDao.execute("update bomdetail set bd_ifrep=-1,bd_repcode='" + rs.getString("repcode") + "' where bd_id=" + objs[0]
							+ " and bd_bomid=" + objs[1]);
				} else {
					baseDao.execute("update bomdetail set bd_ifrep=0,bd_repcode='' where bd_id=" + objs[0] + " and bd_bomid=" + objs[1]);
				}
			} else {
				baseDao.execute("update bomdetail set bd_ifrep=0,bd_repcode='' where bd_id=" + objs[0] + " and bd_bomid=" + objs[1]);
			}
		}
	}

	/*
	 * 限制生产报废单多次打印
	 */
	public void MakeScrap_print_count(Integer id) {
		Object printcount = baseDao.getFieldDataByCondition("makescrap", "ms_count", "ms_id=" + id);
		// 当打印次数大于1,不允许打印
		if (Integer.parseInt(printcount.toString()) > 0) {
			BaseUtil.showError("该单据不允许多次打印!");
		}
	}

	/**
	 * 制造单转检验单FQC之前，限制线别不能为空
	 * */
	public void make_turnFQC_lineCheck(ArrayList<Map<Object, Object>> maps) {
		Object line = null;// 线别
		for (Map<Object, Object> map : maps) {
			line = map.get("ma_remark");
		}
		if (line == null || "".equals(line)) {
			BaseUtil.showError("线别必须填写才能转FQC！");
		}
	}

	/**
	 * 模具报价单：更新之前，选定的供应商必须在五个供应商中选定
	 * 
	 * @author madan 2016-01-29 17:00:07
	 */
	public void pricemould_updateVendor(HashMap<Object, Object> store, String language, Employee employee) {
		Object vendor = store.get("pd_vendcode");
		Object vend1 = store.get("pd_vend1") == null ? "" : store.get("pd_vend1");
		Object vend2 = store.get("pd_vend2") == null ? "" : store.get("pd_vend2");
		Object vend3 = store.get("pd_vend3") == null ? "" : store.get("pd_vend3");
		Object vend4 = store.get("pd_vend4") == null ? "" : store.get("pd_vend4");
		Object vend5 = store.get("pd_vend5") == null ? "" : store.get("pd_vend5");
		if (vendor == null || "".equals(vendor)) {
			BaseUtil.showError("请先选定供应商!");
		} else {
			if (!vendor.equals(vend1) && !vendor.equals(vend2) && !vendor.equals(vend3) && !vendor.equals(vend4) && !vendor.equals(vend5)) {
				BaseUtil.showError("必须在五个供应商中选定!");
			}
		}
	}

	/**
	 * 委外单：如果委外单价不是最低单价，不允许批准
	 */
	public void check_product_price(Integer id) {
		String errorPrice = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('<br>物料:'||ma_prodcode||',当前价:'||ma_price||',最低价:'||ppd_price) from (select ma_prodcode,round(nvl(ma_price,0)*NVL(ma_RATE,0)/(1+nvl(ma_taxrate,0)/100),8) ma_price,(select round(min(ppd_price*NVL(CR_RATE,0)/(1+nvl(ppd_rate,0)/100)),8) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join Currencys on ppd_currency=cr_name where ppd_prodcode=ma_prodcode and ma_vendcode=ppd_vendcode and pp_kind='委外' and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=nvl(ma_qty,0)) ppd_price,CR_RATE,MA_TAXRATE from make left join Currencys on ma_currency=cr_name where ma_id=? and ma_tasktype='OS') where nvl(ppd_price,0)<round((ma_price*NVL(CR_RATE,0)/(1+nvl(MA_TAXRATE,0)/100)),8);",
						String.class, id);
		if (errorPrice != null) {
			BaseUtil.showError("不是最低委外单价,不允许批准!" + errorPrice);
		}
	}

	/**
	 * 委外单：如果委外单价不是核价单中有效的价格，不允许批准
	 */
	public void check_product_vaildprice(Integer id) {
		String errorPrice = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(ma_prodcode) from make where ma_id=? and ma_tasktype='OS' and not exists (select ppd_vendcode,ppd_rate,ppd_currency,ppd_price from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id where ppd_prodcode=ma_prodcode and ma_vendcode=ppd_vendcode and ma_currency=ppd_currency and round(ma_price,8)=round(ppd_price,8) and pp_kind='委外' and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=nvl(ma_qty,0))",
						String.class, id);
		if (errorPrice != null) {
			BaseUtil.showError("不是核价单中有效的价格,不允许批准!" + errorPrice);
		}
	}

	/**
	 * Make ,Make!Base->audit->before 存在未执行的自然切换ecn不能请购
	 * 
	 * @author xiaost
	 */
	public void make_haveEcnCheck(Integer id) {
		SqlRowList sl = baseDao
				.queryForRowSet("select  wm_concat(ma_prodcode) prcode,count(1) num from make left join product on ma_prodcode=pr_code where ma_id="
						+ id
						+ " AND ma_prodcode in (SELECT  ed_repcode from ecndetail,ecn where ecn_id=ed_ecnid and ecn_type='AUTO' and ecn_checkstatuscode='AUDITED' and ecn_didstatuscode='OPEN' and ed_type='SWITCH'  and ed_didstatuscode='OPEN'  ) ");
		if (sl.next()) {
			if (sl.getInt("num") > 0) {
				BaseUtil.showError("存在未执行的自然切换ecn，不能审核:" + sl.getString("prcode") + "");
			}
		}
		sl = baseDao
				.queryForRowSet("select  wm_concat(ma_prodcode) prcode,count(1) num from make left join product on ma_prodcode=pr_code where ma_id="
						+ id
						+ " AND ma_prodcode in (SELECT  ed_soncode from ecndetail,ecn where ecn_id=ed_ecnid and ecn_type='AUTO' and ecn_checkstatuscode='AUDITED' and ecn_didstatuscode='OPEN' and ed_type='DISABLE'  and ed_didstatuscode='OPEN'  ) ");
		if (sl.next()) {
			if (sl.getInt("num") > 0) {
				BaseUtil.showError("存在未执行的自然切换ecn，不能审核:" + sl.getString("prcode") + "");
			}
		}
	}

	/**
	 * 万利达bom等级中的是否量产与ecr评审的BOM阶段匹配
	 * */
	public void ecr_prodstage_commit(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select count(1) c,wm_concat(ecrd_bomid) bomid from ecrdetail left join ecr on ecrd_ecrid=ecr_id left join bom on ecrd_bomid=bo_id left join bomlevel on bl_code=BO_LEVEL where ecrd_ecrid="
						+ id + " and ecrd_bomid>0" + "and (ecr_prodstage='ECN' and bl_mpbom=0 or( ecr_prodstage='DCN' and bl_mpbom<>0 ))");
		if (rs.next()) {
			if (rs.getInt("c") > 0) {
				BaseUtil.showError("变更的BOM:" + rs.getString("bomid") + "中BOM等级的量产属性与评审表填写的BOM阶段不符,不允许提交！");
			}
		}
	}

	/**
	 * 删除工单明细行，删除关联的替代料 Make || Make!Base->deleteDetail->after
	 * 
	 * @author XiaoST
	 */
	public void make_deletedetail_after(Integer id) {
		baseDao.execute("delete from makematerialreplace where mp_mmid=" + id);
	}

	/**
	 * 工单提交、批准: 工单用料表组件必须与BOM配套表一致 Make || Make!Base->-commit || approve >before
	 * 2016-05-24
	 * 
	 * @author XiaoST
	 */
	public void make_haveBomSetCheck(Integer id) {
		String SQLStr = "";
		SqlRowList rs;
		SQLStr = "select count(1) cn ,wm_concat(bsd_prodcode) bsd_prodcode from make,Bomsetdetail,bomset,makekind where ma_id="
				+ id
				+ " and "
				+ " ma_prodcode=bs_mothercode and bsd_bsid=bs_id and ma_kind=mk_name and nvl(bs_statuscode,' ')='AUDITED' and (bsd_mothercode=ma_prodcode or NVL(bsd_mothercode,' ')=' ')"
				+ " and nvl(bsd_usestatus,' ')<>'DISABLE' and mk_type not in ('R','D') and bsd_prodcode not in(select mm_prodcode from makematerial where mm_maid="
				+ id + ")";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("cn") > 0) {
				BaseUtil.showError("物料：" + rs.getString("bsd_prodcode") + "是BOM配套表中的子件，但本工单明细中未录入");
			}
		}
	}

	/**
	 * 删除BOM工序变更单明细，判断是否来源ECN BOMStepChange->deleteDetail->before
	 * 
	 * @author XiaoST
	 */
	public void BomStepChange_deletedetail_before(Integer id) {
		String str;
		str = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(bc_sourcecode) from BomStepChange,BomStepChangeDetail where bd_bcid=bc_id and bd_id=" + id
						+ " and  bc_sourcecode is not null", String.class);
		if (str != null) {
			BaseUtil.showError("通过ECN转入的BOM工序变更单，限制删除明细行");
		}
	}

	/**
	 * 删除制造工序变更单明细，判断是否来源BOM工序变更单 MakeStepChange->deleteDetail->before
	 * 
	 * @author XiaoST
	 */
	public void MakeStepChange_deletedetail_before(Integer id) {
		Object str;
		str = baseDao.getFieldDataByCondition("makeStepChange,makeStepChangedetail", "mc_sourcecode", "md_mcid=mc_id and md_id=" + id
				+ " and mc_sourcecode is not null");
		if (str != null) {
			BaseUtil.showError("通过BOM工序变更单转入的制造工序变更单，限制删除明细行");
		}
	}

	/**
	 * 删除制造单序列号，判断是否已经在生产中， MakeSerial->deleteDetail->before
	 * 
	 * @param id
	 */
	public void makeSerial_deletedetail_before(Integer id) {
		Object str;
		str = baseDao.getFieldDataByCondition("craftMaterial left join makeSerial on ms_sncode=cm_sncode", "cm_sncode", "ms_id=" + id
				+ " and nvl(ms_status,0)<>4");
		if (str != null) {
			BaseUtil.showError("序列号已经在产线生产中，限制删除明细行");
		}
	}

	/**
	 * BOM提交审核前判断工作中心是否计件，计件工艺路线必填，用于安嵘 计件工资系统
	 * 
	 * @author XiaoST 2016-7-14 下午2:54:26 BOM->commit || audit ->before
	 * */
	public void bom_checkcraft_commited(Integer id) {
		// 判断工作中心是否计件
		SqlRowList rs = baseDao.queryForRowSet(
				"select bo_craftversion from bom left join workcenter on bo_wccode=wc_code where bo_id=? and nvl(wc_capatype,0)<>0", id);
		if (rs.next()) {// 计件工作中心
			if (!StringUtil.hasText(rs.getObject("bo_craftversion"))) {// 工艺路线为空
				BaseUtil.showError("BOM属于计件工作中心，工艺路线必须填写");
			} else {
				// 判断工艺路线是否有效
				Object ob = baseDao.getFieldDataByCondition("craft", "cr_id", "cr_code='" + rs.getString("bo_craftversion")
						+ "' and cr_statuscode='AUDITED'");
				if (ob == null) {
					BaseUtil.showError("工艺路线不存在或者未审核");
				}
			}
		}
	}

	/**
	 * BOM提交 审核前判断计件工作中心，工艺路线是专用时，工艺路线中的产品编号必须与BOM中的母件编号一致 用于安嵘 计件工资系统
	 * 
	 * @author XiaoST 2016-7-14 下午2:54:26 BOM->commit || audit ->before
	 * */
	public void bom_checkcraftprodcode_commited(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select case when bo_mothercode=cr_prodcode then 1 else 0 end ifequal,cr_prodcode from bom left join workcenter on bo_wccode=wc_code left join craft on cr_code=bo_craftversion where bo_id=? and nvl(wc_capatype,0)<>0 and nvl(cr_applytype,' ')='专用'",
						id);
		if (rs.next()) {// 计件工作中心,专用工艺路线
			if (rs.getInt("ifequal") == 0) {
				BaseUtil.showError("计件工作中心中专用工艺路线的产品编号[" + rs.getObject("cr_prodcode") + "]，必须与当前BOM的母件编号一致!");
			}
		}
	}

	/**
	 * 开模申请单:提交之前判断明细行模具资料的项目编号与当前单项目编号不一致，不允许进行当前操作
	 * 
	 * @author madan 2016-07-26 11:16:45
	 */
	public void appmould_commit_before_prjcheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ad_detno) from AppMouldDetail left join AppMould on ad_appid=app_id left join ProductSet on ad_pscode=ps_code where app_id=? and nvl(app_prjcode,' ')<>nvl(ps_prjcode,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行模具资料的项目编号与当前单项目编号不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 模具采购单:提交之前判断明细行模具资料的项目编号与当前单项目编号一不致，不允许进行当前操作
	 * 
	 * @author madan 2016-07-26 11:16:45
	 */
	public void purmould_commit_before_prjcheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pmd_detno) from PURMOULDDETAIL left join PURMOULD on pmd_pmid=pm_id left join ProductSet on pmd_pscode=ps_code where pm_id=? and nvl(pm_prjcode,' ')<>nvl(ps_prjcode,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行模具资料的项目编号与当前单项目编号不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	/**
	 * 模具付款申请：明细行删除时，根据来源明细ID更新模具采购单已转金额
	 */
	public void mouldfeeplease_deletedetail(Integer id) {
		mouldFeePleaseDao.restorePucMould(id);
	}

	/**
	 * 模具付款申请:提交之前判断明细行模具资料的项目编号与当前单项目编号一不致，不允许进行当前操作
	 * 
	 * @author madan 2016-07-26 11:16:45
	 */
	public void mouldfeeplease_commit_before_prjcheck(Integer id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(mfd_detno) from MOULDFEEPLEASEDETAIL left join MOULDFEEPLEASE on mfd_mpid=mp_id left join ProductSet on mfd_pscode=ps_code where mp_id=? and nvl(mp_prjcode,' ')<>nvl(ps_prjcode,' ')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行模具资料的项目编号与当前单项目编号不一致，不允许进行当前操作!行号：" + dets);
		}
	}

	// 逻辑限制:产品必须有图纸才可以提交、批准
	public void makebase_submitOrApprove_before_isNeedAttach(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select pr_attach,pr_code from Product left join Make on pr_code=ma_prodcode where ma_id = "
				+ id);
		if (rs.next()) {
			if (rs.getObject("pr_attach") == null) {
				BaseUtil.showError("产品" + rs.getObject("pr_code") + "没有图纸，不允许提交");
			}
		}
	}

	// 模具付款申请：明细行采购分批付款明细行为尾款的，必须填写明细模具验收报告单号
	public void mouleFeePlease_submit_before_checkYSCode(Integer id) {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select mfd_amount,mfd_purccode, mfd_pddetno, pd_amount, pd_isfinal, mfd_yscode, mfd_detno from MOULDFEEPLEASEDETAIL left join PurMouldDet on mfd_pdid=pd_id where mfd_mpid=? and nvl(mfd_purccode,' ')<>' ' and nvl(mfd_pddetno,0)<>0",
						id);
		while (rs.next()) {
			if (rs.getGeneralInt("pd_isfinal") != 0) {
				if (!StringUtil.hasText("mfd_yscode")) {
					sb.append("行[" + rs.getObject("mfd_detno") + "]为尾款申请，请选择验收报告！");
				} else {
					count = baseDao.getCount("select count(1) from MOD_YSREPORT,MOD_YSBGDETAIL where yd_moid=mo_id and mo_code='"
							+ rs.getGeneralString("mfd_yscode") + "' and yd_mjhtcod='" + rs.getGeneralString("mfd_purccode")
							+ "' and nvl(mo_statuscode,' ')='AUDITED'");
					if (count == 0) {
						sb.append("行[" + rs.getObject("mfd_detno") + "]为尾款申请，请选择正确的验收报告！");
					}
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
	}

	// 模具付款申请：明细行采购分批付款明细行为尾款的，必须填写明细保管单号
	public void mouleFeePlease_submit_before_checkWSCode(Integer id) {
		StringBuffer sb = new StringBuffer();
		int count = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select mfd_amount,mfd_purccode, mfd_pddetno, pd_amount, pd_isfinal, mfd_wscode, mfd_detno from MOULDFEEPLEASEDETAIL left join PurMouldDet on mfd_pdid=pd_id where mfd_mpid=? and nvl(mfd_purccode,' ')<>' ' and nvl(mfd_pddetno,0)<>0",
						id);
		while (rs.next()) {
			if (rs.getGeneralInt("pd_isfinal") != 0) {
				if (!StringUtil.hasText("mfd_wscode")) {
					sb.append("行[" + rs.getObject("mfd_detno") + "]为尾款申请，请选择保管单号！");
				} else {
					count = baseDao
							.getCount("select count(1) from MOD_MJPROTECT where ws_code='" + rs.getGeneralString("mfd_wscode") + "'");
					if (count == 0) {
						sb.append("行[" + rs.getObject("mfd_detno") + "]为尾款申请，请选择正确的保管单号！");
					}
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
	}

	// 开模申请单：审核之后自动产生已过账的应收发票
	public void AppMould_audit_after_arbill(Integer id) {
		pppMouldService.createARBill(id);
	}

	/**
	 * 删除制造单序列号，判断是否已经使用， MakeSNList->deleteDetail->before
	 * 
	 * @param id
	 */
	public void makeSNList_deletedetail_before(Integer id) {
		Object str;
		str = baseDao.getFieldDataByCondition("MakeSNList", "msl_sncode", "msl_id=" + id + " and nvl(msl_status,0)<>0");
		if (str != null) {
			BaseUtil.showError("序列号已经使用,限制删除明细行!");
		}
	}

	/**
	 * 模具报价单：删除明细行，还原来源开模申请单转报价状态
	 * 
	 * @author madan 2017-04-10 10:13:13
	 * 
	 * @param id
	 */
	public void priceMould_deletedetail_before_appmould(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select pdd_adid from PriceMouldDet where pdd_id=? and nvl(pdd_adid,0)<>0",
				new Object[] { id });
		if (rs.next()) {
			int ad_id = rs.getGeneralInt("pdd_adid");
			baseDao.execute("update AppMouldDetail set ad_statuscode=null, ad_status=null where ad_id=" + ad_id);
			appMouldDao.checkAdQty(ad_id);
		}
	}

	/**
	 * 模具报价单：删除模具明细，删除其对应的物料明细
	 * 
	 * @author madan 2017-04-10 10:34:29
	 * 
	 * @param id
	 */
	public void priceMould_deletedetail_detPriceMouldDetail(Integer id) {
		baseDao.execute("delete from PriceMouldDetail where pmd_pddid=" + id);
	}

	/**
	 * 工序委外单：如果委外单价不是核价单中有效的价格，不允许进行当前操作
	 */
	public void makecraft_product_vaildprice(Integer id) {
		String errorPrice = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(ma_prodcode) from MakeCraft left join Make on mc_makecode=ma_code where mc_id=? and nvl(mc_servicer,0)=0 and ma_tasktype='MAKE' and not exists (select ppd_vendcode,ppd_rate,ppd_currency,ppd_price from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id where ppd_prodcode=ma_prodcode and mc_vendcode=ppd_vendcode and mc_currency=ppd_currency and round(mc_price,8)=round(ppd_price,8) and pp_kind='委外' and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=nvl(mc_qty,0))",
						String.class, id);
		if (errorPrice != null) {
			BaseUtil.showError("不是核价单中有效的价格，不允许进行当前操作！" + errorPrice);
		}
	}
	
}
