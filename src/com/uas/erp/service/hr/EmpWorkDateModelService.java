package com.uas.erp.service.hr;


/**
 * Created by IntelliJ IDEA.
 * User: USOFTPC30
 * Date: 13-6-17
 * Time: 上午9:25
 * To change this template use File | Settings | File Templates.
 */
public interface EmpWorkDateModelService {

    public void saveEmpWorkDateModel(String formStore, String gridStore, String  caller);

    public void updateEmpWorkDateModelById(String formStore, String gridStore, String  caller);

    public void deleteEmpWorkDateModel(int id, String  caller);
    
    public void setEmpWorkDateModel(int wdid, String condition, String caller);
    
    public void cancelEmpWorkDateModel( String condition, String caller);
    
    public String updateEmpWorkDateList(String[] emids,String startdate,String enddate,String caller);

    public String loadGridDate(String emid ,String startdate,String enddate,String caller);

}
