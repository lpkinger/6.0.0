package com.uas.erp.dao.common.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SystemDao;

@Repository
public class SystemDaoImpl extends BaseDao implements SystemDao {

	@Transactional
	public void executeScript(final Reader reader) throws IOException {
		final BufferedReader bufferedReader = new BufferedReader(reader);
		for (String sql = bufferedReader.readLine(); sql != null; sql = bufferedReader.readLine()) {
			String trimmedSql = sql.trim();
			// 去掉注释
			final boolean ignore = (trimmedSql.length() == 0 || trimmedSql.startsWith("--") || trimmedSql.startsWith("//") || trimmedSql
					.startsWith("/*"));
			if (!ignore) {
				if (trimmedSql.endsWith(";")) {
					trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
				}
				execute(trimmedSql);
			}
		}
		bufferedReader.close();
		reader.close();
	}

	@Override
	public String getVersion() {
		return queryForObject("select max(version) from sysinfo", String.class);
	}

	@Override
	public void setVersion(String version) {
		getJdbcTemplate().update("insert into sysinfo(version,deploy_time) values (?,sysdate)", version);
	}
}
