package com.uas.b2b.service.common;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.platform.warehouse.core.util.JacksonUtils;
import com.uas.platform.warehouse.tools.client.Warehouse;
import com.uas.platform.warehouse.tools.domain.Invoice;
import com.uas.platform.warehouse.tools.domain.InvoiceDetail;
import com.uas.platform.warehouse.tools.domain.InvoiceReply;
import com.uas.platform.warehouse.tools.domain.InvoiceReplyDetail;
import com.uas.platform.warehouse.tools.domain.Packing;
import com.uas.platform.warehouse.tools.domain.PackingDetail;
import com.uas.platform.warehouse.tools.domain.PackingReply;
import com.uas.platform.warehouse.tools.domain.PackingReplyDetail;
import com.uas.platform.warehouse.tools.domain.Product;
import com.uas.platform.warehouse.tools.domain.Shipment;
import com.uas.platform.warehouse.tools.domain.ShipmentDetail;
import com.uas.platform.warehouse.tools.domain.Stockin;
import com.uas.platform.warehouse.tools.domain.StockinDetail;
import com.uas.platform.warehouse.tools.domain.Transfer;
import com.uas.platform.warehouse.tools.domain.TransferDetail;

@Component
@EnableAsync
@EnableScheduling
public class EdiUploadTask  extends AbstractTask {
	static final String ENDPOINT = "218.17.158.219:9443/warehouse/";
	//static final String ENDPOINT = "localhost:20320/";
	
    /**
     * App key of enterprise.
     */
    static final String EN_APP_KEY = "178698387fa34c9c969302641f773398";

    /**
     * Secret key of enterprise.
     */
    static final String EN_SECRET_KEY = "E707D934C24D74CCC5180E64B76176F9DF985855";
	
	
    private static Warehouse client = new Warehouse(ENDPOINT,
		      EN_APP_KEY, EN_SECRET_KEY);
	
	@Async
	public void execute() {
		super.execute();
	}
	
	@Override
	protected void onExecute(Master master) {
		try {
			/**
			 * 上传任务
			 */
			SqlRowList rs = baseDao.queryForRowSet("select wm_concat(keyvalue_) keys,wm_concat(id_) ids,type_ from edi_datalog where status_=0 and nvl(type_,' ')<>' ' and nvl(keyvalue_,0)<>0 group by type_ order by type_ asc");
			String type,keys;
			boolean uploadSuccess;
			while(rs.next()){
				type = rs.getString("type_");
				keys = rs.getString("keys");
				uploadSuccess = true;
				
				try{
					if(type.equals("pull")){ //上传出货单 
						uploadSuccess = uploadProdout(keys);
					}else if(type.equals("asn")){ //上传入库单
						uploadSuccess = uploadProdin(keys);
					}else if(type.equals("tran")){ //上传调拨单
						uploadSuccess = uploadProdtran(keys);
					}else if(type.equals("item")){ //上传物料资料
						uploadSuccess = uploadProduct(keys);
					}else if(type.equals("packing")){
						uploadSuccess = uploadPacking(keys);
					}else if(type.equals("invoice")){
						uploadSuccess = uploadInvoice(keys);
					}
				}catch(Exception e){
					uploadSuccess = false;
					baseDao.execute("insert into edi_error(data,message) values('"+keys+"','"+e.getMessage()+"')");
					e.printStackTrace();
				}
				if(uploadSuccess){
					//更新edi_datalog状态
					baseDao.execute("update edi_datalog set status_=1 where id_ in ("+rs.getString("ids")+")");					
				}
			}
		} catch (Exception e) {
			baseDao.execute("insert into edi_error(data,message) values('','"+e.getMessage()+"')");
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传invoice
	 * @param ids
	 */
	public boolean uploadInvoice(String ids){
		List<Invoice> invoices = baseDao.query("select * from edi_invoice where status_=0 and sourceid in ("+ids+")", Invoice.class);
		List<InvoiceDetail> details = baseDao.query("select * from edi_invoice_detail where status_=0 and mainsourceid in ("+ids+")", InvoiceDetail.class);
		List<InvoiceDetail> invoiceDetails = null;
		boolean success = false;
		for(Invoice invoice:invoices){
			invoiceDetails = new ArrayList<InvoiceDetail>();
			for(InvoiceDetail detail:details){
				if(detail.getMainid()==invoice.getId()){
					invoiceDetails.add(detail);
				}
			}
			invoice.setDetails(invoiceDetails);
		}
		if(invoices.size()>0){
			logger.info(this.getClass() + " uploadInvoice start");
			logger.info(this.getClass() + " datas: " + JacksonUtils.toJson(invoices));
			success = send(invoices);
			if(success){
				updateInvoiceEdiStatus(ids);
			}
			logger.info(this.getClass() + " uploadInvoice end");
		}
		return success;				
	}
	
	/**
	 * 上传packing
	 * @param ids
	 */
	public boolean uploadPacking(String ids){
		List<Packing> packings = baseDao.query("select * from edi_packing where status_=0 and sourceid in ("+ids+")", Packing.class);
		List<PackingDetail> details = baseDao.query("select * from edi_packing_detail where status_=0 and mainsourceid in ("+ids+")", PackingDetail.class);
		List<PackingDetail> packingDetails = null;
		boolean success = false;
		for(Packing packing:packings){
			packingDetails = new ArrayList<PackingDetail>();
			for(PackingDetail detail:details){
				if(detail.getMainid()==packing.getId()){
					packingDetails.add(detail);
				}
			}
			packing.setDetails(packingDetails);
		}
		if(packings.size()>0){
			logger.info(this.getClass() + " uploadPacking start");
			logger.info(this.getClass() + " datas: " + JacksonUtils.toJson(packings));
			success = send(packings);
			if(success){
				updatePackingEdiStatus(ids);
			}
			logger.info(this.getClass() + " uploadPacking end");
		}
		return success;				
	}
	
	/**
	 * 上传物料资料
	 * @param ids
	 */
	public boolean uploadProduct(String ids){
		List<Product> products = baseDao.query("select * from edi_product where status_=0 and sourceid in ("+ids+")", Product.class);
		boolean success = false;
		if(products.size()>0){
			logger.info(this.getClass() + " uploadProduct start");
			logger.info(this.getClass() + " datas: " + JacksonUtils.toJson(products));
			success = send(products);
			logger.info(this.getClass() + " uploadProduct end");
		}
		return success;
	}
	
	/**
	 * 上传调拨单
	 */
	public boolean uploadProdtran(String ids){
		List<Transfer> transfers = baseDao.query("select * from edi_prodtran where status_=0 and sourceid in ("+ids+")", Transfer.class);
		List<TransferDetail> details = baseDao.query("select * from edi_prodtran_detail where status_=0 and mainsourceid in ("+ids+")", TransferDetail.class);
		List<TransferDetail> transferDetails = null;
		boolean success = false;
		for(Transfer transfer:transfers){
			transferDetails = new ArrayList<TransferDetail>();
			for(TransferDetail detail:details){
				if(detail.getMainid()==transfer.getId()){
					transferDetails.add(detail);
				}
			}
			transfer.setDetails(transferDetails);
		}
		if(transfers.size()>0){
			logger.info(this.getClass() + " uploadProdtran start");
			logger.info(this.getClass() + " datas: " + JacksonUtils.toJson(transfers));
			success = send(transfers);
			if(success){
				updateProdtranEdiStatus(ids);
			}
			logger.info(this.getClass() + " uploadProdtran end");
		}
		return success;
	}
	
	/**
	 * 上传入库单
	 */
	public boolean uploadProdin(String ids){
		List<Stockin> stockins = baseDao.query("select * from edi_prodin where status_=0 and sourceid in ("+ids+")", Stockin.class);
		List<StockinDetail> details = baseDao.query("select * from edi_prodin_detail where status_=0 and mainsourceid in ("+ids+")", StockinDetail.class);
		List<StockinDetail> stockinDetails = null;
		boolean success = false;
		for(Stockin stockin:stockins){
			stockinDetails = new ArrayList<StockinDetail>();
			for(StockinDetail detail:details){
				if(detail.getMainid()==stockin.getId()){
					stockinDetails.add(detail);
				}
			}
			stockin.setDetails(stockinDetails);
		}
		if(stockins.size()>0){
			logger.info(this.getClass() + " uploadProdint start");
			logger.info(this.getClass() + " datas: " + JacksonUtils.toJson(stockins));
			success = send(stockins);
			if(success){
				updateProdinEdiStatus(ids);
			}
			logger.info(this.getClass() + " uploadProdint end");
		}
		return success;
	}
	
	/**
	 * 上传出货单
	 */
	public boolean uploadProdout(String ids){
		List<Shipment> shipments = baseDao.query("select * from edi_prodout where status_=0 and sourceid in ("+ids+")", Shipment.class);
		List<ShipmentDetail> details = baseDao.query("select * from edi_prodout_detail where status_=0 and mainsourceid in ("+ids+")", ShipmentDetail.class);
		List<ShipmentDetail> shipDetails = null;
		boolean success = false;
		for(Shipment shipment:shipments){
			shipDetails = new ArrayList<ShipmentDetail>();
			for(ShipmentDetail detail:details){
				if(detail.getMainid()==shipment.getId()){
					shipDetails.add(detail);
				}
			}
			shipment.setDetails(shipDetails);
		}
		if(shipments.size()>0){
			logger.info(this.getClass() + " uploadProdout start");
			logger.info(this.getClass() + " datas: " + JacksonUtils.toJson(shipments));
			//TODO 处理异常
			success = send(shipments);
			if(success){
				updateProdoutEdiStatus(ids);
			}
			logger.info(this.getClass() + " uploadProdout end");
		}
		return success;
	}
	
	//更新出库单状态
	public void updateProdoutEdiStatus(String ids){
		//TODO 处理异常
		baseDao.execute("update edi_prodout set status_=1 where status_=0 and sourceid in ("+ids+")");
		baseDao.execute("update edi_prodout_detail set status_=1 where status_=0 and mainsourceid in ("+ids+")");
	}
	
	//更新入库单状态
	public void updateProdinEdiStatus(String ids){
		//TODO 处理异常
		baseDao.execute("update edi_prodin set status_=1 where status_=0 and sourceid in ("+ids+")");
		baseDao.execute("update edi_prodin_detail set status_=1 where status_=0 and mainsourceid in ("+ids+")");
	}
	
	//更新调拨单状态
	public void updateProdtranEdiStatus(String ids){
		//TODO 处理异常
		baseDao.execute("update edi_prodtran set status_=1 where status_=0 and sourceid in ("+ids+")");
		baseDao.execute("update edi_prodtran_detail set status_=1 where status_=0 and mainsourceid in ("+ids+")");
	}
	
	//更新packing状态
	public void updatePackingEdiStatus(String ids){
		//TODO 处理异常
		baseDao.execute("update edi_packing set status_=1 where status_=0 and sourceid in ("+ids+")");
		baseDao.execute("update edi_packing_detail set status_=1 where status_=0 and mainsourceid in ("+ids+")");
	}
	
	//更新invoice状态
	public void updateInvoiceEdiStatus(String ids){
		//TODO 处理异常
		baseDao.execute("update edi_invoice set status_=1 where status_=0 and sourceid in ("+ids+")");
		baseDao.execute("update edi_invoice_detail set status_=1 where status_=0 and mainsourceid in ("+ids+")");
	}
	
	public <T> boolean send(List<T> datas){
	    boolean success = false;
		try {
			success = client.sendToB2b(datas);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return success;
	}
}
