Ext.define('erp.view.common.JProcess.RulesApply',{
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
					saveUrl: '/common/saveJprocessRulesApply.action',
					deleteUrl: '/common/deleteJprocessRulesApply.action',
					updateUrl: '/common/updateJprocessRulesApply.action',
					auditUrl: '/common/auditJprocessRulesApply.action',
					resAuditUrl: '/common/resAuditJprocessRulesApply.action',
					submitUrl: '/common/submitJprocessRulesApply.action',
					resSubmitUrl: '/common/resSubmitJprocessRulesApply.action',		
					getIdUrl: 'common/getId.action?seq=jprocessruleapply_SEQ',
					keyField: 'ra_id',
					codeField: 'ra_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});