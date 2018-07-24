package com.uas.b2c.service.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.api.b2c_erp.baisc.model.ComponentInfoUas;
import com.uas.api.b2c_erp.seller.model.GoodsQtyPriceUas;
import com.uas.api.b2c_erp.seller.model.GoodsSimpleUas;
import com.uas.b2c.service.seller.B2CGoodsUpAndDownService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.support.ICallable;
import com.uas.erp.core.support.MergeTask;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.ProductBatchUUIdService;

/**
 * 获取b2c平台商品库存，超过一小时重新获取
 * 
 * @author XiaoST
 */
@Component
@EnableAsync
@EnableScheduling
public class GetGoodsReserveService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private B2CGoodsUpAndDownService b2CGoodsUpAndDownService;
	@Autowired
	private B2CComponentService b2CComponentService;
    @Autowired
    private ProductBatchUUIdService  productBatchUUIdService;
    
	/**
	 * @param uuids
	 */
	public void getGoodsOnhand(String uuids) {
		final Master master = SystemSession.getUser().getCurrentMaster();
		String[] uuidArray = BaseUtil.parseStr2Array(uuids, ",");
		// 每20一组拆分
		Object[] uuidGroup = CollectionUtil.split(uuidArray, 40);
		MergeTask<List<ComponentInfoUas>, String> task = new MergeTask<List<ComponentInfoUas>, String>(
				new ICallable<List<ComponentInfoUas>, String>() {
					@Override
					public List<ComponentInfoUas> call(String str) throws Exception {
						return getComponents(str, master);
					}

				});
		// 按分拆的uuid，添加为支线任务
		for (Object group : uuidGroup) {
			task.join(CollectionUtil.toString((Object[]) group));
		}
		List<ComponentInfoUas> datas = task.execute(ComponentInfoUas.class);
		if (!CollectionUtil.isEmpty(datas)) {
			List<String> sqls = new ArrayList<String>();
			for(ComponentInfoUas info : datas){
				SqlRowList rs = baseDao.queryForRowSet("select pr_unit,go_unit,pr_code,go_erpunit from product left join B2C$GoodsOnhand on go_prodcode=pr_code  where pr_uuid=?",info.getUuid());
				if(rs.next()){
					if(rs.getObject("go_unit") == null){//插入数据
						String erpunit = productBatchUUIdService.getUASUnit(info.getUnit(), rs.getString("pr_unit"));
						double rate =  productBatchUUIdService.getUnitRate(info.getUnit(),erpunit);
						sqls.add("insert into B2C$GoodsOnhand(go_id,go_uuid,go_minprice,go_onsaleqty,go_synctime,go_code,go_minbuyqty,go_maxprice,go_prodcode,go_erpunit,go_unit) "
								+ "values(B2C$GOODSONHAND_SEQ.nextval,'"+info.getUuid()+"',nvl("+info.getMinPrice()+",0)/"+rate+",nvl("+info.getReserve()+",0)*"+rate+",sysdate,'"+info.getCode()+"',nvl("+info.getMinBuyQty()+",0)*"+rate+",nvl("+info.getMaxPrice()+",0)/"+rate+",'"+rs.getString("pr_code")+"','"+erpunit+"','"+info.getUnit()+"')");
					}else {//更新数据
						double rate = productBatchUUIdService.getUnitRate(rs.getString("go_unit"),rs.getString("go_erpunit"));
						sqls.add("update B2C$GoodsOnhand set go_minprice=nvl("+info.getMinPrice()+",0)/"+rate+",go_onsaleqty=nvl("+info.getReserve()+",0)*"+rate+",go_synctime=sysdate,go_maxprice=nvl("+info.getMaxPrice()+",0)/"+rate+",go_minbuyqty=nvl("+info.getMinBuyQty()+",0)*"+rate +" where go_prodcode ='"+rs.getString("pr_code")+"'");
					}
				}
			}
			baseDao.execute(sqls);
		}
	}

	/**
	 * 远程获取元器件资料
	 * 
	 * @param uuids
	 * @return
	 */
	private List<ComponentInfoUas> getComponents(String uuids, Master master) {
		try {
			return b2CComponentService.getSimpleInfoByUuids(uuids, master);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取平台标准料号获取库存
	 * 
	 * @param uuids
	 */
	public void getGoodsBatch(String uuids) {
		final Master master = SystemSession.getUser().getCurrentMaster();
		String[] sa = BaseUtil.parseStr2Array(uuids, ",");
		// 每20一组拆分
		Object[] uuidGroup = CollectionUtil.split(sa, 40);
		// 聚合任务
		MergeTask<List<GoodsSimpleUas>, String> task = new MergeTask<List<GoodsSimpleUas>, String>(
				new ICallable<List<GoodsSimpleUas>, String>() {
					@Override
					public List<GoodsSimpleUas> call(String str) throws Exception {
						return getBatchGoods(str, master);
					}
				});
		// 按分拆的uuid，添加为支线任务
		for (Object group : uuidGroup) {
			task.join(CollectionUtil.toString((Object[]) group));
		}
		List<GoodsSimpleUas> goods = task.execute(GoodsSimpleUas.class);
		// 清空原有数据
		baseDao.deleteByCondition("B2C$GoodsBatch", "gb_uuid in (" + CollectionUtil.toSqlString(BaseUtil.parseStr2Array(uuids, ",")) + ")");
		if (!CollectionUtil.isEmpty(goods)) {
			List<String> sqls = new ArrayList<String>();
			for(GoodsSimpleUas good : goods){
				SqlRowList rs = baseDao.queryForRowSet("select go_erpunit,go_unit from B2C$GOODSONHAND WHERE go_uuid='"+good.getUuid()+"'");
				if(rs.next()){
					double rate = productBatchUUIdService.getUnitRate(rs.getString("go_unit"),rs.getString("go_erpunit"));
					String qtyprice = "";
					if(rate != 1){
						List<GoodsQtyPriceUas> price = FlexJsonUtil.fromJsonArray(good.getQtyPrice(),GoodsQtyPriceUas.class);
						for(GoodsQtyPriceUas pu:price){
							pu.setStart(pu.getStart()*rate);
							pu.setEnd(pu.getEnd()*rate);
							if(pu.getRMBNTPrice() != null){
							  pu.setRMBNTPrice(pu.getRMBNTPrice()/rate);
							  pu.setRMBPrice(pu.getRMBPrice()/rate);
							}
							if(pu.getUSDNTPrice()!= null){
								pu.setUSDNTPrice(pu.getUSDNTPrice()/rate);
								pu.setUSDPrice(pu.getUSDPrice()/rate);
							}
						}
						qtyprice = FlexJsonUtil.toJsonArrayDeep(price);
					}else{
						qtyprice = good.getQtyPrice();
					}
					sqls.add("insert into B2C$GoodsBatch(gb_id,gb_b2bbatchcode,gb_minpackqty,gb_minbuyqty,gb_price,gb_usdprice,"
							+ "gb_madedate,gb_onsaleqty,gb_remark,gb_deliveryTime,gb_uuid,gb_currency,gb_hkdeliveryTime)"
							+ "values(B2C$GOODSBATCH_SEQ.nextval,'"+good.getBatchCode()+"',"+good.getMinPackQty()*rate+","+good.getMinBuyQty()*rate+",'"+qtyprice+"','"+qtyprice+"',"
							+ DateUtil.parseDateToOracleString(null, good.getCreatedDate())+","+good.getReserve()*rate+",'"+good.getRemark()+"',"+good.getDeliveryTime()+",'"+good.getUuid()+"','"+good.getCurrencyName()+"',"+good.getDeliveryHKTime()+")");
				}
			}
			baseDao.execute(sqls);
		}
	}

	/**
	 * 远程获标准料号库存
	 * 
	 * @param uuids
	 * @return
	 */
	private List<GoodsSimpleUas> getBatchGoods(String str, Master master) {
		try {
			List<GoodsSimpleUas> goodsSimpleUas = b2CGoodsUpAndDownService.getListByUuids(str, master);
			return goodsSimpleUas;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
