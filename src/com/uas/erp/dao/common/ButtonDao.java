package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.GridButton;

public interface ButtonDao {
	List<GridButton> getGridButtons(String sob, String caller);
}
