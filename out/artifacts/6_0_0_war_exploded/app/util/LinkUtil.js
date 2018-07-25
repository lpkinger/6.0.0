/**
 * 
 */

Ext.define('erp.util.LinkUtil',{
	url:new Object(),
	cfg:new Object(),
	getLinkByKind:function(kind) {
		if(this.cfg[kind]){
			return this.cfg[kind];
		}
		var table = 'RecBalance', keyfield = 'rb_id', mainfield = 'rbd_rbid', 
			codefield = 'rb_code', kindfield = 'rb_kind', url;
		
		switch (kind) {
		case '其它应收单':
			table = 'ArBill';
			keyfield = 'ab_id';
			mainfield = 'abd_abid';
			codefield = 'ab_code';
			kindfield = 'ab_class';
			url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!OTRS';
			break;
		case '应收发票':
			table = 'ArBill';
			keyfield = 'ab_id';
			mainfield = 'abd_abid';
			codefield = 'ab_code';
			kindfield = 'ab_class';
			url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!IRMA';
			break;
		case '应收冲应付':
			url = 'jsps/fa/ars/recBalanceAP.jsp?whoami=RecBalance!RRCW';
			break;
		case '收款单':
			url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!PBIL';
			break;
		case '冲应收款':
			url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!IMRE';
			break;
		case '预收冲应收':
			url = 'jsps/fa/ars/recBalancePRDetail.jsp?whoami=RecBalance!PTAR';
			break;
		case '应收款转销':
			url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!ARRM';
			break;
		case '应收款转销(转入方)':
			kind = '应收款转销';
			url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!ARRM';
			break;
		case '应收款转销(转出方)':
			kind = '应收款转销';
			url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!ARRM';
			break;
		case '应收退款单':
			url = 'jsps/fa/ars/recBalanceTK.jsp?whoami=RecBalance!TK';
			break;
		case '开票记录':
			table = 'BillOut';
			keyfield = 'bi_id';
			mainfield = 'ard_biid';
			codefield = 'bi_code';
			kindfield = null;
			url = 'jsps/fa/ars/billOut.jsp';
			break;
		case '应收开票记录':
			table = 'BillOut';
			keyfield = 'bi_id';
			mainfield = 'ard_biid';
			codefield = 'bi_code';
			kindfield = null;
			url = 'jsps/fa/ars/billOut.jsp';
			break;
		case '预收款':
			table = 'PreRec';
			keyfield = 'pr_id';
			mainfield = 'prd_prid';
			codefield = 'pr_code';
			kindfield = 'pr_kind';
			url = 'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DERE';
			break;
		case '预收退款':
			table = 'PreRec';
			keyfield = 'pr_id';
			mainfield = 'prd_prid';
			codefield = 'pr_code';
			kindfield = 'pr_kind';
			url = 'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR';
			break;
		case '预收退款单':
			table = 'PreRec';
			keyfield = 'pr_id';
			mainfield = 'prd_prid';
			codefield = 'pr_code';
			kindfield = 'pr_kind';
			url = 'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR';
			break;
		case '发出商品':
			table = 'GoodsSend';
			keyfield = 'gs_id';
			mainfield = 'gsd_gsid';
			codefield = 'gs_code';
			kindfield = 'gs_class';
			url = 'jsps/fa/ars/goodsSend.jsp?whoami=GoodsSendGs';
			break;
		case '凭证':
			table = 'Voucher';
			keyfield = 'vo_id';
			mainfield = 'vd_void';
			codefield = 'vo_number';
			kindfield = null;
			url = 'jsps/fa/ars/voucher.jsp';
			break;
		case '凭证流水':
			table = 'Voucher';
			keyfield = 'vo_id';
			mainfield = 'vd_void';
			codefield = 'vo_code';
			kindfield = null;
			url = 'jsps/fa/ars/voucher.jsp';
			break;
		case 'AccountRegister':
			table = 'AccountRegister';
			keyfield = 'ar_id';
			mainfield = 'ard_arid';
			codefield = 'ar_code';
			kindfield = null;
			url = 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank';
			break;
		case 'AccountRegiste':
			table = 'AccountRegister';
			keyfield = 'ar_id';
			mainfield = 'ard_arid';
			codefield = 'ar_code';
			kindfield = null;
			url = 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank';
			break;
		case '银行登记':
			table = 'AccountRegister';
			keyfield = 'ar_id';
			mainfield = 'ard_arid';
			codefield = 'ar_code';
			kindfield = null;
			url = 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank';
			break;
		case '转存(转出方)':
			table = 'AccountRegister';
			keyfield = 'ar_id';
			mainfield = 'ard_arid';
			codefield = 'ar_code';
			kindfield = null;
			url = 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank';
			break;
		case '应收票据':
			table = 'BillAR';
			keyfield = 'bar_id';
			mainfield = null;
			codefield = 'bar_code';
			kindfield = null;
			url = 'jsps/fa/gs/billAR.jsp';
			break;
		case '应收票据:其他收款':
			table = 'BillAR';
			keyfield = 'bar_id';
			mainfield = null;
			codefield = 'bar_code';
			kindfield = null;
			url = 'jsps/fa/gs/billAR.jsp';
			break;
		case 'BillAR':
			table = 'BillAR';
			keyfield = 'bar_id';
			mainfield = null;
			codefield = 'bar_code';
			kindfield = null;
			url = 'jsps/fa/gs/billAR.jsp';
			break;
		case '应收票据异动':
			table = 'BillARChange';
			keyfield = 'brc_id';
			mainfield = 'brd_brcid';
			codefield = 'brc_code';
			kindfield = null;
			url = 'jsps/fa/gs/billARChange.jsp';
			break;
		case '应收票据异动:其他收款':
			table = 'BillARChange';
			keyfield = 'brc_id';
			mainfield = 'brd_brcid';
			codefield = 'brc_code';
			kindfield = null;
			url = 'jsps/fa/gs/billARChange.jsp';
			break;
		case 'BillARChange':
			table = 'BillARChange';
			keyfield = 'brc_id';
			mainfield = 'brd_brcid';
			codefield = 'brc_code';
			kindfield = null;
			url = 'jsps/fa/gs/billARChange.jsp';
			break;
		case '期初调整单':
			table = 'ProductWHMonthAdjust';
			keyfield = 'pwa_id';
			mainfield = 'pwd_pwaid';
			codefield = 'pwa_code';
			kindfield = null;
			url = 'jsps/co/inventory/productWHMonthAdjust.jsp';
			break;
		case '期末调整单':
			table = 'ProductWHMonthAdjust';
			keyfield = 'pwa_id';
			mainfield = 'pwd_pwaid';
			codefield = 'pwa_code';
			kindfield = null;
			url = 'jsps/co/inventory/productWHMonthAdjust.jsp';
			break;
		}
		if(url){
			cfg = {
				kind: kind,
				table: table,
				keyfield: keyfield,
				mainfield: mainfield,
				codefield: codefield,
				kindfield: kindfield,
				url: url
			}
			this.cfg[kind] = cfg;
		}else{
			cfg = this.getLinkByKind1(kind);
		}
		return cfg;
	},
	getLinkByKind1:function(kind) {
		if(this.cfg[kind]){
			return this.cfg[kind];
		}
		
		var table = 'ProdInOut', keyfield = 'pi_id', mainfield = 'pd_piid', 
		codefield = 'pi_inoutno', kindfield = 'pi_class', url='jsps/scm/reserve/prodInOut.jsp?whoami=',caller;
			
		switch (kind) {
			case  '采购验收单':
				caller = 'ProdInOut!PurcCheckin';
				break; 
			case  '采购验退单':
				caller = 'ProdInOut!PurcCheckout';
				break;
			case  '其它采购入库单':
				caller = 'ProdInOut!OtherPurcIn';
				break; 
			case  '完工入库单':
				caller = 'ProdInOut!Make!In';
				break;
			case  '其它采购出库单':
				caller = 'ProdInOut!OtherPurcOut';
				break; 
			case  '换货出库单':
				caller = 'ProdInOut!ExchangeOut';
				break; 
			case  '换货入库单':
				caller = 'ProdInOut!ExchangeIn';
				break; 
			case  '出货单':
				caller = 'ProdInOut!Sale';
				break; 
			case  '委外领料单':
				caller = 'ProdInOut!OutsidePicking';
				break; 
			case  '研发退料单':
				caller = 'ProdInOut!YFIN';
				break; 
			case  '研发领料单':
				caller = 'ProdInOut!YFOUT';
				break; 
			case  '辅料入库单':
				caller = 'ProdInOut!FLIN';
				break;
			case  '辅料出库单':
				caller = 'ProdInOut!FLOUT';
				break; 
			case  '借货出货单':
				caller = 'ProdInOut!SaleBorrow';
				break; 
			case  '借货归还单':
				caller = 'ProdInOut!OutReturn';
				break; 
			case  '委外补料单':
				caller = 'ProdInOut!OSMake!Give';
				break; 
			case  '不良品入库单':
				caller = 'ProdInOut!DefectIn';
				break; 
			case  '不良品出库单':
				caller = 'ProdInOut!DefectOut';
				break; 
			case  '库存初始化':
				caller = 'ProdInOut!ReserveInitialize';
				break; 
			case  '报废单':
				caller = 'ProdInOut!StockScrap';
				break; 
			case  '盘亏调整单':
				caller = 'ProdInOut!StockLoss';
				break; 
			case  '盘盈调整单':
				caller = 'ProdInOut!StockProfit';
				break; 
			case  '拆件入库单':
				caller = 'ProdInOut!PartitionStockIn';
				break; 
			case  '其它入库单':
				caller = 'ProdInOut!OtherIn';
				break; 
			case  '生产领料单':
				caller = 'ProdInOut!Picking';
				break; 
			case  '生产退料单':
				caller = 'ProdInOut!Make!Return';
				break; 
			case  '销售退货单':
				caller = 'ProdInOut!SaleReturn';
				break; 
			case  '委外验收单':
				caller = 'ProdInOut!OutsideCheckIn';
				break; 
			case  '委外验退单':
				caller = 'ProdInOut!OutesideCheckReturn';
				break; 
			case  '委外退料单':
				caller = 'ProdInOut!OutsideReturn';
				break; 
			case  '拨出单':
				caller = 'ProdInOut!AppropriationOut';
				break; 
			case  '拨入单':
				caller = 'ProdInOut!AppropriationIn';
				break; 
			case  '销售拨出单':
				caller = 'ProdInOut!SaleAppropriationOut';
				break; 
			case  '销售拨入单':
				caller = 'ProdInOut!SaleAppropriationIn';
				break; 
			case  '其它出库单':
				caller = 'ProdInOut!OtherOut';
				break; 
			case  '生产补料单':
				caller = 'ProdInOut!Make!Give';
				break; 
			case  '用品领用单':
				caller = 'ProdInOut!GoodsPicking';
				break; 
			case  '用品验收单':
				caller = 'ProdInOut!GoodsIn';
				break; 
			case  '用品验退单':
				caller = 'ProdInOut!GoodsOut';
				break; 
			case  '用品退仓单':
				caller = 'ProdInOut!GoodsShutout';
				break; 
			case  '用品借用单':
				caller = 'ProdInOut!GoodsLend';
				break; 
			case  '用品归还单':
				caller = 'ProdInOut!GoodsReturn';
				break; 
			case  '成本调整单':
				caller = 'ProdInOut!CostChange';
				break; 
			}
		if(caller){
			cfg = {
				kind: kind,
				table: table,
				keyfield: keyfield,
				mainfield: mainfield,
				codefield: codefield,
				kindfield: kindfield,
				url: url+caller
			}
			this.cfg[kind] = cfg;
			
		}else{
			cfg = this.getLinkByKind2(kind);
		}
		return cfg;
	},
	getLinkByKind2:function(kind) {
		if(this.cfg[kind]){
			return this.cfg[kind];
		}
		
		var table = 'PayBalance', keyfield = 'pb_id', mainfield = 'pbd_pbid', 
		codefield = 'pb_code', kindfield = 'pb_kind', url;
		switch (kind) {
			case '其它应付单':
				table = 'ApBill';
				keyfield = 'ab_id';
				mainfield = 'abd_abid';
				codefield = 'ab_code';
				kindfield = 'ab_class';
				url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!OTDW';
				break;
			case '应付发票':
				table = 'ApBill';
				keyfield = 'ab_id';
				mainfield = 'abd_abid';
				codefield = 'ab_code';
				kindfield = 'ab_class';
				url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM';
				break;
			case '模具发票':
				table = 'ApBill';
				keyfield = 'ab_id';
				mainfield = 'abd_abid';
				codefield = 'ab_code';
				kindfield = 'ab_class';
				url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM';
				break;
			case '冲应付款':
				url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!CAID';
				break;
			case '应付款转销':
				url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!APRM';
				break;
			case '应付款转销(转入方)':
				kind = '应付款转销';
				url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!APRM';
				break;
			case '应付款转销(转出方)':
				kind = '应付款转销';
				url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!APRM';
				break;
			case '预付冲应付':
				url = 'jsps/fa/arp/payBalancePRDetail.jsp?whoami=PayBalance!Arp!PADW';
				break;
			case '付款单':
				url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance';
				break;
			case '应付冲应收':
				url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!DWRC';
				break;
			case '应付退款单':
				url = 'jsps/fa/arp/paybalanceTK.jsp?whoami=PayBalance!TK';
				break;
			case '预付款':
				table = 'PrePay';
				keyfield = 'pp_id';
				mainfield = 'ppd_ppid';
				codefield = 'pp_code';
				kindfield = 'pp_type';
				url = 'jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAMT';
				break;
			case '预付退款':
				table = 'PrePay';
				keyfield = 'pp_id';
				mainfield = 'ppd_ppid';
				codefield = 'pp_code';
				kindfield = 'pp_type';
				url = 'jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAPR';
				break;
			case '预付退款单':
				table = 'PrePay';
				keyfield = 'pp_id';
				mainfield = 'ppd_ppid';
				codefield = 'pp_code';
				kindfield = 'pp_type';
				url = 'jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAPR';
				break;
			case '应付暂估':
				table = 'Estimate';
				keyfield = 'es_id';
				mainfield = 'esd_esid';
				codefield = 'es_code';
				kindfield = 'es_class';
				url = 'jsps/fa/arp/estimate.jsp';
				break;
			case '应付开票记录':
				table = 'BillOutAP';
				keyfield = 'bi_id';
				mainfield = 'ard_biid';
				codefield = 'bi_code';
				kindfield = null;
				url = 'jsps/fa/arp/billOutAP.jsp';
				break;
			case '应付票据':
				table = 'BillAP';
				keyfield = 'bap_id';
				mainfield = null;
				codefield = 'bap_code';
				kindfield = null;
				url = 'jsps/fa/gs/billAP.jsp';
				break;
			case 'BillAP':
				table = 'BillAP';
				keyfield = 'bap_id';
				mainfield = null;
				codefield = 'bap_code';
				kindfield = null;
				url = 'jsps/fa/gs/billAP.jsp';
				break;
			case '应付票据异动':
				table = 'BillAPChange';
				keyfield = 'bpc_id';
				mainfield = 'bpd_bpcid';
				codefield = 'bpc_code';
				kindfield = null;
				url = 'jsps/fa/gs/billAPChange.jsp';
				break;
			case 'BillAPChange':
				table = 'BillAPChange';
				keyfield = 'bpc_id';
				mainfield = 'bpd_bpcid';
				codefield = 'bpc_code';
				kindfield = null;
				url = 'jsps/fa/gs/billAPChange.jsp';
				break;
			case '摊销':
				table = 'PrePaid';
				keyfield = 'pp_id';
				mainfield = 'pd_ppid';
				codefield = 'pp_code';
				kindField = null;
				kindfield = 'pp_class';
				url = 'jsps/fa/gla/prePaid.jsp';
				break;
			case '固定资产卡片':
				table = 'ASSETSCARD';
				keyfield = 'ac_id';
				mainfield = null;
				codefield = 'ac_code';
				kindfield = null;
				url = 'jsps/fa/fix/assetsCard.jsp';
				break;
			case '卡片':
				table = 'ASSETSCARD';
				keyfield = 'ac_id';
				mainfield = null;
				codefield = 'ac_code';
				kindfield = null;
				url = 'jsps/fa/fix/assetsCard.jsp';
				break;
			case '折旧单':
				table = 'AssetsDepreciation';
				keyfield = 'de_id';
				mainfield = 'dd_deid';
				codefield = 'de_code';
				kindfield = 'de_class';
				url = 'jsps/fa/fix/assetsDepreciation.jsp?whoami=AssetsDepreciation';
				break;
			case '卡片变更单':
				table = 'AssetsCardChange';
				keyfield = 'acc_id';
				mainfield = null;
				codefield = 'acc_code';
				kindfield = null;
				url = 'jsps/fa/fix/assetsCardChange.jsp';
				break;
			case '资产增加单':
				table = 'AssetsDepreciation';
				keyfield = 'de_id';
				mainfield = 'dd_deid';
				codefield = 'de_code';
				kindfield = 'de_class';
				url = 'jsps/fa/fix/assetsDepreciation.jsp?whoami=AssetsDepreciation!Add';
				break;
			case '资产减少单':
				table = 'AssetsDepreciation';
				keyfield = 'de_id';
				mainfield = 'dd_deid';
				codefield = 'de_code';
				kindfield = 'de_class';
				url = 'jsps/fa/fix/assetsDepreciation.jsp?whoami=AssetsDepreciation!Reduce';
				break;
			case '生产报废单':
				table = 'MakeScrap';
				keyfield = 'ms_id';
				mainfield = 'md_msid';
				codefield = 'ms_code';
				kindfield = 'ms_class';
				url = 'jsps/pm/make/makeScrap.jsp';
				break;
			case '委外报废单':
				table = 'MakeScrap';
				keyfield = 'ms_id';
				mainfield = 'md_msid';
				codefield = 'ms_code';
				kindfield = 'ms_class';
				url = 'jsps/pm/make/makeScrapmake.jsp';
				break;
			case '物料':
				table = 'Product';
				keyfield = 'pr_id';
				mainfield = null;
				codefield = 'pr_code';
				kindfield = null;
				url = 'jsps/scm/product/product.jsp';
				break;
			case '仓库':
				table = 'Warehouse';
				keyfield = 'wh_id';
				mainfield = null;
				codefield = 'wh_code';
				kindfield = null;
				url = 'jsps/scm/reserve/warehouse.jsp';
				break;
			case '科目':
				table = 'Category';
				keyfield = 'ca_id';
				mainfield = null;
				codefield = 'ca_code';
				kindfield = null;
				url = 'jsps/fa/ars/category.jsp?whoami=Category!Base';
				break;
			case 'OS':
				table = 'make';
				keyfield = 'ma_id';
				mainfield = 'mm_maid';
				codefield = 'ma_code';
				kindfield = null;
				url = 'jsps/pm/make/makeBase.jsp?whoami=Make';
				break;
			case '委外加工单':
				table = 'make';
				keyfield = 'ma_id';
				mainfield = 'mm_maid';
				codefield = 'ma_code';
				kindfield = null;
				url = 'jsps/pm/make/makeBase.jsp?whoami=Make';
				break;
			case '委外待检':
				table = 'make';
				keyfield = 'ma_id';
				mainfield = 'mm_maid';
				codefield = 'ma_code';
				kindfield = null;
				url = 'jsps/pm/make/makeBase.jsp?whoami=Make';
				break;
			case 'MAKE':
				table = 'make';
				keyfield = 'ma_id';
				mainfield = 'mm_maid';
				codefield = 'ma_code';
				kindfield = null;
				url = 'jsps/pm/make/makeBase.jsp?whoami=Make!Base';
				break;
			case '制造单':
				table = 'make';
				keyfield = 'ma_id';
				mainfield = 'mm_maid';
				codefield = 'ma_code';
				kindfield = null;
				url = 'jsps/pm/make/makeBase.jsp?whoami=Make!Base';
				break;
			case '采购单':
				table = 'purchase';
				keyfield = 'pu_id';
				mainfield = 'pd_puid';
				codefield = 'pu_code';
				kindfield = null;
				url = 'jsps/scm/purchase/purchase.jsp';
				break;
			case '采购待检':
				table = 'purchase';
				keyfield = 'pu_id';
				mainfield = 'pd_puid';
				codefield = 'pu_code';
				kindfield = null;
				url = 'jsps/scm/purchase/purchase.jsp';
				break;
			case '请购单':
				table = 'Application';
				keyfield = 'ap_id';
				mainfield = 'ad_apid';
				codefield = 'ap_code';
				kindfield = null;
				url = 'jsps/scm/purchase/application.jsp';
				break;
			case '批记录':
				table = 'batch';
				keyfield = 'ba_id';
				mainfield = null;
				codefield = 'ba_code';
				kindfield = null;
				url = 'jsps/common/query.jsp?whoami=ProdIO!Quantity!Query';
				break;
			case '制造用料':
				table = 'make';
				keyfield = 'ma_id';
				mainfield = 'mm_maid';
				codefield = 'ma_code';
				kindfield = null;
				url = 'jsps/pm/make/makeBase.jsp?whoami=Make!Base';
				break;
			case '销售订单':
				table = 'Sale';
				keyfield = 'sa_id';
				mainfield = 'sd_said';
				codefield = 'sa_code';
				kindfield = null;
				url = 'jsps/scm/sale/sale.jsp?whoami=Sale';
				break;
			case '销售预测':
				table = 'SaleForecast';
				keyfield = 'sf_id';
				mainfield = 'sd_sfid';
				codefield = 'sf_code';
				kindfield = null;
				url = 'jsps/scm/sale/saleForecast.jsp';
				break;
			case '采购收料单':
				table = 'VerifyApply';
				keyfield = 'va_id';
				mainfield = 'vad_vaid';
				codefield = 'va_code';
				kindfield = 'va_class';
				url = 'jsps/scm/purchase/verifyApply.jsp?whoami=VerifyApply';
				break;
			case '委外收料单':
				table = 'VerifyApply';
				keyfield = 'va_id';
				mainfield = 'vad_vaid';
				codefield = 'va_code';
				kindfield = 'va_class';
				url = 'jsps/scm/purchase/verifyApply.jsp?whoami=VerifyApply!OS';
				break;
			case '采购检验单':
				table = 'QUA_VerifyApplyDetail';
				keyfield = 've_id';
				mainfield = 'ved_veid';
				codefield = 've_code';
				kindfield = 've_class';
				url = 'jsps/scm/qc/verifyApplyDetail2.jsp?whoami=VerifyApplyDetail';
				break;
			case '委外检验单':
				table = 'QUA_VerifyApplyDetail';
				keyfield = 've_id';
				mainfield = 'ved_veid';
				codefield = 've_code';
				kindfield = 've_class';
				url = 'jsps/scm/qc/verifyApplyDetail2.jsp?whoami=VerifyApplyDetail';
				break;
			case '生产检验单':
				table = 'QUA_VerifyApplyDetail';
				keyfield = 've_id';
				mainfield = 'ved_veid';
				codefield = 've_code';
				kindfield = 've_class';
				url = 'jsps/scm/qc/verifyApplyDetail.jsp?whoami=VerifyApplyDetail!FQC';
				break;
			case '客户验货单':
				table = 'QUA_VerifyApplyDetail';
				keyfield = 've_id';
				mainfield = 'ved_veid';
				codefield = 've_code';
				kindfield = 've_class';
				url = 'jsps/scm/qc/verifyApplyDetailOQC.jsp?whoami=VerifyApplyDetailOQC';
				break;
			case '发货检验单':
				table = 'QUA_VerifyApplyDetail';
				keyfield = 've_id';
				mainfield = 'ved_veid';
				codefield = 've_code';
				kindfield = 've_class';
				url = 'jsps/scm/qc/verifyApplyDetailOQC.jsp?whoami=VerifyApplyDetailOQC';
				break;
			}
		if(url){
			cfg = {
				kind: kind,
				table: table,
				keyfield: keyfield,
				mainfield: mainfield,
				codefield: codefield,
				kindfield: kindfield,
				url: url
			}
			this.cfg[kind] = cfg;
		}else{
			return null;
		}
		return cfg;
	},
	getLinks:function(cfg, code,extra,kind) {
		if (!cfg) {
			return;
		}
		if(!this.url[cfg.kind]){
			this.url[cfg.kind] = new Object();
		}
		if(this.url[cfg.kind][code]){
			return this.url[cfg.kind][code];
		}
		var me = this, k = cfg.keyfield, m = cfg.mainfield, url='',URL='',condition;
			if(typeof(kind)!='undefined' && kind!=cfg.kind){
				condition = cfg.codefield + '=\'' + code + '\' and ' + cfg.kindfield + '=\'' + kind + '\''+(extra?extra:'');	
			}else{
				condition = (cfg.codefield + '=\'' + code + '\'' + (cfg.kindfield ? (' and (' + cfg.kindfield + '=\'' + cfg.kind + '\''+(cfg.kind.substr(cfg.kind.length-1)!='单'?' or '+ cfg.kindfield + '=\'' + cfg.kind + '单\')':')')) : ''))+(extra?extra:'');
			}
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: cfg.table,
		   			field: k,
		   			condition: condition
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   				return;
		   			}
		   			if(r.success){
		   				if(r.data){
		   					cfg.url += cfg.url.indexOf('?') > 0 ? '&' : '?';
			   				url = cfg.url + 'formCondition=' + k + 'IS' + r.data + '&gridCondition=' + m + 'IS' + r.data;
			   				me.url[cfg.kind][code]=url;
		   				}else{
		   					me.url[cfg.kind][code]=' ';
		   				}
			   		}
		   	}
		});
		return url;
	},
	Handler: function(view, cell, rowIdx, cellIdx) {
		var column = this;
		var record = view.getStore().getAt(rowIdx);
		var code = record.data[column.dataIndex];
		var kind = '',kind1 = '';
		if(!column.args){
			kind = record.data['class'];
		}else if(column.args[0]){
			if(contains(column.args[0], ',', true)){
				kind = record.data[column.args[0].split(',')[0]];
				kind1 = column.args[0].split(',')[1];
			}else{
				kind = record.data[column.args[0]];
			}
		}else{
			var extra = '';
			if(column.args[1]=='凭证'){
				extra = " and VO_CODE = '"+ record.data['vo_code']+"'";
			}
			if(contains(column.args[1], ',', true)){
				kind = column.args[1].split(',')[0];
				kind1 = column.args[1].split(',')[1];
			}else{
				kind = column.args[1];
			}
		}
		
		if(!this.LinkUtil){
			this.LinkUtil = Ext.create('erp.util.LinkUtil');
		}
		if(kind=='其他付款'||kind=='其他收款'||kind=='其它收款'||kind=='其它付款'||kind=='费用'){
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: 'voucherbill',
		   			field: 'vb_vscode',
		   			condition: "vb_vocode='" + record.get('vo_code')+"'"
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else if(r.success && r.data){
		   				if (['AccountRegiste','BillAP','BillARChange','BillAR','BillAPChange'].indexOf(r.data) > -1) {
		   					kind = r.data;
		   					console.log(kind)
		   				}
		   			}
		   		}
			});
		}
		kind1 = this.LinkUtil.getReallyKind(kind,kind1);
		openUrl2(this.LinkUtil.getLinks(this.LinkUtil.getLinkByKind(kind1),code,extra,kind),kind1 + '(' + code + ')');
	},
	getReallyKind: function(kind,kind1){
		if(kind=='初始化'){
			if(kind1=='AR'){
				return '应收发票';
			}else if(kind1=='AP'){
				return '应付发票';
			}
		}
		return kind;
	}
})
	