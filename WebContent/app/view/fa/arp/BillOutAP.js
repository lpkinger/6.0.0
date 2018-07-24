Ext.define('erp.view.fa.arp.BillOutAP',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BillOutAPViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'fa/arp/saveBillOutAP.action',
					deleteUrl: 'fa/arp/deleteBillOutAP.action',
					updateUrl: 'fa/arp/updateBillOutAP.action',
					auditUrl: 'fa/arp/auditBillOutAP.action',
					resAuditUrl: 'fa/arp/resAuditBillOutAP.action',
					submitUrl: 'fa/arp/submitBillOutAP.action',
					resSubmitUrl: 'fa/arp/resSubmitBillOutAP.action',
					postUrl: 'fa/arp/postBillOutAP.action',
					resPostUrl: 'fa/arp/resPostBillOutAP.action',
					printUrl: 'fa/arp/printBillOutAP.action',
					printVoucherCodeUrl: 'fa/arp/printVoucherCodeBillOutAP.action',
					getIdUrl: 'common/getId.action?seq=BILLOUTAP_SEQ',
					codeField: 'bi_code',
					keyField: 'bi_id',
					statusField: 'bi_statuscode',
					voucherConfig: {
						voucherField: 'bi_vouchercode',
						vs_code: 'BillOutAP',
						yearmonth: 'bi_date',
						datas: 'bi_code',
						status: 'bi_statuscode',
						mode: 'single',
						kind: '应付开票记录',
						vomode: 'AP'
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