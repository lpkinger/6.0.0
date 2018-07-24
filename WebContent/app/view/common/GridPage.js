Ext.define('erp.view.common.GridPage',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpGridPanel4',
				defaultCondition: '1=1',
				anchor: '100% 100%',
				saveUrl: 'common/batchSave.action?caller=' +caller,
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller
			}]
		}); 
		me.callParent(arguments); 
	} 
});