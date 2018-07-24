Ext.define('erp.view.drp.distribution.Terminal',{ 
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
					saveUrl: 'drp/distribution/saveTerminal.action',
					deleteUrl: 'drp/distribution/deleteTerminal.action',
					updateUrl: 'drp/distribution/updateTerminal.action',
					auditUrl: 'drp/distribution/auditTerminal.action',
					resAuditUrl: 'drp/distribution/resAuditTerminal.action',
					submitUrl: 'drp/distribution/submitTerminal.action',
					resSubmitUrl: 'drp/distribution/resSubmitTerminal.action',
					bannedUrl: 'drp/distribution/bannedTerminal.action',
					resBannedUrl: 'drp/distribution/resBannedTerminal.action',
					getIdUrl: 'common/getId.action?seq=TERMINAL_SEQ',
					keyField: 'te_id',
					codeField: 'te_code',
					statusField:'te_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});