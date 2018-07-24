package com.uas.mobile.dao;
import java.util.List;

import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Form;
public interface PanelDao {
  Form getMobileForm(String caller);
  List<DetailGrid> getPanelDetailsByCaller(String caller);
}
