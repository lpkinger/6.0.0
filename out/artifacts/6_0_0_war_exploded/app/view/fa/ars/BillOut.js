Ext.define('erp.view.fa.ars.BillOut',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BillOutViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'fa/ars/saveBillOut.action',
					deleteUrl: 'fa/ars/deleteBillOut.action',
					updateUrl: 'fa/ars/updateBillOut.action',
					auditUrl: 'fa/ars/auditBillOut.action',
					resAuditUrl: 'fa/ars/resAuditBillOut.action',
					printUrl: 'fa/ars/printBillOut.action',
					printVoucherCodeUrl: 'fa/ars/printVoucherCodeBillOut.action',
					submitUrl: 'fa/ars/submitBillOut.action',
					resSubmitUrl: 'fa/ars/resSubmitBillOut.action',
					postUrl: 'fa/ars/postBillOut.action',
					resPostUrl: 'fa/ars/resPostBillOut.action',	
					getIdUrl: 'common/getId.action?seq=BILLOUT_SEQ',
					codeField: 'bi_code',
					keyField: 'bi_id',
					statusField: 'bi_statuscode',
					voucherConfig: {
						voucherField: 'bi_vouchercode',
						vs_code: 'BillOut',
						yearmonth: 'bi_date',
						datas: 'bi_code',
						status: 'bi_statuscode',
						mode: 'single',
						kind: '应收开票记录',
						vomode: 'AR'
					}
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'ard_detno',
					necessaryField: 'ard_ordercode',
					keyField: 'ard_id',
					mainField: 'ard_biid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});