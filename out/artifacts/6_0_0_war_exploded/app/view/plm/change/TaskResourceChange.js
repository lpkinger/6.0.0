Ext.define('erp.view.plm.change.TaskResourceChange',{ 
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
					saveUrl: 'plm/change/saveTaskResourceChange.action',
					deleteUrl: 'plm/change/deleteTaskResourceChange.action',
					updateUrl: 'plm/change/updateTaskResourceChange.action',
					auditUrl: 'plm/change/auditTaskResourceChange.action',
					resAuditUrl: 'plm/change/resAuditTaskResourceChange.action',
					submitUrl: 'plm/change/submitTaskResourceChange.action',
					resSubmitUrl: 'plm/change/resSubmitTaskResourceChange.action',
					getIdUrl: 'common/getId.action?seq=TaskResourceChange_SEQ'
					}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});