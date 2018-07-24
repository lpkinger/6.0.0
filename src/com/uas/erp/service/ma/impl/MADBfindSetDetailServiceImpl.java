package com.uas.erp.service.ma.impl;

//import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.ma.MADBfindSetDetailService;

@Service
public class MADBfindSetDetailServiceImpl implements MADBfindSetDetailService{
	@Autowired
	private BaseDao baseDao;
	@Override
	public void save(String form, String formdetail) {
		String formSql = SqlUtil.getInsertSqlByFormStore(form, "DBfindSetDetail", new String[]{}, new Object[]{});
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
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(formdetail, "DBfindSetDetail", "fd_id", id);
		baseDao.execute(gridSql);*/
	}
	
}
