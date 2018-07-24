package com.uas.erp.dao.common;

import java.io.IOException;
import java.io.Reader;

public interface SystemDao {

	void executeScript(final Reader reader) throws IOException;

	String getVersion();

	void setVersion(String version);

}
