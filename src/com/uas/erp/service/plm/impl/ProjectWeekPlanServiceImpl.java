package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;






import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.ProjectWeekPlanService;


@Service
public class ProjectWeekPlanServiceImpl implements ProjectWeekPlanService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateProject(String formStore){
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(formStore);
		baseDao.execute("update Prjwkreportdetail set wrd_auditdetail=? where wrd_id=?",new Object[]{map.get("wrd_auditdetail"),map.get("wrd_id")});
		map.remove("wrd_auditdetail");
		map.remove("wrd_prjcode");
		map.remove("wrd_prjname");
		map.remove("wrd_id");
		String sql = SqlUtil.getUpdateSqlByFormStore(map, "project", "prj_id");
		baseDao.execute(sql);
		//SqlUtil.getUpdateSqlByFormStore(formStore, table, keyField)
		
	}
	
	@Override
	public Map<String,Object> getProjectList(){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		List<Object[]> datas = new ArrayList<Object[]>();
		List<Object[]> prjDatas = new ArrayList<Object[]>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		datas = baseDao.getFieldsDatasByCondition("plmproducttype", new String[]{"pt_id","pt_code","pt_name","pt_description","pt_subof"}, "1=1 order by pt_id desc");
		prjDatas = baseDao.getFieldsDatasByCondition("project left join plmproducttype on pt_code=prj_producttypecode", new String[]{"pt_id","pt_code","pt_name","pt_description","pt_subof","prj_name","prj_code","prj_id"}, "pt_id is not null and prj_auditstatuscode='AUDITED' order by pt_id,prj_id desc");
		
		for(Object[] obj:datas){
			map = new HashMap<String, Object>();
			map.put("pt_id",obj[0]);
			map.put("pt_code",obj[1]);
			map.put("name",obj[2]);
			map.put("pt_description", obj[3]);
			map.put("pt_subof", obj[4]);
			map.put("type","product"); //类型，标识是产品类型还是项目名称

			if("0".equals(obj[4].toString())){
				List<Map<String,Object>> child = getChild(datas,obj,prjDatas);
				map.put("children",child);
				list.add(map);
			}			
		}	
		modelMap.put("datas", list);
		return modelMap;
	}
	
	private List<Map<String,Object>> getChild(List<Object[]> datas,Object[] data,List<Object[]> prjDatas){
		List<Map<String,Object>> child = new ArrayList<Map<String,Object>>();
		Map<String,Object> childJson = null;
		Map<String,Object> prjMap = null;
		for(Object[] tree:datas){
			if(tree[4]==null){
				tree[4] = "";
			}
			if(data[0].toString().equals(tree[4].toString())){
				childJson = new HashMap<String,Object>();
				childJson.put("pt_id",tree[0]);
				childJson.put("pt_code",tree[1]);
				childJson.put("name",tree[2]);
				childJson.put("pt_description", tree[3]);
				childJson.put("pt_subof", tree[4]);
				childJson.put("type","product");
				List<Map<String,Object>> childnext = getChild(datas,tree,prjDatas);
				if(childnext.size()>0){
					childJson.put("children",childnext);
				}else{
					childJson.put("leaf",true);
				}
				child.add(childJson);
			}
		}
		
		for(Object[] prjData:prjDatas){
			childJson = new HashMap<String,Object>();		
			if(data[1]!=null&&prjData[1]!=null){
				if(prjData[1].equals(data[1])){
					prjMap = new HashMap<String,Object>();
					prjMap.put("name", "<span style='font-style:italic'>" + prjData[5] + "</span>"); //显示名称
					prjMap.put("prj_name", prjData[5]); //项目名称
					prjMap.put("pt_name", data[2]); //产品类型名称
					prjMap.put("type","project");
					prjMap.put("leaf", true);
					child.add(prjMap);
					//prjDatas.remove(prjData);
				}
			}				
		}
		return child;
	}
}
