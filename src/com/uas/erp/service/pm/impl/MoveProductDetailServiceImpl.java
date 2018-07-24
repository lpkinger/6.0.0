package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.service.pm.MoveProductDetailService;
import com.uas.erp.service.scm.ProdInOutService;

@Service("moveProductDetailService")
public class MoveProductDetailServiceImpl implements MoveProductDetailService {

	final static String GET_SAME_PRODUCT = "select mm_id ammid,bmmid,mm_detno f,mm_prodcode,mm_thisqty aqty,t,bqty,mm_oneuseqty from Makematerial left join(select mm_id bmmid,mm_detno t,mm_prodcode p,mm_thisqty bqty from Makematerial where mm_maid=?) B on mm_prodcode=B.p where mm_maid=? and mm_onlineqty>0 and nvl(t,0)<>0";

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ProdInOutService prodInOutService;

	@Override
	public String saveMoveProductDetail(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MoveProduct", "mp_code='" + store.get("mp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[] { store,grid });
		// 保存Dispatch
		String formSql = SqlUtil.getInsertSqlByMap(store, "MoveProduct");
		baseDao.execute(formSql);
		// //保存DispatchDetail
		
		for(Map<Object, Object> m:grid) {
			m.put("mpd_id", baseDao.getSeqId("DispatchDETAIL_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "MoveProductDetail"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "mp_id",  store.get("mp_id"));;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[] { store,grid });
		String log = checkMaTaskType(store.get("mp_code"));
		return log;
	}

	@Override
	public void deleteMoveProductDetail(int mp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MoveProduct", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mp_id });
		// 删除Dispatch
		baseDao.deleteById("MoveProduct", "mp_id", mp_id);
		// 删除DispatchDetail
		baseDao.deleteById("MoveProductdetail", "mpd_mpid", mp_id);
		// 记录操作
		baseDao.logger.delete(caller, "mp_id", mp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mp_id });
	}

	@Override
	public String updateMoveProductDetailById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MoveProduct", "mp_statuscode", "mp_id=" + store.get("mp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store,gstore});
		// 修改Dispatch
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MoveProduct", "mp_id");
		baseDao.execute(formSql);
		// 修改DispatchDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MoveProductDetail", "mpd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("mpd_id") == null || s.get("mpd_id").equals("") || s.get("mpd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MOVEPRODUCTDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MoveProductDetail", new String[] { "mpd_id" },
						new Object[] { id }); 
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "mp_id", store.get("mp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store,gstore});
		String log = checkMaTaskType(store.get("mp_code"));
		return log;
	}

	@Override
	public void auditMoveProductDetail(int mp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MoveProduct", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { mp_id });

		// 生产退料单和补料单 并过帐
		String err = CreateInAndOut(mp_id,caller);
		if (err != null && !err.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(err));
		}
		// 执行审核操作
		baseDao.audit("MoveProduct", "mp_id="+ mp_id, "mp_status", "mp_statuscode", "mp_aduitdate", "mp_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "mp_id", mp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { mp_id });
	}

	@Override
	public void resAuditMoveProductDetail(int mp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MoveProduct", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 反过帐 生产退料单和补料单 并删除
		String err = UnCreateInAndOut(mp_id,caller);
		if (err != null && !err.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(err));
		}
		// 执行反审核操作
		baseDao.resOperate("MoveProduct", "mp_id="+ mp_id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "mp_id", mp_id);
	}

	@Override
	public void submitMoveProductDetail(int mp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MoveProduct", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.submitOnlyEntering(status);
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("MoveProductDetail", "mpd_prodcode", "mpd_mpid=" + mp_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c
						+ "')\">" + c + "</a>&nbsp;");
			}
		}
		//判断制造单从+制单A序号+物料编号与工单用料表、
		//判断制造单到+制单B序号+物料编号与工单用料表以及这两者之间是不是一致，否则限制提交
	   SqlRowList rs =  baseDao.queryForRowSet("select count(1) cn ,  WMSYS.wm_concat(mpd_detno)  detno from MoveProductDetail  where mpd_mpid="+mp_id+" and (mpd_qty>mpd_aqty or mpd_qty>mpd_bqty)");
	    if(rs.next()){
			if(rs.getInt("cn")>0){
				BaseUtil.showError("挪料数量不能大于A工单的可挪料数或B工单的需求数!行号：["+rs.getString("detno")+"]");
			}
		}		
	    rs =  baseDao.queryForRowSet("select count(1) cn ,  WMSYS.wm_concat(mpd_detno)  detno from MoveProduct left join  MoveProductDetail on mpd_mpid=mp_id left join make on ma_code=mp_frommakecode left join makematerial on ma_id=mm_maid and mm_detno=mpd_fromdetno where mp_id="+mp_id+" and mpd_prodcode<>NVL(mm_prodcode,' ')");
        if(rs.next()){
           if(rs.getInt("cn")>0){
                BaseUtil.showError("制造单A的用料序号和物料编号不对应!行号：["+rs.getString("detno")+"]");
           }
        }  
        rs =  baseDao.queryForRowSet("select count(1) cn ,  WMSYS.wm_concat(mpd_detno)  detno from MoveProduct left join  MoveProductDetail on mpd_mpid=mp_id left join make on ma_code=mp_tomakecode left join makematerial on ma_id=mm_maid and mm_detno=mpd_todetno where mp_id="+mp_id+" and mpd_prodcode<>NVL(mm_prodcode,' ')");
        if(rs.next()){
           if(rs.getInt("cn")>0){
                BaseUtil.showError("制造单B的用料序号和物料编号不对应!行号：["+rs.getString("detno")+"]");
           }
        }
        Object mp_code = baseDao.getFieldDataByCondition("MoveProduct", "mp_code", "mp_id="+mp_id);
        String log = checkMaTaskType(mp_code);
		if(!"".equals(log)){
			BaseUtil.showError(log);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { mp_id });
		// 执行提交操作
		baseDao.submit("MoveProduct", "mp_id="+ mp_id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mp_id", mp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { mp_id });
		
	}

	@Override
	public void resSubmitMoveProductDetail(int mp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MoveProduct", "mp_statuscode", "mp_id=" + mp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { mp_id });
		// 执行反提交操作
		baseDao.resOperate("MoveProduct", "mp_id="+ mp_id, "mp_status", "mp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mp_id", mp_id);
		handlerService.afterResSubmit(caller, new Object[] { mp_id });
	}

	/**
	 * 挪料单载入
	 */
	@Override
	public int moveProduct(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object id = store.get("mp_id");
		if (id == null || "".equals(id) || Integer.parseInt(id.toString()) == 0) {
			id = baseDao.getSeqId("MOVEPRODUCT_SEQ");
			String code = baseDao.sGetMaxNumber("MoveProduct", 2);
			store.put("mp_id", id);
			store.put("mp_code", code);
			baseDao.execute(SqlUtil.getInsertSqlByMap(store, "MoveProduct"));
		} else {
			baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "MoveProduct", "mp_id"));
		}
		// A工单允许被挪料数量
		Object f = baseDao.getFieldDataByCondition("Make", "ma_id", "ma_code='" + store.get("mp_frommakecode") + "'");
		makeDao.setMMOnlineQTY(f.toString(), null);
		// 计算已转退料数
		makeDao.setBackQty(f.toString(),0);
		baseDao.updateByCondition("makematerial", "mm_thisqty=mm_onlineqty-nvl(mm_backqty,0)", " mm_maid in (" + f.toString() + ")");
		baseDao.updateByCondition("MakeMaterialreplace", "mp_thisqty=(select mm_thisqty from MakeMaterial where mm_id=mp_mmid)",
				"mp_maid in (" +f.toString() + ")");
		// B工单允许挪的物料和数量
		Object t = baseDao.getFieldDataByCondition("Make", "ma_id", "ma_code='" + store.get("mp_tomakecode") + "'");
		makeDao.setThisQty(null, Integer.parseInt(t.toString()), null);
		SqlRowList rs = baseDao.queryForRowSet(GET_SAME_PRODUCT, t, f);
		List<Map<Object, Object>> grid = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> map = null;
		int detno = 1;
		double a = 0;
		double b = 0;
		double qty =0,setqty=0;
		if (store.get("mp_nyts")!=null){
			setqty=Double.parseDouble(store.get("mp_nyts").toString());
		} 
		setqty=setqty<=0?0:setqty;
		while (rs.next()) {
			map = new HashMap<Object, Object>();
			map.put("mpd_mpid", id);
			map.put("mpd_id", baseDao.getSeqId("MOVEPRODUCTDETAIL_SEQ"));
			map.put("mpd_detno", detno++);
			map.put("mpd_prodcode", rs.getString("mm_prodcode"));
			map.put("mpd_fromdetno", rs.getInt("f"));
			map.put("mpd_todetno", rs.getInt("t"));
			a = rs.getDouble("aqty");
			b = rs.getDouble("bqty");
			map.put("mpd_aqty", a);
			map.put("mpd_bqty", b);
			qty=b > a ? a : b;
			qty=setqty>0?setqty* rs.getDouble("mm_oneuseqty"):qty;
			qty=qty>a?a:qty;
			qty=qty>b?b:qty;
			map.put("mpd_qty", qty); 
			map.put("mpd_ammid", rs.getObject("ammid"));
			map.put("mpd_bmmid", rs.getObject("bmmid"));
			grid.add(map);
		}
		if (grid.size() > 0) {
			baseDao.deleteByCondition("MoveProductDetail", "mpd_mpid=" + id);
			baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "MoveProductDetail"));
		}
		return Integer.parseInt(String.valueOf(id));
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public String UnCreateInAndOut(int mpid, String caller) {
		String inpiclass = "", outpiclass = "";
		Object tasktype = null;
		String inwhoami = "", outwhoami = "";  
		// 根据挪料的工单定义出入库单类型
		tasktype = baseDao.getFieldDataByCondition("MoveProduct left join make on MP_FROMMAKECODE=ma_code",
				"ma_tasktype", "mp_id=" + mpid);
		if (tasktype != null && !tasktype.equals("")) {
			if (tasktype.equals("OS")) {
				inwhoami = "ProdInOut!OutsideReturn";
				outwhoami = "ProdInOut!OutsidePicking";
				inpiclass = "委外退料单";
				outpiclass = "委外领料单";
			} else {
				inwhoami = "ProdInOut!Make!Return";
				outwhoami = "ProdInOut!Picking";
				inpiclass = "生产退料单";
				outpiclass = "生产领料单";
			} 
		} else {
			return BaseUtil.getLocalMessage("工单不存在或类型错误");
		}
		Object mpcode = baseDao.getFieldDataByCondition("MoveProduct", "mp_code", "mp_id='" + mpid + "'");
		Object[] indata = baseDao.getFieldsDataByCondition("ProdInOut", "pi_id,pi_statuscode", "pi_sourcecode='"
				+ mpcode + "' and pi_class='" + inpiclass + "'  ");
		Object[] outdata = baseDao.getFieldsDataByCondition("ProdInOut", "pi_id,pi_statuscode", "pi_sourcecode='"
				+ mpcode + "' and pi_class='" + outpiclass + "' ");
		if (outdata != null) {
			if (!outdata[1].equals("POSTED")) {
				// 反过帐 删除 领料单
				prodInOutService.resPostProdInOut((String) outwhoami, Integer.parseInt(outdata[0].toString()));
			}
		}
		if (indata != null) {
			if (!indata[1].equals("POSTED")) {
				// 反过帐 删除 退料单
				prodInOutService
						.resPostProdInOut(inwhoami, Integer.parseInt(indata[0].toString()));
			}
		}
		if (outdata != null) {
			prodInOutService.deleteProdInOut((String) outwhoami, Integer.parseInt(indata[0].toString()));
		}
		if (indata != null) {
			prodInOutService.deleteProdInOut((String) inwhoami, Integer.parseInt(outdata[0].toString()));
		}
		return "";
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public String CreateInAndOut(int mpid, String caller) {
		String incode = null, outcode = null;
		Object inid = null;
		Object outid = null;
		String inpiclass = "", outpiclass = "";
		String inwhoami = "", outwhoami = "";  
		int detno = 1;
		JSONObject j = null;
		Object tasktype = null;
		// 根据挪料的工单定义出入库单类型
		tasktype = baseDao.getFieldDataByCondition("MoveProduct left join make on MP_FROMMAKECODE=ma_code",
				"ma_tasktype", "mp_id=" + mpid);
		if (tasktype != null && !tasktype.equals("")) {
			if (tasktype.equals("OS")) {
				inwhoami = "ProdInOut!OutsideReturn";
				outwhoami = "ProdInOut!OutsidePicking";
				inpiclass = "委外退料单";
				outpiclass = "委外领料单";
			} else {
				inwhoami = "ProdInOut!Make!Return";
				outwhoami = "ProdInOut!Picking";
				inpiclass = "生产退料单";
				outpiclass = "生产领料单";
			} 
		} else {
			return "工单不存在或类型错误";
		}
		// 如果已经存在此来源的领退单据，则不能审核
		Object mpcode = baseDao.getFieldDataByCondition("MoveProduct", "mp_code", "mp_id='" + mpid + "'");
		Object[] indata = baseDao.getFieldsDataByCondition("ProdInOut", "pi_id,pi_statuscode", "pi_sourcecode='"
				+ mpcode + "' and pi_class='" + inpiclass + "'  ");
		Object[] outdata = baseDao.getFieldsDataByCondition("ProdInOut", "pi_id,pi_statuscode", "pi_sourcecode='"
				+ mpcode + "' and pi_class='" + outpiclass + "' ");
		if (indata != null || outdata != null) {
			if (outdata != null) {
				if (outdata[1].equals("UNPOST")) {
					// 删除 领料单
					prodInOutService.deleteProdInOut((String) outwhoami, Integer.parseInt(outdata[0].toString()));

				} else {
					return "已经存在此挪料单的退料单，不能审核挪料单";
				}
			}
			if (indata != null) {
				if (indata[1].equals("UNPOST")) {
					// 删除 退料单
					prodInOutService.deleteProdInOut(inwhoami, Integer.parseInt(indata[0].toString()));
				} else {
					return "已经存在此挪料单的领料单，不能审核挪料单";
				}

			}

		}
		// 根据挪料的工单定义出入库单类型
		Object movewhcode = null;// 挪料仓库
		movewhcode = baseDao.getFieldDataByCondition("MoveProduct","mp_whcode", "mp_id=" + mpid);
		Object whcode ="";
		if(movewhcode != null && !("").equals(movewhcode)){
			whcode = baseDao.getFieldDataByCondition("warehouse", "wh_code", "wh_code='"+movewhcode.toString()+"' and wh_ifmove<>0");
		}
		if (whcode == null || ("").equals(whcode)) { 
			return BaseUtil.getLocalMessage("挪料仓库填写错误，或仓库属性未定义成挪料仓");
		} 
		// 产生 退料
		j = makeDao.newProdIO(movewhcode.toString(), inpiclass, inwhoami,"");
		if (j == null) {
			return BaseUtil.getLocalMessage(inpiclass + "产生失败");
		} else {
			incode = j.getString("pi_inoutno");
			inid = j.get("pi_id");
		}
		SqlRowList rs = baseDao
				.queryForRowSet("select mpd_ammid,mpd_fromdetno,mpd_qty from MoveProductDetail where mpd_qty>0 and mpd_mpid="
						+ mpid + " order by mpd_detno");
		while (rs.next()) {
			makeDao.turnInWh(incode, detno++, rs.getInt("mpd_ammid"), rs.getInt("mpd_fromdetno"), rs.getDouble("mpd_qty"),
					inpiclass);
		}
		// 产生 领料
		j = makeDao.newProdIO(movewhcode.toString(), outpiclass,outwhoami,null);
		if (j == null) {
			return BaseUtil.getLocalMessage(outpiclass + "产生失败");
		} else {
			outcode = j.getString("pi_inoutno");
			outid = j.get("pi_id");
		}
		detno = 1;
		rs = baseDao
				.queryForRowSet("select mpd_bmmid,mpd_todetno,mpd_qty from MoveProductDetail where mpd_qty>0 and mpd_mpid="
						+ mpid + " order by mpd_detno");
		while (rs.next()) {
			makeDao.turnOutWh(outcode, detno++, outpiclass, rs.getInt("mpd_bmmid"), rs.getInt("mpd_todetno"), rs.getDouble("mpd_qty"));
		}
		Object[] mp_departmentcode = baseDao.getFieldsDataByCondition("MoveProduct","mp_departmentcode,mp_departmentname", "mp_id=" + mpid);
		// 更新此出入库单的来源单号  mp_departmentcode   mp_departmentname  pi_departmentcode 
		baseDao.updateByCondition("ProdInOut", "pi_invostatus='已审核',pi_invostatuscode='AUDITED',pi_departmentcode='"+mp_departmentcode[0]+"',pi_departmentname='"+mp_departmentcode[1]+"',pi_sourcecode='" + mpcode + "'", "pi_id=" + inid + " or pi_id=" + outid);
		baseDao.updateByCondition("ProdIODetail", "pd_description='良品退仓'", "pd_piid=" + inid);
		baseDao.updateByCondition("ProdIODetail", "pd_batchcode='" + mpcode + "'", "pd_piid=" + inid + " or pd_piid="
				+ outid);
		Object ma_tasktype = baseDao.getFieldDataByCondition("Make left join MoveProduct on mp_frommakecode=ma_code", "ma_tasktype", "mp_id="+mpid);
		if("OS".equals(ma_tasktype.toString())){
			//赋值委外领料单的委外商号和应付供应商
			baseDao.execute("update prodinout set (pi_cardcode,pi_title,pi_receivecode,pi_receivename)=(select ma_vendcode,ma_vendname,ma_apvendcode,ma_apvendname from make left join moveproduct on mp_tomakecode=ma_code where mp_id=?) where pi_id=?",mpid,outid);
			//赋值委外退料单的委外商号和应付供应商
			baseDao.execute("update prodinout set (pi_cardcode,pi_title,pi_receivecode,pi_receivename)=(select ma_vendcode,ma_vendname,ma_apvendcode,ma_apvendname from make left join moveproduct on mp_frommakecode=ma_code where mp_id=?) where pi_id=?",mpid,inid);
		}
		try{
			// 过帐 退料单
			prodInOutService.postProdInOut(Integer.parseInt(inid.toString()),inwhoami);
		}catch(Exception e){
			baseDao.logger.others("审核时退料单过账出错", e.getMessage(), caller, "mp_id", mpid);
			return "";
		}
		try{
			// 过帐 领料单
			prodInOutService.postProdInOut(Integer.parseInt(outid.toString()), outwhoami);
		}catch(Exception e){
			baseDao.logger.others("审核时领料单过账出错", e.getMessage(), caller, "mp_id", mpid);
			return "";
		}
		return "";
	}
	private String checkMaTaskType(Object mp_code){
		String log = "";
		SqlRowList rs = null;
		rs = baseDao.queryForRowSet("select * from moveproduct left join make on mp_frommakecode = ma_code where mp_code = ? and (select ma_tasktype from make where ma_code = mp_frommakecode) <> (select ma_tasktype from make where ma_code = mp_tomakecode)",mp_code);
		if(rs.next()){
			String mp_frommakecode = rs.getString("mp_frommakecode");
			String mp_tomakecode = rs.getString("mp_tomakecode");
			String ma_tasktype = rs.getString("ma_tasktype");
			log = "单号:"+mp_frommakecode+"为"+("MAKE".equals(ma_tasktype)?"制造单":"委外单")+"<br/>单号:"+mp_tomakecode+"为"+(!"MAKE".equals(ma_tasktype)?"制造单":"委外单"+"<br/>制造类型不一致不能挪料");
		}else{
			rs = baseDao.queryForRowSet("select * from moveproduct left join make on mp_frommakecode = ma_code where ma_tasktype ='OS' and mp_code = ? and (select ma_vendcode from make where ma_code = mp_frommakecode) <> (select ma_vendcode from make where ma_code = mp_tomakecode)",mp_code);
			if(rs.next()){
				String mp_frommakecode = rs.getString("mp_frommakecode");
				String mp_tomakecode = rs.getString("mp_tomakecode");
				log = "委外单"+mp_frommakecode+"和"+mp_tomakecode+"的委外商号不同";
			}
		}
		return log;
	}
}
