package com.uas.erp.service.scm.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.service.scm.BarStockCodeService;

@Service("barStockCodeService")
public class BarStockCodeServiceImpl implements BarStockCodeService{
	@Autowired 
    private BaseDao baseDao;
	@Autowired 
    private  VerifyApplyDao verifyApplyDao;		
    /**
     * 保存barcodeSet
     */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveBarcode(String caller, String gridStore) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,"BarStocktakingDetailDet", "bdd_id");
		baseDao.execute(gridSql);
	    SqlRowList rs = baseDao.queryForRowSet("select count(0) cn from BarStocktakingDetail left join BarStocktakingDetailDet on bdd_bsdid=bsd_id  where bsd_id="+ gstore.get(0).get("bdd_bsdid")+" having sum(bdd_inqty)>bsd_inqty  group by bdd_inqty");
		if(rs.next()){
			if(rs.getInt("cn")>0){
				BaseUtil.showError("入库数总和不允许超过来料总量！");
			}
		}
	}

	 /** 
	    * 批量生成条码
	    */
		@Override
	    @Transactional
	    public void batchGenBarcode(String caller,int id,String data) {
	    	 List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
	    	 double zxbzs=0;
	    	 int bsd_id=0,pa_id=0;
	    	 double box_zxbzs=0,rest=0,temp=0;
	    	 String outBox=null;
	    	 checkNumber(id);
	    	 for (Map<Object, Object> s : gstore) {
		    	 if(s.get("pr_zxbzs") != null && !("").equals(s.get("pr_zxbzs"))){
		    		 temp = Double.valueOf(s.get("pr_zxbzs").toString());
		    		 if(((temp%1)<1 &&(temp%1)>0 )  || temp<=0 ){
		    			 BaseUtil.showError("最小包装数错误！");
		    		 }
		    		 zxbzs = temp;  //最小包装数
	    		 }else{
	    			 BaseUtil.showError("最小包装数错误!");
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
	    		 bsd_id = Integer.valueOf( s.get("bsd_id").toString());     //从表id
	    		 
	    		 SqlRowList rs0 = baseDao.queryForRowSet(
			                "select bs_id,nvl(bsd_whcode,bs_whcode) bsd_whcode,bsd_inqty,bsd_ordercode,pr_id,bs_code,bsd_id,pr_tracekind,bsd_detno,"
	    				 	+"bsd_prodcode,bsd_custvendcode,bs_class,bsd_batchcode,ba_id,bs_indate from BARSTOCKTAKING left join BARSTOCKTAKINGDETAIL on bs_id=bsd_bsid"
	    				 	+" left join batch on ba_code=bsd_batchcode left join product on pr_code=bsd_prodcode where bs_id=? and bsd_id=? and nvl(bs_status,'')='已提交'",id,bsd_id);
		        if (rs0.next()) {
		    		/*if(rs0.getInt("pr_tracekind")==1 && zxbzs!=1){
		    			BaseUtil.showError("单件管控物料的最小包装数只能为1");
		    		}*/
		    		if(rs0.getDouble("bsd_inqty")>0){
		    			 int bqty = 0;
					     double aqty = 0;
					     Map<Object,Object> store1 = new HashMap<Object,Object>();
			             store1.put("bdd_bsid",id);
			             store1.put("bdd_bsdid",rs0.getInt("bsd_id"));
			             store1.put("bdd_detno",rs0.getGeneralInt("bsd_detno"));
			             store1.put("bdd_prodcode",rs0.getString("bsd_prodcode"));
			             store1.put("bdd_vendcode",s.get("vendercode"));
			             DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			             Date date;
			             Timestamp time = null;
						 try {
							 if(s.get("madedate") != null && !("").equals(s.get("madedate"))){
								 date = inputFormat.parse(s.get("madedate").toString());
								 time =Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
						    	 time = Timestamp.valueOf(inputFormat.format(date));
							 }/*else{
								 date = inputFormat.parse(rs0.getString("bs_indate").toString());
						    	 time = Timestamp.valueOf(inputFormat.format(date));   //如果前台没有传生产日期
							 }*/
						 }catch (ParseException e) {
							 e.printStackTrace();
						 }					 
						 store1.put("bdd_madedate",time);
						 Object ve_id = baseDao.getFieldDataByCondition("Vendor left join BARSTOCKTAKINGDETAIL on bsd_custvendcode=ve_code",
					                "ve_id", "bsd_bsid="+id);
						 store1.put("ve_id", ve_id);
						 //（针对条码）
			             bqty = (int) (rest / zxbzs); //本次数量除以最小包装数，最小包装数件数
			             aqty = (new BigDecimal(Double.toString(rest))).subtract(new BigDecimal(
			                        Double.toString(NumberUtil.mul(Double.valueOf(bqty), zxbzs)))).doubleValue(); //取余数，相当于pd_qty- (num*pr_zxbzs) 			       			            
			            //如果外箱容量不存在，则只插入条码即可
						 if(box_zxbzs == 0 || box_zxbzs == -1){
							if (bqty >= 1) {
				                for (int i = 0; i < bqty; i++) {
			                    	 insertBar(caller,store1,zxbzs);  
			                    	 store1.put("ve_id", ve_id);
				                }
				            }
							if (aqty > 0) 
						         insertBar(caller,store1,aqty); 
							 	 store1.put("ve_id", ve_id);
						}else{
							double temzxbzs = zxbzs;
							 if(aqty > 0){
					        	 bqty +=1;
					         }
					         for(int i=0;i<bqty;i++){
					        	 if(i%box_zxbzs == 0){
					        		 outBox = verifyApplyDao.outboxMethod(rs0.getString("pr_id"),"2");
					        	 }
					        	 if( i+1 == bqty && aqty>0){//尾数条码
					        		 temzxbzs = aqty;
					        	 }
					        	 	insertBarPd(caller,pa_id,outBox,temzxbzs,store1);
									store1.put("ve_id", ve_id);
					         }
						}	
						}
		        }else{
			       BaseUtil.showError("单据不是已提交状态不允许此操作!");
			    }
	    	 }
		}
	@Override
	public void deleteAllBarDetails(String caller, String id,String bddids) {
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id='" + id+"'");
		if(status.equals("AUDITED")){
			BaseUtil.showError("单据已审核不允许删除!");
		}      
        if(bddids == null || "".equals(bddids)){  //没有值全部删除        		
        	baseDao.deleteByCondition("barstocktakingdetaildet","bdd_bsid=" + id);
        }else{//有值勾选删除
        	 String[] array = bddids.split(",");
        	 int length = array.length;
        	 for(int i=0 ; i<length ; i++){
        		 baseDao.deleteByCondition("barstocktakingdetaildet","bdd_bsid=" + id +" and bdd_id = '"+array[i]+"'");
        	 }       	 
        }
    
	}

	@Override
	public void batchGenBO(String caller, String formStore) {
		// TODO Auto-generated method stub
		int bqty = 0,pa_id = 0;
		Double aqty;	
		Object ob ;
		String out_boxcode = null,bar_code;
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);				
		Double pr_zxbzs = Double.valueOf(store.get("pr_zxbzs").toString());   
		Double bdd_qty = Double.valueOf(store.get("bdd_qty").toString());
		
		Object status = baseDao.getFieldDataByCondition("BarStocktaking left join BarStocktakingDetail on bs_id=bsd_bsid", "bs_statuscode", "bsd_id=" + store.get("bdd_bsdid"));
		if(status.equals("AUDITED")){
			BaseUtil.showError("单据已审核不允许操作!");
		}
		Object ve_id  = baseDao.getFieldDataByCondition("Vendor left join BarStocktakingDetail on bsd_custvendcode=ve_code OR bsd_custvendcode=ve_name OR bsd_custvendcode=ve_apvendname", "ve_id", "bsd_id='"+store.get("bdd_bsdid")+"'");
		//条码盘盈单该明细行物料入库数总量大于批总量[bdd_qty]，不允许生成条码
		int cn = baseDao.getCount("select count(0) cn from BarStocktakingDetailDet  where bdd_bsdid='"+store.get("bdd_bsdid").toString()+"' HAVING SUM(bdd_inqty)>"+(Double.valueOf(store.get("bsd_inqty").toString())-bdd_qty));	
		if (cn > 0){
			BaseUtil.showError("入库数的总和允许超过了来料总量，不允许操作");
		}
		//获取盘盈单明细行入库日期或者单据日期,一般情况下是一致的
		ob = baseDao.getFieldDataByCondition("product", "pr_id", "pr_code='"+store.get("bdd_prodcode")+"'");
		store.put("bdd_prodid", ob.toString());
		ob = baseDao.getFieldDataByCondition("barstocktaking inner join barstocktakingdetail on bs_id=bsd_bsid left join batch on ba_code=bsd_batchcode and ba_prodcode=bsd_prodcode and ba_whcode=bs_whcode", "nvl(to_char(ba_date,'yyyy-mm-dd'),to_char(bs_indate,'yyyy-mm-dd')) ba_date ", "bsd_id='"+store.get("bdd_bsdid").toString()+"'");		
		store.put("bdd_indate",  ob.toString());
		if(!store.get("bdd_madedate").equals("") && store.get("bdd_madedate") != null){
		   store.put("bdd_madedate", ob.toString());		
		}
		bqty = (int) (bdd_qty/pr_zxbzs);//条码数量是最小包装数的条数
		aqty = (new BigDecimal(Double.toString(bdd_qty))).subtract(new BigDecimal(Double.toString(bqty*pr_zxbzs))).doubleValue();//条码零头	
					
		Double pk_qty = Double.valueOf(store.get("bdd_pkqty").toString());//箱内总数			
		int  bqtyB = 0;				
		bqtyB = (int) (bdd_qty/pk_qty); 	//整数箱数
		double aqtyB = (new BigDecimal(Double.toString(bdd_qty))).subtract(new BigDecimal(Double.toString(bqtyB*pk_qty))).doubleValue();
		int sumJ =	(int) (pk_qty/Double.valueOf(store.get("pr_zxbzs").toString()));//每箱件数		
		
		if (bqty >= 1){
			for (int i = 0;i < bqty;i++){
				if(i%sumJ == 0){
					out_boxcode = verifyApplyDao.outboxMethod(store.get("bdd_prodid").toString(),"2");							
			        pa_id = baseDao.getSeqId("PACKAGE_SEQ");
					if(i >= bqty-sumJ && aqtyB>0 ){		//生成箱号							
				        baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_prodcode,pa_packdate,pa_packageqty,pa_totalqty,pa_status,pa_indate)values("+pa_id+",'"+out_boxcode+"','"+store.get("bi_prodcode")+"',sysdate,'"+(bqty-sumJ)+"','"+aqtyB+"','0',to_date('"+ob.toString()+"','yyyy-MM-dd'))");	
					}else{
				        baseDao.execute("insert into package (pa_id,pa_outboxcode,pa_prodcode,pa_packdate,pa_packageqty,pa_totalqty,pa_status,pa_indate)values("+pa_id+",'"+out_boxcode+"','"+store.get("bi_prodcode")+"',sysdate,'"+sumJ+"','"+pk_qty+"','0',to_date('"+ob.toString()+"','yyyy-MM-dd'))");	
					}
			   }
			if(ve_id != null && !ve_id.equals("")){
			   bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(),ve_id.toString(),0);
			}else {
			   bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(),"",0);
			}
			 insertBarPd(caller,pa_id,out_boxcode,pr_zxbzs,bar_code,store);
			}
		}
		if(aqty>0){
			bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(),ve_id.toString(),0);	
			insertBarPd(caller,pa_id,out_boxcode,aqty,bar_code,store);
		}	
	}
	
	private void insertBarPd(String caller, int pa_id, String out_boxcode,
			Double qty, String bar_code, Map<Object, Object> store) {
		List<String> sqls = new ArrayList<String>();
		int bdd_id = baseDao.getSeqId("BARSTOCKTAKINGDETAILDET_SEQ");				
		//生成BarStocktakingDetailDet		
		store.put("bdd_id",bdd_id);
		store.put("bdd_barcode", bar_code);
		store.put("bdd_inqty", qty);
		store.remove("bdd_qty");
		store.remove("pr_zxbzs");
		store.remove("bdd_pkqty");
		store.remove("bsd_inqty");
		store.remove("bsd_bsid");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BarStocktakingDetailDet", new String[]{}, new Object[]{});
		sqls.add(formSql);
		sqls.add("insert into packageDetail (pd_id,pd_paid,pd_outboxcode,pd_barcode,pd_innerqty) values ("+ baseDao.getSeqId("PACKAGEDETAIL_SEQ")+","+pa_id+",'"+out_boxcode+"','"+bar_code+"','"+qty+"')");
		sqls.add("update barstocktakingdetaildet set bdd_outboxcode='"+out_boxcode+"',bdd_outboxid="+pa_id+" where bdd_id="+bdd_id);
		baseDao.execute(sqls);
	}
	
	private void insertBar(String caller, Map<Object, Object> store,Double inqty) {
        String bar_code;
        Object ve_id = store.get("ve_id");
        if ((ve_id != null) && (ve_id != "")) {
            bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(),ve_id.toString(),0);
        } else {
            bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(), "",0);
        }
        int bdd_id = baseDao.getSeqId("BARSTOCKTAKINGDETAILDET_SEQ");
        //生成BARCODEIO		
        store.put("bdd_id", bdd_id);
        store.put("bdd_barcode", bar_code);
        store.remove("ve_id");
        store.put("bdd_inqty", inqty);
        String formSql = SqlUtil.getInsertSqlByFormStore(store, "BARSTOCKTAKINGDETAILDET",
                new String[] {  }, new Object[] {  });
        baseDao.execute(formSql);
    }
	
	 private void insertBarPd(String caller, int pa_id, String out_boxcode,
		        Double qty,Map<Object, Object> store) {
		        List<String> sqls = new ArrayList<String>();   
		        String bar_code;
		        Object ve_id = store.get("ve_id");
		        if ((ve_id != null) && (ve_id != "")) {
		            bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(),ve_id.toString(),0);
		        } else {
		            bar_code = verifyApplyDao.barcodeMethod(store.get("bdd_prodcode").toString(), "",0);
		        }
		        int bdd_id = baseDao.getSeqId("BARSTOCKTAKINGDETAILDET_SEQ");
		        //生成BARCODEIO		
		        store.put("bdd_id", bdd_id);
		        store.put("bdd_barcode", bar_code);
		        store.put("bdd_inqty", qty);
		        store.remove("ve_id");
		        String formSql = SqlUtil.getInsertSqlByFormStore(store, "BARSTOCKTAKINGDETAILDET",
		                new String[] {}, new Object[] {});
		        baseDao.execute(formSql);
		        sqls.add("update barstocktakingdetaildet set bdd_outboxcode='" + out_boxcode +
		            "',bdd_outboxid=" + pa_id + " where bdd_id=" + bdd_id);
		        baseDao.execute(sqls);
		    }
	 
	  private void checkNumber(Integer id){
		  SqlRowList rs = baseDao.queryForRowSet("select count(0) cn ,wmsys.wm_concat(bsd_detno) detno from "
			         +"   barstocktakingdetail left join batch on bsd_batchid = ba_id left join (select sum(bar_remain) bar_remain,bar_batchid from barcode where bar_status=1 "
			         +"   group by bar_batchid) A on A.bar_batchid= bsd_batchid where "
			         +"   bsd_bsid=? and nvl(bsd_batchcode,' ')<>' ' and nvl(bsd_inqty,0) > (nvl(ba_remain,0)-nvl(bar_remain,0)) and rownum<30",id);
			if(rs.next()){
				if(rs.getInt("cn") > 0){
				      BaseUtil.showError("明细行号:"+rs.getString("detno")+"应补数量+已生成条码库存数量不能超过批号库存数");
				  }
			}
			/*rs = baseDao.queryForRowSet("select count(0) cn ,wmsys.wm_concat(bsd_detno) detno from "
			         +"   barstocktakingdetail left join productWH on pw_prodcode=bsd_prodcode and pw_whcode = bsd_whcode left join (select sum(bar_remain) bar_remain,bar_prodcode,bar_whcode from barcode where bar_status=1 "
			         +"   group by bar_prodcode,bar_whcode) A on A.bar_prodcode= bsd_prodcode and A.bar_whcode = bsd_whcode where "
			         +"   bsd_bsid=? and nvl(bsd_batchcode,' ')=' ' and nvl(bsd_inqty,0) > (nvl(pw_onhand,0)-nvl(bar_remain,0)) and rownum<30",id);
			if(rs.next()){
				if(rs.getInt("cn") > 0){
				      BaseUtil.showError("明细行号:"+rs.getString("detno")+"应补数量+已生成条码库存数量不能超过物料库存数");
				  }
			}	*/
					
		}
}
