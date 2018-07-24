Ext.define('erp.view.drp.distribution.Exchange',{ 
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
					anchor: '100% 70%',
					saveUrl: 'drp/distribution/saveExchange.action',
					deleteUrl: 'drp/distribution/deleteExchange.action',
					updateUrl: 'drp/distribution/updateExchange.action',
					auditUrl: 'drp/distribution/auditExchange.action',
					resAuditUrl: 'drp/distribution/resAuditExchange.action',
					submitUrl: 'drp/distribution/submitExchange.action',
					resSubmitUrl: 'drp/distribution/resSubmitExchange.action',
					bannedUrl: 'drp/distribution/bannedExchange.action',
					resBannedUrl: 'drp/distribution/resBannedExchange.action',
					getIdUrl: 'common/getId.action?seq=EXCHANGE_SEQ',
					keyField: 'ec_id',
					codeField: 'ec_code',
					statusField:'ec_status'
				},{
					xtype:'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'ecd_detno',
					keyField: 'ecd_id',
					mainField: 'ecd_ecid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});