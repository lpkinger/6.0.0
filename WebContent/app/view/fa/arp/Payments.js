Ext.define('erp.view.fa.arp.Payments',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fa/arp/savePayments.action',
					deleteUrl: 'fa/arp/deletePayments.action',
					updateUrl: 'fa/arp/updatePayments.action',
					auditUrl: 'fa/arp/auditPayments.action',
					resAuditUrl: 'fa/arp/resAuditPayments.action',
					submitUrl: 'fa/arp/submitPayments.action',
					resSubmitUrl: 'fa/arp/resSubmitPayments.action',
					bannedUrl: 'fa/arp/bannedPayments.action',
					resBannedUrl: 'fa/arp/resBannedPayments.action',
					getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
					keyField: 'pa_id',
					codeField: 'pa_code',
					statusField: 'pa_auditstatuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});