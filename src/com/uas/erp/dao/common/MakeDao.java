package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author yingp
 * 
 */
public interface MakeDao {

	Map<String, String> statistics(String condition);

	/**
	 * @param inoutno
	 *            領料單號
	 * @param id
	 *            領料單ID
	 * @param detno
	 *            領料明細序號
	 * @param mmid
	 *            工單明細ID
	 * @param qty
	 *            領料數
	 * @param whcode
	 *            倉庫號
	 * @param piclass
	 *            單據類型
	 * @param employee
	 * @param language
	 * @param caller
	 *            領料單caller
	 */
	void turnOut(String inoutno, int id, int detno, int mmid, int mmdetno, double qty, String whcode, String piclass, String caller);

	/**
	 * 退料单 明细
	 * 
	 * @param mmid
	 * @param qty
	 * @param piclass
	 * @param employee
	 * @param language
	 * @param caller
	 * @return
	 */
	void turnIn(String inoutno, int id, int detno, int mmid, double qty, String whcode, String piclass, String caller);

	void turnAdd(String inoutno, int id, int detno, int mmid, int mmdetno, double qty, String whcode, String piclass, String caller);

	JSONObject turnQuaCheck(int maid, double qty, Object xlcode, Object batchcode, String statuscode, boolean fqcSeq);// 制造单转检验单

	JSONObject turnQuaCheck2(int maid, double qty, Object xlcode, Object batchcode, String statuscode, boolean fqcSeq);// 制造单转检验单(免检)

	JSONObject newProdIO(String whcode, String piclass, String caller,String piintype);

	JSONObject newProdIOWithVendor(String whcode, String ve_code,  String ve_apvendcode, String piclass, String caller,String piintype);

	JSONObject newProdIOWithVendor(String ve_code,  String ve_apvendcode, String piclass, String caller,String piintype);

	void turnOutWh(String no, int detno, String piclass, int mmid, int mmdetno, double qty);

	void turnInWh(String no, int detno, int mmid, int mmdetno, double qty, String pi_class);

	void turnMadeWh(String no, int maid, double qty);

	void turnMadeOSWh(String no, int maid, double qty);

	void turnMadeWhbyflow(String no, int mfid, double qty);

	JSONObject turnMake(int id, double tqty);// 委外转制造

	JSONObject turnScrap(String caller, String cls, List<Map<Object, Object>> store);

	void deleteMake(int id);

	void restoreSaleWithQty(int maid, double uqty);

	String turnAccept(String caller, List<Map<Object, Object>> maps);

	/**
	 * 更新本次可领料数
	 * 
	 * @param mm_id
	 *            单个
	 * @param ma_id
	 *            单个
	 * @param maidlist
	 *            多个工单 maid用逗号分隔
	 **/
	void setThisQty(Integer mm_id, Integer ma_id, String maidlist);

	/**
	 * 保存工单时自动默认可从物料资料获取的未填写属性
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 * */
	void saveDefault(String maidstr, String mmidstr);

	/**
	 * 更新用料表的备损数
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 * */
	void setBalance(String maidstr, String mmidstr);

	/**
	 * 把所有替代料更新在用料表的mm_repprodcode字段
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 * */
	void setMaterialRepcode(String maidstr, String mmidstr);// 更新用料可替代物料

	/**
	 * 更新 工单最大领料套数 只考虑正常用料的领料数
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 **/
	void setMaxCanMadeqty(String maidstr);

	/**
	 * 更新工单用料在线结存数量
	 * 
	 * @param WhereStr
	 *            筛选条件 不带where
	 */
	void setMaxCanMadeqtyByCondition(String WhereStr);

	/**
	 * 更新工单用料在线结存数量
	 * 
	 * @param maidstr
	 *            工单ID用逗号隔开
	 * @param mmidstr
	 *            用料表ID用逗号隔开
	 */
	void setMMOnlineQTY(String maidstr, String mmidstr);

	/**
	 * 汇总工单的已转退料数
	 * 
	 * @param maidstr
	 * @param tasktype
	 *            (MAKE、OS)
	 * @param piclass
	 *            (生产退料单、委外退料单)
	 */
	void setBackQty(String maidstr, Integer mmid);

	void setLSThisqty(String caller, String maid, Integer setqty, String wipwhcode);

	/**
	 * 计算本次可补料数
	 * 
	 * @param ma_id
	 */
	void setAddQty(String ma_id);

	/*
	 * 更新用料表已转数
	 * 
	 * @param ma_id
	 */
	void refreshTurnQty(Integer ma_id, Integer mm_id);

	void changeMakeGetStatus(Integer ma_id);

	void updateMakeGetStatus(String ma_id);
	
	//更新工单的完工状态
	void updateMakeFinishStatus(Integer ma_id);
}
