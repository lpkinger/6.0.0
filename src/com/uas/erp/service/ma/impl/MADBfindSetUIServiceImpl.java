package com.uas.erp.service.ma.impl;

//import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.service.ma.MADBfindSetUIService;

@Service
public class MADBfindSetUIServiceImpl implements MADBfindSetUIService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Override
	public void save(String form, String formdetail) {
		String formSql = SqlUtil.getInsertSqlByFormStore(form, "Form", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		/*Object[] id = new Object[1];
		if(formdetail.contains("},")){//明细行有多行数据哦
			String[] datas = formdetail.split("},");
			id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				id[i] = baseDao.getSeqId("FORMDETAIL_SEQ");
			}
		} else {
			id[0] = baseDao.getSeqId("FORMDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(formdetail, "FormDetail", "fd_id", id);
		baseDao.execute(gridSql);*/
	}
	@Override
	public boolean checkByCaller(String caller) {
		boolean bool = true;
		if(!baseDao.checkByCondition("form", "fo_caller='" + caller + "'")){//先看caller是否存在于form表
			Form form = formDao.getForm(caller, SpObserver.getSp());
			for(FormDetail detail:form.getFormDetails()){
				bool = baseDao.checkByCondition("dbfindSetUI", "ds_whichui='" + detail.getFd_field() + "'");
				if(!bool)break;
			}
		}
		return bool;
	}
	
}
