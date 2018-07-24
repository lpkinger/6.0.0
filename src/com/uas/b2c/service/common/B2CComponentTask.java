package com.uas.b2c.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.scm.ProductBatchUUIdService;

/**
 * erp端自动根据原厂型号，匹配uuid标准料号
 * @author XiaoST 
 * @date 2016年9月12日 下午2:38:45
 */
@Component("b2ccomponenttask")
@EnableAsync
@EnableScheduling
public class B2CComponentTask {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private B2CComponentService b2cComponentService;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;

	private static List<Master> masters = null;

	public void execute() {
		if (masters == null) {
			masters = enterpriseService.getMasters();
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (master.b2bEnable()) {
				SpObserver.putSp(master.getMa_name());
				dowloadComponent(master);//根据原厂型号获取UUID
			}
		}
		SpObserver.putSp(sob);
	}
	
	/**
	 * 获取需要匹配UUID的物料原厂型号
	 * @return
	 */
	public List<String> getNeedDownloadCodes(int page, int pageSize){
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		try {
			List<String> codeSets = baseDao
					.getJdbcTemplate()
					.queryForList("select pr_orispeccode from (select TT.*, ROWNUM rn from (select pr_orispeccode from product where nvl(pr_orispeccode,' ')<>' ' "+
						       "and nvl(pr_uuid,' ')=' ' group by pr_orispeccode)TT where ROWNUM <=?) where rn >=?",String.class,end,start);
			return codeSets;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 匹配UUID
	 * @param master
	 * @return
	 */
	public void dowloadComponent(Master master){
		int cn = baseDao.getCount("select count(1) from (select pr_orispeccode from product where nvl(pr_orispeccode,' ')<>' ' and nvl(pr_uuid,' ')=' ' group by pr_orispeccode)");
		if(cn > 0){
			int num = (int) Math.ceil(cn/400);
			for(int i=1;i<=num;i++){
				List<String> oriCodes = getNeedDownloadCodes(i,400);
				if(!CollectionUtil.isEmpty(oriCodes)) {
					try {
						List<ComponentInfoUas> componentInfoUas = b2cComponentService.findByCode(oriCodes, master);
						if (!CollectionUtil.isEmpty(componentInfoUas)) {
							List<String> sqls = new ArrayList<String>();
							// 按照分组code 进行分组
							Map<Object, List<ComponentInfoUas>> set = new HashMap<Object, List<ComponentInfoUas>>();
							List<ComponentInfoUas> list = null;
							for (ComponentInfoUas componentInfo : componentInfoUas) {
								String key = componentInfo.getCode();
								if (StringUtil.hasText(key) && set.containsKey(key)) {
									list = set.get(key);
								} else {
									list = new ArrayList<ComponentInfoUas>();
								}
								list.add(componentInfo);
								set.put(key, list);
							}
							String unit ="", erpunit="";
							for (Map.Entry<Object, List<ComponentInfoUas>> entry : set.entrySet()) {
								if (entry.getValue().size() > 1) {// 一个原厂型号多个uuid
								} else {
									SqlRowList rs = baseDao.queryForRowSet("select pr_id,pr_unit,pr_code from product where pr_orispeccode=? and pr_uuid is null ",entry.getKey());
									while (rs.next()){
										sqls.add("update product set pr_uuid='"+entry.getValue().get(0).getUuid()+"' where pr_id="+rs.getLong("pr_id") +" and pr_uuid is null");
										unit = entry.getValue().get(0).getUnit();
										erpunit = productBatchUUIdService.getUASUnit(unit, rs.getString("pr_unit"));
										//自动产生平台物料信息表B2C$GOODSONHAND,写入商城标准单位，和ERP 单位
										sqls.add("insert into b2c$goodsonhand(go_uuid,go_id,go_unit,go_erpunit,go_prodcode,go_code) select '"+entry.getValue().get(0).getUuid()+"',b2c$goodsonhand_seq.nextval,'"+unit+"','"+erpunit+"','"+rs.getString("pr_code")+"','"+entry.getValue().get(0).getCode()+"' from dual where not exists(select 1 from b2c$goodsonhand where go_prodcode='"+rs.getString("pr_code")+"') ");
									}
								}
							}
							baseDao.execute(sqls);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
