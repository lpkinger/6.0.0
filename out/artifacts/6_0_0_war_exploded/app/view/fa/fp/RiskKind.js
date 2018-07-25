Ext.define('erp.view.fa.fp.RiskKind',{ 
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
					saveUrl: 'fa/fp/saveRiskKind.action',
					deleteUrl: 'fa/fp/deleteRiskKind.action',
					updateUrl: 'fa/fp/updateRiskKind.action',
					auditUrl: 'fa/fp/auditRiskKind.action',
					resAuditUrl: 'fa/fp/resAuditRiskKind.action',
					submitUrl: 'fa/fp/submitRiskKind.action',
					resSubmitUrl: 'fa/fp/resSubmitRiskKind.action',
					printUrl: 'fa/fp/printRiskKind.action',
					getIdUrl: 'common/getId.action?seq=RiskKind_SEQ',
					keyField: 'rk_id',
					codeField: 'rk_code',
					statusField: 'rk_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});