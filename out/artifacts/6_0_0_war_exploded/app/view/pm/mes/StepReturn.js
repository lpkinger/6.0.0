Ext.define('erp.view.pm.mes.StepReturn',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'StepReturnViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveStepReturn.action',
					deleteUrl: 'pm/mes/deleteStepReturn.action',
					updateUrl: 'pm/mes/updateStepReturn.action',
					getIdUrl: 'common/getId.action?seq=StepReturn_SEQ',
					submitUrl: 'pm/mes/submitStepReturn.action',
					auditUrl: 'pm/mes/auditStepReturn.action',
					resAuditUrl: 'pm/mes/resAuditStepReturn.action',			
					resSubmitUrl: 'pm/mes/resSubmitStepReturn.action',
					keyField: 'sr_id',
					codeField: 'sr_code', 
					statusField: 'sr_status',
					statuscodeField: 'sr_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});