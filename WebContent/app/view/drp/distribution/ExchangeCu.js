Ext.define('erp.view.drp.distribution.ExchangeCu',{ 
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
					saveUrl: 'drp/distribution/saveExchangeCu.action',
					deleteUrl: 'drp/distribution/deleteExchangeCu.action',
					updateUrl: 'drp/distribution/updateExchangeCu.action',
					auditUrl: 'drp/distribution/auditExchangeCu.action',
					resAuditUrl: 'drp/distribution/resAuditExchangeCu.action',
					submitUrl: 'drp/distribution/submitExchangeCu.action',
					resSubmitUrl: 'drp/distribution/resSubmitExchangeCu.action',
					bannedUrl: 'drp/distribution/bannedExchangeCu.action',
					resBannedUrl: 'drp/distribution/resBannedExchangeCu.action',
					getIdUrl: 'common/getId.action?seq=EXCHANGECU_SEQ',
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