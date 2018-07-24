package com.uas.erp.core.listener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.SystemDao;
import com.uas.erp.model.Master;

/**
 * 监听ServletContext初始化完成事件
 * 
 * @author yingp
 * 
 */
@Component
public class OnServletContextAwareInitialize implements ServletContextAware {

	@Override
	public void setServletContext(ServletContext context) {
		checkVersion();
		upgradeBySQL();
	}

	private void checkVersion() {
		Resource resource = ContextUtil.getApplicationContext().getResource("classpath:VERSION");
		if (resource.exists()) {
			try {
				String version = StringUtil.trimBlankChars(FileUtils.readFileToString(resource.getFile(), "UTF-8"));
				SystemDao systemDao = ContextUtil.getBean(SystemDao.class);
				String lastVersion = systemDao.getVersion();
				if (!version.equals(lastVersion) && version.matches("\\d+")) {
					EnterpriseDao enterpriseDao = (EnterpriseDao) ContextUtil.getBean("enterpriseDao");
					String defaultSource = BaseUtil.getXmlSetting("defaultSob");
					List<Master> masters = enterpriseDao.getMasters();
					for (Master master : masters) {
						SpObserver.putSp(master.getMa_name());
						try {
							systemDao.setVersion(version);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					SpObserver.putSp(defaultSource);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 按sql语句先进行数据结构升级
	 */
	private void upgradeBySQL() {
		// 打包的时候根据上次版本与本次版本差异而生成的升级sql文件
		Resource resource = ContextUtil.getApplicationContext().getResource("classpath:upgrade.sql");
		if (resource.exists()) {
			try {
				File file = resource.getFile();
				BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
				String result = "success";
				try {
					String fileContent = FileUtils.readFileToString(file, "UTF-8");
					if (fileContent != null) {
						String[] sqls = fileContent.split("(;\\s*\\r\\n)|(;\\s*\\n)");
						StringBuffer buffer = new StringBuffer("BEGIN");
						// 无需执行的master id
						String excludeTag = "--exclude=";
						Long excludeMaster = null;
						for (String sql : sqls) {
							if (StringUtil.hasText(sql)) {
								if (excludeMaster == null && sql.startsWith(excludeTag)) {
									excludeMaster = Long.parseLong(sql.substring(10));
									continue;
								}
								// 存在表、字段在当前账套已存在的情况，忽略错误
								buffer.append("\nBEGIN\nexecute immediate '").append(sql.replace("'", "''"))
										.append("';\nexception when others then \nnull;\nEND;");
							}
						}
						buffer.append("\nEND;");
						EnterpriseDao enterpriseDao = (EnterpriseDao) ContextUtil.getBean("enterpriseDao");
						String defaultSource = BaseUtil.getXmlSetting("defaultSob");
						List<Master> masters = enterpriseDao.getMasters();
						for (Master master : masters) {
							if (excludeMaster == null || master.getMa_manageid() == null
									|| Long.compare(excludeMaster, master.getMa_manageid()) != 0) {
								SpObserver.putSp(master.getMa_name());
								baseDao.getJdbcTemplate().execute(buffer.toString());
							}
						}
						SpObserver.putSp(defaultSource);
					}

				} catch (Exception e) {
					result = "failed";
					e.printStackTrace();
				} finally {
					// 执行完即销毁
					file.renameTo(new File(file.getAbsolutePath() + "." + result));
				}
			} catch (IOException e) {

			}
		}
	}

}
