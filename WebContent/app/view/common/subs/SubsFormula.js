Ext.define('erp.view.common.subs.SubsFormula',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				bodyStyle: 'background:#f1f1f1;',
				saveUrl: 'common/charts/save.action?caller=' +caller,
				updateUrl: 'common/charts/update.action?caller=' +caller,
				deleteUrl: 'common/charts/delete.action?caller=' +caller,		
				auditUrl: 'common/charts/audit.action?caller=' +caller,			
				resAuditUrl: 'common/charts/resAudit.action?caller=' +caller,
				submitUrl: 'common/charts/submit.action?caller=' +caller,
				resSubmitUrl: 'common/charts/resSubmit.action?caller=' +caller,
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
	    	},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});