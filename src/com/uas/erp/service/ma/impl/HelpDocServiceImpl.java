package com.uas.erp.service.ma.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.WordToHtml;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.service.ma.HelpDocService;
@Service
public class HelpDocServiceImpl implements HelpDocService {
	final private String UAS_HOST="218.17.158.219";
	final private String UAS_USER="root";
	final private int UAS_PORT=2022;
	final private String UAS_PWD="select456***";
	final private String CREATE_PUB_LINK=" CREATE PUBLIC DATABASE LINK \"UAS_STANDARD\"  CONNECT TO \"UAS\"  IDENTIFIED BY \"select!#%*(\" USING '(DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = 218.17.158.219)(PORT = 1521)))(CONNECT_DATA =(SID = orcl)))'";
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormAttachDao  formAttachDao;
	@Override
	public void saveDoc(String data){
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(data);
		Object caller=map.get("CALLER_");String sql="";
		if(caller!=null){
			Object path_=map.get("PATH_");
			if(path_==null) BaseUtil.showError("未选择任何有效的文件!");
			JSONObject obj = formAttachDao.getFilePath( Integer.parseInt(String.valueOf(path_).split(";")[0]));
			String filepath = String.valueOf(obj.get("fp_path"));
			map.put("FILEPATH_",filepath);
			if(baseDao.checkIf("HelpDoc", "CALLER_='"+caller+"'")){
				sql=SqlUtil.getUpdateSqlByFormStore(map, "HelpDoc", "CALLER_");
			}else sql=SqlUtil.getInsertSqlByMap(map, "HelpDoc");
			baseDao.execute(sql);
			map.put("man_",SystemSession.getUser().getEm_name());
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "HelpDoclog"));
		}
	}
	@Override
	public JSONObject getHelpInfo(String caller) {
		// TODO Auto-generated method stub
		SqlRowList sl=baseDao.queryForRowSet("select * from HelpDoc where caller_=?", new Object[]{caller});
		if(sl.next()) return sl.getJSONObject();
		else return null;
	}
	@Override
	public String getHelpDoc(String caller) {
		// TODO Auto-generated method stub
		String path_=baseDao.getFieldValue("HelpDoc","path_","caller_='"+caller+"'",String.class);
		if(path_!=null){
			JSONObject obj = formAttachDao.getFilePath( Integer.parseInt(path_.split(";")[0]));
			String path = obj.getString("fp_path");
			String fileName = obj.getString("fp_name");
			return WordToHtml.getWord(path, caller, fileName);
		}
		return "jsps/ma/helpdocremind.html";
	}
	@Override
	public List<JSONObject> getUpdateLogs(String caller) {
		// TODO Auto-generated method stub
		List<JSONObject> lists=new ArrayList<JSONObject>();
		SqlRowList sl=baseDao.queryForRowSet("select * from HelpDocLog where caller_=?", new Object[]{caller});
		while(sl.next()){
			lists.add(sl.getJSONObject());
		}
		return lists;
	}
	@Override
	public void download() {
		// TODO Auto-generated method stub
		Session session=null;
		Channel channel = null;
		compare();
		JSch jsch = new JSch();
		try {
			session=jsch.getSession(UAS_USER,UAS_HOST,UAS_PORT);
		} catch (JSchException e1) {
			e1.printStackTrace();
		}
		if (session == null) {
			BaseUtil.showError("网络故障,无法连接到优软UAS!");
		}
		session.setPassword(UAS_PWD);
		//设置第一次登陆的时候提示，可选值：(ask | yes | no)
		session.setConfig("StrictHostKeyChecking", "no");
		try {
			session.connect(300000);
		} catch (JSchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			//创建sftp通信通道
			channel = (Channel) session.openChannel("sftp");
			channel.connect(1000);
			ChannelSftp sftp = (ChannelSftp) channel;
			SqlRowList sl=baseDao.queryForRowSet("select * from Helpdoc_Compare");
			BufferedInputStream bis=null;
			BufferedOutputStream bos=null;
			OutputStream outstream=null;
			InputStream instream=null;
			String downpath=null;
			while(sl.next()){
				downpath=sl.getGeneralString("C_PATH");
				/*downpath="/usr/local/apache-tomcat-7.0.32/webapps/CR.war";
				//
*/				
				System.out.println(downpath);
				instream=sftp.get(downpath);
				byte b[] = new byte[1024];
				bis=new BufferedInputStream(instream,2048);
				//FileUtil.getFileName(downpath)
				//path.substring(path.lastIndexOf("/")+1)
				outstream=new FileOutputStream(new File(PathUtil.getFilePath()+File.separator+downpath.substring(downpath.lastIndexOf("/")+1)));
				bos=new BufferedOutputStream(outstream);			
				int n;
				while ((n = bis.read(b)) != -1) {
					bos.write(b, 0, n);
				}
			}
			
			bis.close();
			bos.flush();
			bos.close();
			outstream.flush();
			outstream.close();
			instream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
			channel.disconnect();
		}
	}
	private void compare(){
		String sob = SpObserver.getSp();
		String defaultSob=BaseUtil.getXmlSetting("defaultSob");
		if(!defaultSob.equals(sob)) BaseUtil.showError("请先切换回默认帐套再进行更新!");
		try{
			baseDao.execute("select  * from dual@UAS_STANDARD");
			baseDao.execute("delete from Helpdoc_Compare");
			baseDao.execute("INSERT INTO Helpdoc_Compare(C_CALLER,C_VERSION,C_PATH,C_KEYWORDS,C_DESC) SELECT CALLER_,VERSION_,FILEPATH_,KEYWORDS_,DESC_ FROM HELPDOC@UAS_STANDARD  A WHERE NOT EXISTS (SELECT 1 FROM HELPDOC B WHERE A.VERSION_=B.VERSION_ AND A.CALLER_=B.CALLER_) ");
		}catch(Exception e){
			e.printStackTrace();
			try{
				baseDao.execute(CREATE_PUB_LINK);
				baseDao.execute("delete from Helpdoc_Compare");
				baseDao.execute("INSERT INTO Helpdoc_Compare(C_CALLER,C_VERSION,C_PATH,C_KEYWORDS,C_DESC) SELECT CALLER_,VERSION_,FILEPATH_,KEYWORDS_,DESC_ FROM HELPDOC@UAS_STANDARD  A WHERE NOT EXISTS (SELECT 1 FROM HELPDOC B WHERE A.VERSION_=B.VERSION_ AND A.CALLER_=B.CALLER_) ");
			}catch(Exception e1){
				e1.printStackTrace();
				BaseUtil.showError("网络故障，无法进行比对");
			}
		}		
	}
}
