package com.uas.erp.service.scm.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.service.scm.GenerateBarcodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service("generateBarcodeService")
public class GenerateBarcodeServiceImpl implements GenerateBarcodeService { //生成条形码

    @Autowired
    private BaseDao baseDao;
    @Autowired
    private VerifyApplyDao verifyApplyDao;

    @Override
    public String getBarFormStore(String caller, String Condition) {
        Map<String, Object> map1 = new HashMap<String, Object>();
        String sql = "select pi_status as bi_status FROM ProdInOut LEFT JOIN ProdIODetail ON pd_piid=PI_id  left join product on pr_code=pd_prodcode left join (select bi_pdid,NVL(sum(NVL(bi_inqty,0)),0) bi_inqty from barcodeio  left join ProdIoDetail on bi_pdid=pd_id LEFT JOIN ProdInOut ON pd_piid=Pi_id where " +
            Condition + " group by bi_pdid)A on A.bi_pdid=pd_id where " +
            Condition;
        SqlRowList rs = baseDao.queryForRowSet(sql); //数据库里要不要as都可以，为了严谨还是写着比较好

        if (rs.next()) {
            Map<String, Object> map = rs.getCurrentMap();
            Iterator<String> it = map.keySet().iterator(); //关于map类型的迭代器	  

            while (it.hasNext()) {
                String key;
                key = (String) it.next();
                map1.put(key.toLowerCase(), map.get(key));
            }
        }

        return (map1 == null) ? null : BaseUtil.parseMap2Str(map1);
    }

    /**
     * 保存barcodeSet
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveBarcode(String caller, String gridStore) {
        // TODO Auto-generated method stub
        List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
        List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,"BARCODEIO", "bi_id");
        baseDao.execute(gridSql);

        if (caller.equals("ProdInOut!BarcodeIn")) {
            SqlRowList rs = baseDao.queryForRowSet(
                    "select count(0) cn from ProdInOut left join ProdIODetail on pi_id=pd_piid left join barcodeio on pd_piid=bi_piid and pd_pdno=bi_pdno where bi_pdno='" +
                    gstore.get(0).get("bi_pdno").toString() +
                    "' and bi_piid='" +
                    gstore.get(0).get("bi_piid").toString() +
                    "' having sum(bi_inqty)>pd_inqty  group by pd_inqty");

            if (rs.next()) {
                if (rs.getInt("cn") > 0) {
                    BaseUtil.showError("入库数总和不允许超过来料总量！");
                }
            }

            SqlRowList rs1 = baseDao.queryForRowSet(
                    "select count(0) cm from packagedetail left join barcodeio on pd_barcode=bi_barcode ");

            if (rs1.next()) {
                if (rs.getInt("cm") > 0) {
                    baseDao.execute(
                        "update packagedetail set pd_innerqty=(select bi_inqty from barcodeio) where pd_barcode=bi_barcode");
                    baseDao.execute(
                        "update package set pa_totalqty=sum(pd_innerqty) where pd_barcode=bi_barcode");
                }
            }
        } else {
            SqlRowList rs = baseDao.queryForRowSet(
                    "select count(0) cn from ProdInOut left join ProdIODetail on pi_id=pd_piid left join barcodeio on pd_piid=bi_piid and pd_pdno=bi_pdno where bi_pdno='" +
                    gstore.get(0).get("bi_pdno").toString() +
                    "' and bi_piid='" +
                    gstore.get(0).get("bi_piid").toString() +
                    "' having sum(bi_outqty)>pd_outqty  group by pd_outqty");

            if (rs.next()) {
                if (rs.getInt("cn") > 0) {
                    BaseUtil.showError("出库数总和不允许超过来料总量！");
                }
            }
        }

        //更新出入库单中明细行中的条码数量
        baseDao.execute(
            "update ProdIODetail set (pd_barcodeinqty,pd_barcodeoutqty)=(select NVL(sum(nvl(bi_inqty,0)),0),NVL(sum(nvl(bi_outqty,0)),0) from barcodeio where bi_pdid=pd_id )" +
            " where  pd_id=" + gstore.get(0).get("bi_pdid"));
    }

    final static  String INSERTBARCODEIO = "insert into barcodeio (bi_id,bi_batchcode,bi_batchid,bi_piid,bi_pdid,bi_pdno,bi_inoutno,bi_piclass,bi_barcode,bi_outboxcode," +
		                    "bi_prodcode,bi_whcode,bi_inqty,bi_madedate,bi_vendbarcode,bi_location,bi_status,bi_printstatus)"+
		                    "values(barcodeio_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0,0)";
    /** 
    * 批量生成条码
    */
	@Override
    @Transactional
    public void batchGenBarcode(String caller,int id,String data) {
    	 List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
    	 double zxbzs=0;
    	 int pd_id=0,pa_id=0;
    	 double box_zxbzs=0,rest=0,temp=0;
    	 String outBox=null;
    	 //获取单据时候已过账
    	 boolean bopost = baseDao.checkIf("prodiodetail", "pd_piid="+id+" and pd_status=99");
    	 Object ve_id = baseDao.getFieldDataByCondition("Vendor left join ProdInOut on pi_cardcode=ve_code", "ve_id", "pi_id="+id);
    	 for (Map<Object, Object> s : gstore) {
    		 pd_id = Integer.valueOf(s.get("pd_id").toString());     //从表id
    		 String ba_code;
    		 int ba_id = 0;
    		 SqlRowList rs0 = baseDao.queryForRowSet(
		                "select pi_id,NVL(pd_whcode,pi_whcode) pd_whcode,(pd_inqty-nvl(pd_barcodeinqty,0)) restqty,pd_ordercode,pr_id,pi_inoutno,pd_id,pr_tracekind,pd_pdno,pi_inoutNo,pd_prodcode,pi_cardcode,pd_location,pi_class,pd_batchcode,ba_id,nvl(pd_prodmadedate,pi_date) pd_date,pd_inqty,pd_outqty from ProdInOut left join ProdIODetail on pd_piid=pi_id left join batch on ba_id=pd_batchid  left join product on pr_code=pd_prodcode where pi_id=" +
		                id + " and pd_id=" +pd_id +" and nvl(pi_pdastatus,' ') <> '已入库'");
	         if (rs0.next()) {
	        	 ba_id = rs0.getInt("ba_id");
	        	 ba_code = rs0.getString("pd_batchcode");
	        	//如果已经过账并且对应的批次号已经有出库则不允许再生成条码
	    	    if(bopost){
	    	    	if(baseDao.checkIf("batch","ba_id="+ ba_id+" and ba_outqty>0")){
	    	    		BaseUtil.showError("批号已经出库不允许再生成条码！，批号："+ba_code);
	    	    	}
	    	    }
	        	if(rs0.getString("pd_whcode")==null || ("").equals(rs0.getString("pd_whcode"))){
	        		BaseUtil.showError("仓库编号不能为空");  
	        	}
	        	if(s.get("pr_zxbzs") != null && !("").equals(s.get("pr_zxbzs"))){
		    		 temp = Double.valueOf(s.get("pr_zxbzs").toString());
		    		 if(((temp%1)<1 &&(temp%1)>0 )  || temp<=0 ){
		    			 BaseUtil.showError("分装数量错误！");
		    		 }
		    		 zxbzs = temp;  //最小包装数
	    		 }else{
	    			 BaseUtil.showError("分装数量错误!");
	    		 }
		    	 if(s.get("rest") != null && !("").equals(s.get("rest"))){
		    		 temp = Double.valueOf(s.get("rest").toString());
		    		 if(temp<=0 ){
		    			 BaseUtil.showError("本次数量错误！");
		    		 }
		    		 rest =temp;  //本次数量
	    		 }else{
	    			 BaseUtil.showError("本次数量错误！");
	    		 }  	
		    	 
	    		 if((s.get("pr_boxqty") != null || !("").equals(s.get("pr_boxqty"))) && s.get("pr_boxqty")!="0" ){
	     			  box_zxbzs=Double.valueOf((s.get("pr_boxqty").toString()));  //外箱容量
	     			 if(((box_zxbzs%1)<1 &&(box_zxbzs%1)>0 )|| box_zxbzs<0 ){
		    			 BaseUtil.showError("外箱容量错误！");
		    		 }
	 	    	}else{
	 	    		 BaseUtil.showError("外箱容量错误！");
	 	    	}
	    		 
	        	if(rs0.getDouble("restqty")< rest){
	    			BaseUtil.showError("本次数量不能大于剩余未生成条码数量");  
	    		}
	    		/*if(rs0.getInt("pr_tracekind")==1 && zxbzs!=1){
	    			BaseUtil.showError("单件管控物料的分装数量只能为1");
	    		}*/
	    		if(rs0.getDouble("pd_inqty")>0){
	    			 int bqty = 0;
				     double aqty = 0;
				      
				     Map<Object,Object> store1 = new HashMap<Object,Object>();
		        	 store1.put("bi_piclass", rs0.getString("pi_class"));
		             store1.put("bi_whcode", rs0.getString("pd_whcode"));
		             store1.put("bi_batchcode",ba_code);
		             store1.put("bi_batchid", ba_id);
		             store1.put("bi_prodid", rs0.getGeneralInt("pr_id"));
		             store1.put("bi_piid",id);
		             store1.put("bi_pdid",rs0.getGeneralInt("pd_id"));
		             store1.put("bi_pdno",rs0.getGeneralInt("pd_pdno"));
		             store1.put("bi_inoutno",rs0.getString("pi_inoutno"));
		             store1.put("bi_prodcode",rs0.getString("pd_prodcode"));
		             store1.put("bi_vendbarcode",s.get("vendercode"));
		             store1.put("bi_ordercode",rs0.getString("pd_ordercode"));
		             store1.put("bi_location",rs0.getString("pd_location"));	
		             store1.put("bi_inman", SystemSession.getUser().getEm_name());
		             DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		             Date date;
		             Date date1 = new Date();
		             Timestamp time =Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
					 try {
						 if(s.get("madedate") != null && !("").equals(s.get("madedate"))){
							 date = inputFormat.parse(s.get("madedate").toString());
					    	 time = Timestamp.valueOf(inputFormat.format(date));
						 }else{
							 date = inputFormat.parse(rs0.getString("pd_date").toString());
					    	 time = Timestamp.valueOf(inputFormat.format(date));   //如果前台没有传生产日期
						 }
					 }catch (ParseException e) {
						 e.printStackTrace();
					 }					 
					 store1.put("bi_madedate",time);
					 store1.put("bi_indate",Timestamp.valueOf(inputFormat.format(date1)));
					 store1.put("ve_id", ve_id);
					 //（针对条码）
					 Double sum = 0.0;
					 int arlength = 0;
					 double [] mantissa = new double[]{};
					 if (s.get( "pd_mantissapackage" ) != null && !("").equals( s.get( "pd_mantissapackage" ) ) ) {
		    		 	String str = (String) s.get("pd_mantissapackage");
		    		 	String []ar = str.split(",");
		    		 	arlength = ar.length;
		    		    mantissa = new double[arlength];
		    		 	for(int i=0;i<arlength;i++){
		    		 		Double num = Double.valueOf(ar[i]);
		    		 		mantissa[i] = num;
		    		 		sum+=num;
		    		 	};	
		    		 	if((rest-sum) % zxbzs !=0){
		    		 		BaseUtil.showError("本次数量-尾数数量不是分装数量的整数倍!");
		    		 	}
		    		 }
					 rest = rest - sum;
		             bqty = (int) (rest / zxbzs); //本次数量除以最小包装数，最小包装数件数
		             aqty = (new BigDecimal(Double.toString(rest))).subtract(new BigDecimal(
		                        Double.toString(NumberUtil.mul(Double.valueOf(bqty), zxbzs)))).doubleValue(); //取余数，相当于pd_qty- (num*pr_zxbzs) 			       			            
		            //如果外箱容量不存在，则只插入条码即可
					 if(box_zxbzs == 0 || box_zxbzs == -1){
						if (bqty >= 1) {//先将整的插入barcodeio
			                for (int i = 0; i < bqty; i++) {
		                    	 insertBar(caller,store1,zxbzs);  
		                    	 store1.put("ve_id", ve_id);
			                }
			            }
						if(arlength>0){
							for(int i=0;i<arlength;i++){//如果存在尾箱，就将解析的尾箱数赋值
			    		 		insertBar(caller,store1,mantissa[i]); 
								store1.put("ve_id", ve_id);
			    		 	}
						}else if (aqty > 0) {//没有尾箱，则将余数赋值
						        insertBar(caller,store1,aqty); 
							 	store1.put("ve_id", ve_id);
						} 	 
					}else{
						double temzxbzs = zxbzs;
						if(arlength>0){ //如果尾箱数存在，则增加
							 bqty += arlength;
						}else{
							 if(aqty > 0){//如果没有尾箱数，增长1就可以了，装不满一箱
					        	 bqty +=1;
					         }
						}
				         for(int i=0;i<bqty;i++){
				        	 if(i%box_zxbzs == 0){//整箱
				        		 outBox = verifyApplyDao.outboxMethod(rs0.getString("pr_id"),"2");
				        	 }
				        	 if(arlength>0 && i>=bqty-arlength){//存在尾箱数
				        		 temzxbzs = mantissa[i+arlength-bqty];
				        	 }else{ //不存在尾箱数剩余的装不满一箱
					        	 if( i+1 == bqty && aqty>0){//
					        		 temzxbzs = aqty;
					        	 }
				        	 }
			        	 	insertBarPd(caller,pa_id,outBox,temzxbzs,store1);
							store1.put("ve_id", ve_id);
				         }
					}	
				}
	    		
	    		//更新出入库单中明细行中的条码数量
		        baseDao.execute("update ProdIODetail set (pd_barcodeinqty,pd_barcodeoutqty)=(select NVL(sum(nvl(bi_inqty,0)),0),NVL(sum(nvl(bi_outqty,0)),0) from barcodeio where bi_pdid=pd_id )" +
		        				" where  pd_piid=" + id+"and pd_id="+pd_id );
		        //如果主表pi_pdastatus为空时，就更新为未入库
		        baseDao.execute("update prodinout set pi_pdastatus = '未入库' where pi_id="+id+" and nvl(pi_pdastatus,' ') <> '未入库'");
		       
		        if(bopost){//更新批号对应的是否有条码
		        	baseDao.execute("update batch set ba_hasbarcode=-1 where ba_id=?",ba_id);
		        	//更新出库单状态未备料
		        	baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id in(select distinct pd_piid from prodiodetail where pd_batchid=? and pd_outqty>0) and nvl(pi_pdastatus,' ')<>'未备料'",ba_id);
		        }
	        }else{
		       BaseUtil.showError("单据已入库状态不允许此操作!");
		    }
    	 }
	}

    //清除明细
    @Override
    @Transactional
    public void deleteAllBarDetails(String caller, Integer pi_id,String biids) {
    	SqlRowList rs0 = null;
    	int status=0;
        //确认入库不允许删除,入库的单据才可删除，出库单据不可在此删除
        SqlRowList rs = baseDao.queryForRowSet("select max(pi_pdastatus) pi_pdastatus,max(pd_outqty) pd_outqty,max(pd_status) pd_status from prodinout left join prodiodetail on pd_piid=pi_id where pi_id=? ",pi_id);
        if(rs.next()){
        	if("已入库".equals(rs.getString("pi_pdastatus"))){
        		 BaseUtil.showError("单据条码状态已入库，不允许删除!");
        	}
        	if(rs.getDouble("pd_outqty")>0){
        		 BaseUtil.showError("单据属于出库类型，不允许直接删除，可在PDA中撤销采集!");
        	}
        	status = rs.getInt("pd_status");
        }
        if(biids == null || "".equals(biids)){  //没有值全部删除    
        	//判断barcodeio 是否有bi_status=99 
            int cn=baseDao.getCount("select count(1) from barcodeio where bi_piid="+pi_id+" and bi_status=99");
        	if(cn>0){
     	   		BaseUtil.showError("存在已确认入库条码，不允许删除!");
        	}
        	if(status == 99){
        		rs0 = baseDao.queryForRowSet("select distinct pi_id from prodinout left join prodiodetail on pd_piid=pi_id where pd_outqty>0 and pd_batchid in(select bi_batchid from barcodeio where bi_piid=? group by bi_batchid)",pi_id);
        	}
        	baseDao.deleteByCondition("BarcodeIO","bi_piid=" + pi_id);
	       	//更新出入库单中明细行中的条码数量
        	baseDao.execute("update ProdIODetail set pd_barcodeinqty=0 where pd_piid=?",pi_id);
        	//更新主表pi_pdastatus状态
        	baseDao.execute("update prodinout set pi_pdastatus='' where pi_id=?",pi_id);
        	if(status == 99){
	        	//单据已过帐，则更新批号是否有条码
	            baseDao.execute("update batch set ba_hasbarcode=0 where ba_id in (select pd_batchid from prodiodetail where pd_piid=?) and ba_hasbarcode<>0",pi_id);
        	}
        }else{//有值勾选删除
        	 if(status == 99){
        		 rs0 = baseDao.queryForRowSet("select distinct pi_id from prodinout left join prodiodetail on pd_piid=pi_id where pd_batchid in (select bi_batchid from barcodeio where bi_id in("+biids+")) and pd_outqty>0 ") ;
        	 }
        	 String[] array = biids.split(",");
        	 int length = array.length;
        	 for(int i=0;i<length; i++){
        		//判断barcodeio 是否有bi_status=99 
    	        int cn=baseDao.getCount("select count(1) from barcodeio where bi_id = "+array[i]+" and bi_status=99 ");
    	       	if(cn>0){
    	    	   	BaseUtil.showError("条码已确认入库条码，不允许删除!");
    	       	}
        		 baseDao.deleteByCondition("BarcodeIO","bi_id = '"+array[i]+"'");
        	 }       	 
        	 //更新出入库单中明细行中的条码数量
        	baseDao.execute("update ProdIODetail set pd_barcodeinqty=(select NVL(sum(nvl(bi_inqty,0)),0) from barcodeio where bi_pdid=pd_id) where  pd_piid=" + pi_id);
        	//更新主表pi_pdastatus状态
        	rs = baseDao.queryForRowSet("select bi_id from barcodeio where bi_piid=?",pi_id);
        	if(!rs.next()){
        		baseDao.execute("update prodinout set pi_pdastatus = ' ' where pi_id=?",pi_id); 
           }
        	if(status == 99){
	        	//单据已过帐，则更新批号是否有条码
	            baseDao.execute("update batch set ba_hasbarcode=0 where ba_id in (select pd_batchid from prodiodetail where pd_piid=?) and ba_hasbarcode<>0 and not exists(select 1 from barcodeio where bi_piid=? and bi_inqty>0 and bi_batchid=ba_id)",pi_id,pi_id);
        	}
        }
    	//更新对应出库单据的备料状态
    	if(status == 99){//单据已过账才会被抓到出库单据中
	    	if(rs0.hasNext()){
	    		int opiid=0;
	    		while(rs0.next()){
	    			opiid = rs0.getInt("pi_id");
		        	//更新状态
					rs = baseDao.queryForRowSet("select count(1)cn from prodiodetail where pd_piid=? and pd_batchid in (select bi_batchid from barcodeio where bi_status=0 and bi_inqty>0 and bi_batchid>0)",opiid);
					if(rs.next() && rs.getInt("cn") > 0){
						baseDao.execute("update prodinout set pi_pdastatus ='未备料' where pi_id=?",opiid);
					}else{
						rs = baseDao.queryForRowSet("select count(1)cn from prodiodetail where pd_piid=? and exists (select 1 from barcode where bar_status=1 and bar_batchid=pd_batchid)",opiid);
						if(rs.next() && rs.getInt("cn") ==0){
							baseDao.execute("update prodinout set pi_pdastatus ='无条码' where pi_id=?",opiid);
						}else{
							rs = baseDao
									.queryForRowSet(
											"select count(1) cn from (select pd_prodcode,pd_whcode,sum(pd_outqty)qty from prodiodetail "
													+ " where pd_piid=? and pd_batchid in (select bar_batchid from barcode) group by pd_prodcode,pd_whcode)A left join (select bi_prodcode,bi_whcode,sum(bi_outqty)qty "
													+ " from barcodeio where bi_piid=? group by bi_prodcode,bi_whcode)B on (pd_prodcode=bi_prodcode and pd_whcode=bi_whcode)"
													+ " where A.qty>NVL(B.qty,0)", opiid, opiid);
							if (rs.next() && rs.getInt("cn") == 0) {
								baseDao.execute("update prodinout set pi_pdastatus ='已备料' where pi_id=?", opiid);
							}else{
								baseDao.execute("update prodinout set pi_pdastatus ='未备料' where pi_id=?", opiid);		
							}
						}
					}
	    		}
	    	}
    	}
    }

    @Override
    public List<Map<String, Object>> getDatasFields(String condition) {
        SqlRowList rs = baseDao.queryForRowSet(
                "select la_id  as \"la_id\",la_code as \"la_code\" from Label left join labelPrintSetting on la_id=lps_laid where lps_caller='" +
                condition + "' and la_statuscode='AUDITED'");

        return rs.getResultList();
    }

    @Override
    public void batchGenBarOBcode(String caller, String formStore) {}

    private void insertBar(String caller, Map<Object, Object> store,Double inqty) {
        String bar_code;
        Object ve_id = store.get("ve_id");
        if ((ve_id != null) && (ve_id != "")) {
            bar_code = verifyApplyDao.barcodeMethod(store.get("bi_prodcode").toString(),ve_id.toString(),0);
        } else {
            bar_code = verifyApplyDao.barcodeMethod(store.get("bi_prodcode").toString(), "",0);
        }
        int bi_id = baseDao.getSeqId("BARCODEIO_SEQ");
        //生成BARCODEIO		
        store.put("bi_id", bi_id);
        store.put("bi_barcode", bar_code);
        store.put("bi_printstatus", "0");
        store.remove("ve_id");
        store.put("bi_inqty", inqty);
        String formSql = SqlUtil.getInsertSqlByFormStore(store, "BARCODEIO",
                new String[] {  }, new Object[] {  });
        baseDao.execute(formSql);
    }

    private void insertBarPd(String caller, int pa_id, String out_boxcode,
        Double qty,Map<Object, Object> store) {
        List<String> sqls = new ArrayList<String>();   
        String bar_code;
        Object ve_id = store.get("ve_id");
        if ((ve_id != null) && (ve_id != "")) {
            bar_code = verifyApplyDao.barcodeMethod(store.get("bi_prodcode").toString(),ve_id.toString(),0);
        } else {
            bar_code = verifyApplyDao.barcodeMethod(store.get("bi_prodcode").toString(), "",0);
        }
        int bi_id = baseDao.getSeqId("BARCODEIO_SEQ");
        //生成BARCODEIO		
        store.put("bi_id", bi_id);
        store.put("bi_barcode", bar_code);
        store.put("bi_printstatus", "0");
        store.put("bi_inqty", qty);
        store.remove("ve_id");
        String formSql = SqlUtil.getInsertSqlByFormStore(store, "BARCODEIO",
                new String[] {}, new Object[] {});
        baseDao.execute(formSql);
        sqls.add("update barcodeio set bi_outboxcode='" + out_boxcode +
            "',bi_outboxid=" + pa_id + " where bi_id=" + bi_id);
        baseDao.execute(sqls);
    }
    
    @Override
    public void freezeBarcode(String caller, String condition) {
    	SqlRowList rs = baseDao.queryForRowSet("select bar_id,nvl(bar_remain,0) bar_remain,bar_code,nvl(bar_status,0) bar_status,nvl(bar_lockstatus,0) bar_lockstatus,bar_batchcode from barcode where "+condition);
    	String err = "";
    	List<String> sqls = new ArrayList<String>();
    	List<Integer> ids = new ArrayList<Integer>();
    	while(rs.next()){
    		if(rs.getInt("bar_status")!=1){
    			err+="条码["+rs.getString("bar_code")+"]不是在库状态!<br>";
    		}else if(rs.getInt("bar_lockstatus")!=0){
    			err+="条码["+rs.getString("bar_code")+"]已冻结!<br>";
    		}else{
    			ids.add(rs.getInt("bar_id"));
    			sqls.add("update batch set ba_lockqty=nvl(ba_lockqty,0)+"+rs.getInt("bar_remain")+" where ba_code='"+rs.getString("bar_batchcode")+"'");
    		}
    	}
    	if("".equals(err)){
    		sqls.add("update barcode set bar_lockstatus=-1 where "+condition);
    		baseDao.execute(sqls);
    		for(int id : ids){
    			baseDao.logger.others("冻结条码", "冻结条码成功", caller, "bar_id", id);
    		}
    	}else{
    		BaseUtil.showError(err);
    	}
    }
    
    @Override
    public void releaseBarcode(String caller, String condition) {
    	SqlRowList rs = baseDao.queryForRowSet("select bar_id,nvl(bar_remain,0) bar_remain,bar_code,nvl(bar_status,0) bar_status,nvl(bar_lockstatus,0) bar_lockstatus,bar_batchcode from barcode where "+condition);
    	String err = "";
    	List<String> sqls = new ArrayList<String>();
    	List<Integer> ids = new ArrayList<Integer>();
    	while(rs.next()){
    		if(rs.getInt("bar_status")!=1){
    			err+="条码["+rs.getString("bar_code")+"]未入库!<br>";
    		}else if(rs.getInt("bar_lockstatus")==0){
    			err+="条码["+rs.getString("bar_code")+"]未冻结!<br>";
    		}else{
    			ids.add(rs.getInt("bar_id"));
    			sqls.add("update batch set ba_lockqty=nvl(ba_lockqty,0)-"+rs.getInt("bar_remain")+" where ba_code='"+rs.getString("bar_batchcode")+"'");
    		}
    	}
    	if("".equals(err)){
    		sqls.add("update barcode set bar_lockstatus=0 where "+condition);
    		baseDao.execute(sqls);
    		for(int id : ids){
    			baseDao.logger.others("释放条码", "释放条码成功", caller, "bar_id", id);
    		}
    	}else{
    		BaseUtil.showError(err);
    	}
    }

	@Override
	public List<Map<String, Object>> breakingBatch(String or_barcode, Double or_remain, Double bar_remain) {
		String level = null;
		boolean ismsd = false;
		double restTime = 0;
		List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
		SqlRowList rs0;
		if(bar_remain<=0){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"拆分数必须大于0!");
		}
		// 判断是否是在烘烤状态
		rs0 = baseDao.queryForRowSet("select ms_action from (select ms_action from msdlog where ms_barcode=?" + 
				 " order by ms_id desc) where rownum=1",or_barcode);
		if (rs0.next() && "入烘烤".equals(rs0.getString("ms_action"))) {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码：" + or_barcode + "，状态为在烘烤，请先出烘烤再拆分!");
		}
		
		rs0 = baseDao.queryForRowSet("select * from barcode where bar_code=? and bar_status=1",or_barcode);
		if(rs0.next()) {
			double total_remain = rs0.getDouble("bar_remain");// 原条码的总数
			if(total_remain<=0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码库存不足!");
			}
			int or_barid = rs0.getInt("bar_id");
			Map<String, Object> mp1 = rs0.getCurrentMap();
			// 更新原barcode表，锁定该条码正在拆分，不允许同时操作
			baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + or_barid + "'");
			// 判断当前条码数量是否等于前台传送参数的条码数，不等于则返回提示提示条码已经拆分
			if (NumberUtil.compare(total_remain, or_remain) != 0) {
				baseDao.updateByCondition("Barcode", "bar_status=1", "bar_id='" + or_barid + "'");
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码已经被拆分!");
			}
			
			if (bar_remain >= total_remain) {
				baseDao.updateByCondition("Barcode", "bar_status=1", "bar_id='" + or_barid + "'");
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"拆分数量必须小于原条码数量!");
			}

			// 新增的barcode 中的前条形码
			Object[] objs = baseDao.getFieldsDataByCondition("barcode left join Vendor  on bar_vendcode=ve_code", new String[] { "ve_id"}, "bar_id=" + or_barid);// 供应商ID
			// 新增Barcode1分拆
			int bar_id = baseDao.getSeqId("BARCODE_SEQ");
			String bar_code;
			if (objs[0] != null) {
				bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), objs[0].toString(),0);// 生成条码
			} else {
				bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), "",0);// 无供应商信息
			}
			mp1.remove("BAR_PRINTCOUNT");
			mp1.remove("BAR_LASTPRINTDATE");
			mp1.remove("BAR_LASTPRINTMAN");
			mp1.put("BAR_LASTCODE", or_barcode);
			mp1.put("BAR_SOURCECODE", or_barcode);
			mp1.put("BAR_LASTID", or_barid);
			mp1.put("BAR_ID", bar_id);
			mp1.put("BAR_KIND", "1");// 类型为分拆 ：1,合并：2，原始：0
			mp1.put("BAR_CODE", bar_code);
			mp1.put("BAR_REMAIN", bar_remain);
			mp1.put("BAR_STATUS", "1");
			mp1.put("BAR_RECORDDATE", DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
			baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp1, "barcode", new String[] {}, new Object[] {}));
			// 产生barcodechange记录
			baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
					+ "values(BARCODECHANGE_SEQ.nextval,?,1,sysdate,?,?,?,?,?,?,?,?)",mp1.get("bar_prodcode"),SystemSession.getUser().getEm_name(),"",total_remain,or_barcode,or_barid,bar_code,bar_id,bar_remain);
			
			// 湿敏元件产生MSDLOG
			if (ismsd && !"1".equals(level)) {
				baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
						+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)"
						,bar_code,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),bar_remain);
			}
			
			Map<String,Object> mapr = new HashMap<String, Object>();
			mapr.put("BAR_ID", bar_id);
			mapr.put("BAR_CODE", bar_code);
			mapr.put("BAR_REMAIN", bar_remain);
			rlist.add(mapr);
			
			boolean bo = baseDao.isDBSetting("BarCodeSetting", "BarInvalidAfBatch");// 条码拆分后原条码作废,默认原条码不作废*/	
			double rest = (new BigDecimal(Double.toString(total_remain))).subtract(new BigDecimal(Double.toString(bar_remain))).doubleValue();
			
			if(bo){// 如果确定原条码作废，则会生成两个新的条码
				bar_id = baseDao.getSeqId("BARCODE_SEQ");
				if (objs[0] != null) {
					bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), objs[0].toString(),0);// 生成条码
				} else {
					bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), "",0);// 生成条码
				}
				mp1.put("BAR_ID", bar_id);
				mp1.put("BAR_CODE", bar_code);
				mp1.put("BAR_REMAIN", rest);
				mp1.put("BAR_RECORDDATE",DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
				rlist.add(mp1);
				baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp1, "barcode", new String[] {}, new Object[] {}));
				baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + or_barid + "'");
				// 产生barcodechange记录
				baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
						+ "values(BARCODECHANGE_SEQ.nextval,?,1,sysdate,?,?,?,?,?,?,?,?)",mp1.get("bar_prodcode"),SystemSession.getUser().getEm_name(),"",total_remain,or_barcode,or_barid,bar_code,bar_id,rest);			
				// 湿敏元件产生MSDLOG
				if (ismsd && !"1".equals(level)) {
					baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
							+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)",
							bar_code,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),rest);
				
					Map<String,Object> mapl = new HashMap<String, Object>();
					mapl.put("BAR_ID", bar_id);
					mapl.put("BAR_CODE", bar_code);
					mapl.put("BAR_REMAIN", rest);
					rlist.add(mapl);
				}
			} else {// 如果原条码不作废，则只会生成一个新的条码为你需要拆分的数量，原条码数量减少// 更新原barcode表，修改锁定，更新原条码数量
				baseDao.updateByCondition("Barcode", "bar_status=1,bar_remain=" + rest, "bar_id=" + or_barid);
				// 湿敏元件产生MSDLOG
				if (ismsd && !"1".equals(level)) {
					baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
							+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)",
							or_barcode,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),bar_remain);
				}
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该条码不是在库状态");
		}
		return rlist;
	}

	@Override
	@Transactional
	public List<Map<String, Object>> combiningAndBreaking(String ids, Double total_remain,Double zxbzs, String every_remain) {
		List<Map<String, Object>> returnList =  new ArrayList<Map<String, Object>>();
		ids = ids.replaceAll("(?:^,+)|(?:,+$)|(,),+", "$1");
		SqlRowList rs2 = baseDao.queryForRowSet("select wm_concat(bar_id) bar_id from barcode where bar_code in ("+ids+")"); 
		if(rs2.next()){
			ids = rs2.getString("bar_id");
		}
		int cn,cn1=0;
		cn = baseDao.getCount("select count(distinct bar_prodcode) from barcode where bar_id in ("+ids+")");
		if(cn >1){
			BaseUtil.showError("需要合并的条码物料不一致不允许合并!");
		}else{
			cn1 = baseDao.getCount("select count(distinct bar_batchcode) from barcode where bar_id in ("+ids+")");
			if (cn1 > 1){
				BaseUtil.showError("需要合并的条码批次不一致不允许合并!");
			} else {
				cn = baseDao.getCount("select count(1) from barcode where bar_id in (" + ids + ") and (bar_remain <= 0 or nvl(bar_status,0) <> 1)");
				if(cn > 0){
					BaseUtil.showError("条码库存不足或者不是在库状态");
				}
				String bar_code = "";
				SqlRowList rs;
				int idx = ids.indexOf(",");
				String temp_id = "";
				// every_remain   尾数分装
				Double sum = 0.0 ;
				String[] array = every_remain.split(",");
				if(every_remain != null && !("").equals(every_remain)){
					if(every_remain.indexOf(",")>0){						
						for(int i = 0;i<array.length;i++){
							sum += Double.valueOf(array[i]);
						}
					}else{
						sum = Double.valueOf(every_remain);
					}
				}
				if(total_remain <= 0){
					BaseUtil.showError("条码总数量不能小于0");
				}else if(zxbzs <= 0){
					BaseUtil.showError("本次数量必须大于0");
				}else if(sum < 0){
					BaseUtil.showError("尾包数量之和不能小于0");
				}else if((total_remain-sum) % zxbzs !=0){ 
	    		 		BaseUtil.showError("本次数量-尾数数量不是分装数量的整数倍!");
				}
				if(idx>0){ // 多个条码合并拆分
					 temp_id = ids.substring(0, idx);
					 Map<String, Object> mp = baseDao.getJdbcTemplate().queryForMap("select * from barcode where bar_id  =" + temp_id);
						String bar_prodcode = "";
						bar_prodcode = mp.get("bar_prodcode").toString();
						bar_code = verifyApplyDao.barcodeMethod(bar_prodcode, "",0);
						int bar_id = baseDao.getSeqId("BARCODE_SEQ");
						mp.put("BAR_ID", bar_id);
						mp.put("BAR_CODE", bar_code);
						mp.put("BAR_KIND", "2");// 类型为合并
						mp.put("BAR_REMAIN", total_remain);
						mp.put("BAR_BATCHQTY", total_remain);
						mp.put("BAR_STATUS", "1");
						// 合并成一条barcode
						baseDao.execute(SqlUtil.getInsertSqlByMap(mp, "barcode"));
						int bc_id;
						String[] id = ids.split( "," );
						int length = id.length;
						String prodcode;
						String code;
						double remain;
						for (int i = 0;i < length;i++) {  //循环合并的多个条码
							rs = baseDao.queryForRowSet("select bar_prodcode,bar_remain,bar_code from barcode where bar_id = ?",id[i]);
							if(rs.next()){
								prodcode = rs.getString("bar_prodcode");
								code = rs.getString("bar_code");
								remain = rs.getDouble("bar_remain");
								// 产生barcodechange记录
								bc_id = baseDao.getSeqId("BARCODECHANGE_SEQ");
							    baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) values("
									+ bc_id
									+ ",'"
									+ prodcode
									+ "',2,sysdate,'"
									+ SystemSession.getUser().getEm_name()
									+ "','',"
									+ remain
									+ ",'"
									+ code
									+ "',"
									+ id[i]
									+ ",'"
									+ bar_code
									+ "',"
									+ bar_id
									+ "," + total_remain + ")");
								// 更新原barcode
								if(id[i]!=null && !("").equals(id[i])){
									baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + id[i] + "'");
								}
							}
						}
						// sum  尾数之和    total_remain 总数
						if(every_remain != null && !("").equals(every_remain)){							
							if(every_remain.indexOf(",") >0){							
								String[] remains = every_remain.split( "," );
								for (int i = 0;i < remains.length;i++) {	
									returnList.addAll(breaking(bar_code,sum,Double.valueOf(remains[i].toString()),true,bar_code));
								}
							}else{
								returnList.addAll(breaking(bar_code,sum,Double.valueOf(every_remain),true,bar_code));
							}
						}
						if(zxbzs >0){
							int count = (int)((total_remain-sum) / zxbzs);
							for(int i =0;i<count;i++){
								returnList.addAll(breaking(bar_code,total_remain-sum,zxbzs,true,bar_code));
							}
						}
				}else{  //一个条码
					rs = baseDao.queryForRowSet("select bar_code,bar_remain from barcode where bar_id=?",ids);
					if(rs.next()){							
						bar_code = rs.getString("bar_code");
						if(total_remain != rs.getDouble("bar_remain")){
							BaseUtil.showError("条码数量不符,请刷新页面重试");
						}
					}else{
						BaseUtil.showError("条码不存在");
					}
					if(every_remain != null && !("").equals(every_remain)){
						if(every_remain.indexOf(",") >0){							
							String[] remains = every_remain.split( "," );
							if(remains.length > 0){						
								for (int i = 0;i < remains.length-1;i++) {
									List<Map<String, Object>> onlyList =  new ArrayList<Map<String, Object>>();
									Map<String,Object> mp = new HashMap<String, Object>();
									mp.put("BAR_ID", ids);
									mp.put("BAR_CODE", bar_code);
									mp.put("BAR_REMAIN", total_remain);
									onlyList.add(mp);
									returnList.addAll(onlyList);
									returnList.addAll(breaking(bar_code,sum,Double.valueOf(remains[i].toString()),true,bar_code));
								}
							}
						}else{
							returnList.addAll(breaking(bar_code,sum,Double.valueOf(every_remain),true,bar_code));
						}
					}
					if(zxbzs >0){
						int count = (int)((total_remain-sum) / zxbzs);
						for(int i =0;i<count;i++){
							returnList.addAll(breaking(bar_code,total_remain-sum,zxbzs,true,bar_code));
						}
					}	
			}
			}
		}
		return returnList;
	}
	
	//因为拆分方法需要调用两次   所以方法拉到外面来执行
	private List<Map<String, Object>> breaking(String or_barcode, Double or_remain, Double bar_remain,boolean combinAndBreak,String lastcode) {
		String level = null;
		boolean ismsd = false;
		double restTime = 0;
		List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
		SqlRowList rs0;
		if(bar_remain<=0){
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"拆分数必须大于0!");
		}
		// 判断是否是在烘烤状态
		rs0 = baseDao.queryForRowSet("select ms_action from (select ms_action from msdlog where ms_barcode=?" + 
				 " order by ms_id desc) where rownum=1",or_barcode);
		if (rs0.next() && "入烘烤".equals(rs0.getString("ms_action"))) {
			throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码：" + or_barcode + "，状态为在烘烤，请先出烘烤再拆分!");
		}
		
		rs0 = baseDao.queryForRowSet("select * from barcode where bar_code=? and bar_status=1",or_barcode);
		if(rs0.next()) {
			double total_remain = rs0.getDouble("bar_remain");// 原条码的总数
			if(total_remain<=0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码库存不足!");
			}
			int or_barid = rs0.getInt("bar_id");
			Map<String, Object> mp1 = rs0.getCurrentMap();
			// 更新原barcode表，锁定该条码正在拆分，不允许同时操作
			baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + or_barid + "'");
			if(!combinAndBreak){				
				// 判断当前条码数量是否等于前台传送参数的条码数，不等于则返回提示提示条码已经拆分
				if (NumberUtil.compare(total_remain, or_remain) != 0) {
					baseDao.updateByCondition("Barcode", "bar_status=1", "bar_id='" + or_barid + "'");
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码已经被拆分!");
				}
				if (bar_remain >= total_remain) {
					baseDao.updateByCondition("Barcode", "bar_status=1", "bar_id='" + or_barid + "'");
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"拆分数量必须小于原条码数量!");
				}
			}

			// 新增的barcode 中的前条形码
			Object[] objs = baseDao.getFieldsDataByCondition("barcode left join Vendor  on bar_vendcode=ve_code", new String[] { "ve_id"}, "bar_id=" + or_barid);// 供应商ID
			// 新增Barcode1分拆
			int bar_id = baseDao.getSeqId("BARCODE_SEQ");
			String bar_code;
			if (objs[0] != null) {
				bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), objs[0].toString(),0);// 生成条码
			} else {
				bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), "",0);// 无供应商信息
			}
			if(combinAndBreak){
				mp1.put("BAR_LASTCODE", lastcode);
			}else{				
				mp1.put("BAR_LASTCODE", or_barcode);
			}
			if(mp1.get("bar_sourcecode") != null && !("").equals(mp1.get("bar_sourcecode"))){				
				mp1.put("bar_sourcecode",mp1.get("bar_sourcecode"));
			}else{
				mp1.put("bar_sourcecode",or_barcode);
			}
			mp1.remove("BAR_PRINTCOUNT");
			mp1.remove("BAR_LASTPRINTMAN");
			mp1.remove("BAR_LASTPRINTDATE");
			mp1.put("BAR_LASTID", or_barid);
			mp1.put("BAR_ID", bar_id);
			mp1.put("BAR_KIND", "1");// 类型为分拆 ：1,合并：2，原始：0
			mp1.put("BAR_CODE", bar_code);
			mp1.put("BAR_REMAIN", bar_remain);
			mp1.put("BAR_STATUS", "1");
			mp1.put("BAR_PIID", 0);
			mp1.put("BAR_RECORDDATE", DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
			baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp1, "barcode", new String[] {}, new Object[] {}));
			// 产生barcodechange记录
			baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
					+ "values(BARCODECHANGE_SEQ.nextval,?,1,sysdate,?,?,?,?,?,?,?,?)",mp1.get("bar_prodcode"),SystemSession.getUser().getEm_name(),"",total_remain,or_barcode,or_barid,bar_code,bar_id,bar_remain);
			
			// 湿敏元件产生MSDLOG
			if (ismsd && !"1".equals(level)) {
				baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
						+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)"
						,bar_code,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),bar_remain);
			}
			
			Map<String,Object> mapr = new HashMap<String, Object>();
			mapr.put("BAR_ID", bar_id);
			mapr.put("BAR_CODE", bar_code);
			mapr.put("BAR_REMAIN", bar_remain);
			rlist.add(mapr);
			
			boolean bo = baseDao.isDBSetting("BarCodeSetting", "BarInvalidAfBatch");// 条码拆分后原条码作废,默认原条码不作废*/	
			double rest = (new BigDecimal(Double.toString(total_remain))).subtract(new BigDecimal(Double.toString(bar_remain))).doubleValue();
			
			if(bo){// 如果确定原条码作废，则会生成两个新的条码
				bar_id = baseDao.getSeqId("BARCODE_SEQ");
				if (objs[0] != null) {
					bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), objs[0].toString(),0);// 生成条码
				} else {
					bar_code = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(), "",0);// 生成条码
				}
				mp1.put("BAR_ID", bar_id);
				mp1.put("BAR_CODE", bar_code);
				mp1.put("BAR_REMAIN", rest);
				mp1.put("BAR_RECORDDATE",DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
				rlist.add(mp1);
				baseDao.execute(SqlUtil.getInsertSqlByFormStore(mp1, "barcode", new String[] {}, new Object[] {}));
				baseDao.updateByCondition("Barcode", "bar_status=-2", "bar_id='" + or_barid + "'");
				// 产生barcodechange记录
				baseDao.execute("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
						+ "values(BARCODECHANGE_SEQ.nextval,?,1,sysdate,?,?,?,?,?,?,?,?)",mp1.get("bar_prodcode"),SystemSession.getUser().getEm_name(),"",total_remain,or_barcode,or_barid,bar_code,bar_id,rest);			
				// 湿敏元件产生MSDLOG
				if (ismsd && !"1".equals(level)) {
					baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
							+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)",
							bar_code,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),rest);
				
					Map<String,Object> mapl = new HashMap<String, Object>();
					mapl.put("BAR_ID", bar_id);
					mapl.put("BAR_CODE", bar_code);
					mapl.put("BAR_REMAIN", rest);
					rlist.add(mapl);
				}
			} else {// 如果原条码不作废，则只会生成一个新的条码为你需要拆分的数量，原条码数量减少// 更新原barcode表，修改锁定，更新原条码数量
				baseDao.updateByCondition("Barcode", "bar_status=1,bar_remain=" + rest, "bar_id=" + or_barid);
				// 湿敏元件产生MSDLOG
				if (ismsd && !"1".equals(level)) {
					baseDao.execute("insert into MSDLog(ms_id,ms_date,ms_barcode,ms_level,ms_lifetime,ms_action,"
							+ " ms_man,ms_location,ms_prodcode,ms_batchcode,ms_qty)values (MSDLOG_SEQ.nextval,sysdate,?,?,?,'拆封',?,?,?,?,?)",
							or_barcode,level,restTime,SystemSession.getUser().getEm_name(),mp1.get("bar_location"),mp1.get("bar_prodcode"), mp1.get("bar_batchcode"),bar_remain);
				}
			}
		} else {
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"该条码不是在库状态");
		}
		return rlist;
	}

}
