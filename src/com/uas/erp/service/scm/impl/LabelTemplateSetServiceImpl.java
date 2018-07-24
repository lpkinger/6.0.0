package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.LabelTemplateSetService;

@Service("LabelTTemplateSetServiceImpl")
public class LabelTemplateSetServiceImpl implements LabelTemplateSetService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveLabelT(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("la_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Label", "la_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		for(int i = 0 ; i < grid.size(); i++){
			Object valuetype = grid.get(i).get("lp_valuetype");
			Object sql = grid.get(i).get("lp_sql");
			Object detno = grid.get(i).get("lp_detno");
			if(!"字符串".equals(valuetype)&&!"常量".equals(valuetype)&&sql!=null&&!"".equals(sql)){
				checkSql(sql.toString(),detno);
			}
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store, grid});	
		// 保存Label
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Label", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存labelParameter		
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "labelParameter", "lp_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "la_id", store.get("la_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store, grid});			

		//测试远程生成文件，文件夹
		// 要写入的文件内容  
        /*String fileContent = "hello world，你好世界";  
        // ftp登录用户名  
        String userName = "XiaoST";  
        // ftp登录密码  
        String userPassword = "xxxx";  
        // ftp地址  
        //String server = "127.0.0.1";//直接ip地址  
        String server = "192.168.253.136";
        // 创建的文件  
        String fileName = "ftp.txt";  
        // 指定写入的目录  
        String path = "D:/资料/测试";  
   
        FTPClient ftpClient = new FTPClient();  
        try {  
            InputStream is = null;  
            // 1.输入流  
            is = new ByteArrayInputStream(fileContent.getBytes());  
            // 2.连接服务器  
            ftpClient.connect(server);   
            //3.登录ftp  
            ftpClient.login(userName,userPassword);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {  
            	System.out.println("未连接到FTP，用户名或密码错误。");  
                    ftpClient.disconnect();  
            } else {  
               System.out.println("FTP连接成功。");  
            }                   
            // 4.指定写入的目录  
            ftpClient.changeWorkingDirectory(path);  
            // 5.写操作  
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            ftpClient.storeFile(new String(fileName.getBytes("utf-8"),  
                    "iso-8859-1"), is);  
            is.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (ftpClient.isConnected()) {  
                try {  
                    ftpClient.disconnect();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        //取值SQL,拼接成String,分割
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        SqlRowList rs = baseDao.queryForRowSet("select bar_code,bar_remain,bar_status from barcode where bar_id in (1129733,1129144,1129145)");
        if(rs.next()){
        	list = rs.getResultList();
        }
        Iterator<Map<String, Object>> iter = list.iterator();
        List<String> listS = new ArrayList<String>();
        while (iter.hasNext()) {
		   listS.add(StringUtils.join(iter.next().values().iterator(), ","));
        }
        System.out.println(StringUtils.join(listS.iterator(),","));*/
	}

	private void checkSql(String sql,Object detno) {
		String value = sql;
		String regex = "[\\{].*[\\}]";
		String va = value.replaceAll(regex, "\\'1\\'");
		va += " and 1=2";
		try {
			baseDao.execute(va);
		} catch (Exception e) {
			BaseUtil.showError("取值SQL语句不合法!行号:"+detno);
		}
	}
	
	@Override
	public void deleteLabelT(String caller, int id) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Label", "la_statuscode", "la_id=" + id);
		StateAssert.delOnlyEntering(status);
		//删除Label
		baseDao.deleteById("Label", "la_id", id);
		//删除LabelParameter
		baseDao.deleteById("LabelParameter", "lp_laid",id);
		//记录操作
		baseDao.logger.delete(caller, "la_id", id);
	}

	@Override
	public void updateLabelT(String formStore, String param, String caller) {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
			List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
			//只能修改[在录入]的单据资料!
			Object status = baseDao.getFieldDataByCondition("Label", "la_statuscode", "la_id=" + store.get("la_id"));
			StateAssert.updateOnlyEntering(status);
			String code = store.get("la_code").toString();
			// 当前编号的记录已经存在,不能修改!
			boolean bool = baseDao.checkByCondition("Label", "la_code='" + code + "' and la_id<>"+store.get("la_id"));
			if(!bool){
				BaseUtil.showError("模板编号重复!");
			}
			for(int i = 0 ; i < grid.size(); i++){
				Object valuetype = grid.get(i).get("lp_valuetype");
				Object sql = grid.get(i).get("lp_sql");
				Object detno = grid.get(i).get("lp_detno");
				if(!"字符串".equals(valuetype)&&!"常量".equals(valuetype)&&sql!=null&&!"".equals(sql)){
					checkSql(sql.toString(),detno);
				}
			}
			//修改Label
			store.put("la_inman", SystemSession.getUser().getEm_name());
			store.put("la_indate", DateUtil.currentDateString(null));
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Label", "la_id");
			baseDao.execute(formSql);
			List<String> gridSql = SqlUtil.getInsertOrUpdateSql(grid, "labelParameter", "lp_id");
			baseDao.execute(gridSql);
			//记录操作
			baseDao.logger.update(caller, "la_id", store.get("la_id"));
	}

	@Override
	public void auditLabelT(String caller, int id) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Label", "la_statuscode", "la_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		//判断参数名是否重复
		SqlRowList rs = baseDao.queryForRowSet("select lp_name,count(1) from labelparameter where lp_laid=? group by lp_name having count(1)>1",id);
		if(rs.next()){
			BaseUtil.showError("参数名不能重复,重复参数名:"+rs.getObject("lp_name"));
		}
		//判断行号是否大于零
		SqlRowList rs2 = baseDao.queryForRowSet("select * from labelparameter where lp_laid=? and lp_detno<=0",id);
		if(rs2.next()){
			BaseUtil.showError("行号必须大于0!");
		}
		//执行审核操作
		baseDao.audit("Label", "la_id=" +id, "la_status", "la_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "la_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);	
	
	}

	@Override
	public void resAuditLabelT(String caller, int id) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Label", "la_statuscode", "la_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Label", "la_id=" + id, "la_status", "la_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "la_id", id);		
	}

	@Override
	public void bannedSerial(String caller, int id) {
		//执行禁用操作
		baseDao.banned("Label", "la_id=" + id, "la_status", "la_statuscode");
		//记录操作
		baseDao.logger.banned(caller, "la_id",id);		
	}

	@Override
	public void resBannedLabelT(String caller, int id) {
		//执行反禁用操作
		baseDao.resOperate("Label", "la_id=" + id, "la_status", "la_statuscode");
		//记录操作
		baseDao.logger.resBanned(caller, "la_id", id);		
	}

	@Override
	public void submitLabelT(String caller, int id) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Label", "la_statuscode", "la_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		//判断参数名是否重复
		SqlRowList rs = baseDao.queryForRowSet("select lp_name,count(1) from labelparameter where lp_laid=? group by lp_name having count(1)>1",id);
		if(rs.next()){
			BaseUtil.showError("参数名不能重复,重复参数名:"+rs.getObject("lp_name"));
		}
		//判断行号是否大于零
		SqlRowList rs2 = baseDao.queryForRowSet("select * from labelparameter where lp_laid=? and lp_detno<=0",id);
		if(rs2.next()){
			BaseUtil.showError("行号必须大于0!");
		}
		//执行提交操作
		baseDao.submit("Label", "la_id=" + id, "la_status", "la_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "la_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);		
	}

	@Override
	public void resSubmitLabelT(String caller, int id) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Label", "la_statuscode", "la_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, id);
		//执行反提交操作
		baseDao.resOperate("Label", "la_id=" + id, "la_status", "la_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "la_id", id);
		handlerService.afterResSubmit(caller, id);		
	
	}

	@Override
	public void saveLabelP(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 保存labelParameter
		Object ob = baseDao.getFieldDataByCondition("label left join labelParameter on la_id=lp_laid", "max(lp_detno)", "lp_laid ='"+store.get("lp_laid")+"' and 1=1");
		if(("").equals(ob)|| ob == null){
			store.put("lp_detno", "1");
		}else{
			store.put("lp_detno", Integer.valueOf(ob.toString())+1);
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "labelParameter", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "lp_id", store.get("lp_id"));		
	}
	
	@Override
	public void updateLabelP(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//修改label中的时间和录入人
		/*store.put("bs_recorder", SystemSession.getUser().getEm_name());
		store.put("bs_date", DateUtil.currentDateString(null));*/
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "LabelParameter", "lp_id");
		baseDao.execute(formSql);				
	}

	@Override
	public void deleteLabelP(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition("Label left join labelParameter on la_id=lp_laid", "la_statuscode", "lp_id=" + id);
		StateAssert.delOnlyEntering(status);
		//删除LabelParameter
		baseDao.deleteById("LabelParameter", "lp_id",id);
		//记录操作
		baseDao.logger.delete(caller, "lp_id", id);
	}

	@Override
	public List<Map<String, Object>> getdetail(String caller, String condition) {
		SqlRowList rs =baseDao.queryForRowSet("select lp_laid,lp_id,lp_encode,lp_valuetype,lp_name,lp_leftrate,lp_toprate,lp_ifshownote,lp_font,lp_size,lp_notealignjustify,lp_width,la_pagesize,lp_height from labelParameter left join label on la_id=lp_laid where "+condition);
	    if(rs.next()){
	    	return rs.getResultList();
	    }
		return null;
	}

	@Override
	public void deleteLabelPrintSetting(String caller, String lps_caller) {		
		baseDao.deleteByCondition("labelprintsetting", "lps_caller='"+lps_caller+"'");
	}

	@Override
	public void saveLPrintSetting(String caller, String param) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 执行保存前的其它逻辑
		for (Map<Object,Object> map: grid){
			String value = map.get("lps_sql").toString();
			String regex = "\\{(?:[A-Za-z][A-Za-z0-9_]*)\\}";			 
			String va = value.replaceAll(regex, "\\'1\\'");		
			va += " and 1=2"  ;
			//如果是平台打印，不需要执行语句进行校验
			if(!map.get("lps_caller").toString().equals("B2B!BarPrint") && !map.get("lps_caller").toString().equals("B2B!OutBoxPrint")){
				try {
					baseDao.execute(va);
				} catch (Exception e) {
					// TODO: handle exception
					BaseUtil.showError("取值SQL语句不合法！");
				}	
			}
		}		
		List<String> sqls = SqlUtil.getInsertOrUpdateSql(grid, "labelprintsetting", "lps_id");
		baseDao.execute(sqls);
	}

}
