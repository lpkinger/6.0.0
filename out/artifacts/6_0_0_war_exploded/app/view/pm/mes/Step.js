Ext.define('erp.view.pm.mes.Step',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'StepViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveStep.action',
					deleteUrl: 'pm/mes/deleteStep.action',
					updateUrl: 'pm/mes/updateStep.action',
					getIdUrl: 'common/getId.action?seq=Step_SEQ',
					submitUrl: 'pm/mes/submitStep.action',
					auditUrl: 'pm/mes/auditStep.action',
					resAuditUrl: 'pm/mes/resAuditStep.action',			
					resSubmitUrl: 'pm/mes/resSubmitStep.action',
					keyField: 'st_id',
					codeField: 'st_code', 
					statusField: 'st_status',
					statuscodeField: 'st_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});