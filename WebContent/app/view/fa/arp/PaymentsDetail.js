Ext.define('erp.view.fa.arp.PaymentsDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'arpPaymentsViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/arp/savePaymentsDetail.action',
					deleteUrl: 'fa/arp/deletePaymentsDetail.action',
					updateUrl: 'fa/arp/updatePaymentsDetail.action',
					auditUrl: 'fa/arp/auditPaymentsDetail.action',
					resAuditUrl: 'fa/arp/resAuditPaymentsDetail.action',
					submitUrl: 'fa/arp/submitPaymentsDetail.action',
					resSubmitUrl: 'fa/arp/resSubmitPaymentsDetail.action',
					bannedUrl: 'fa/arp/bannedPaymentsDetail.action',
					resBannedUrl: 'fa/arp/resBannedPaymentsDetail.action',
					getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
					keyField: 'pa_id',
					codeField: 'pa_code',
					statusField: 'pa_auditstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'pad_detno',
					keyField: 'pad_id',
					mainField: 'pad_paid',
					necessaryField: 'pad_subpaid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});