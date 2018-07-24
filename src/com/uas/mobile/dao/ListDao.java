package com.uas.mobile.dao;

import java.util.List;

import com.uas.erp.model.DataList;
import com.uas.mobile.model.ListQuerySet;

public interface ListDao {
 DataList getListView(String caller,String master);
 String getRelativesettings(String caller,String kind,int emid);
 List<ListQuerySet> getListViewQuerySet(String caller);
}
