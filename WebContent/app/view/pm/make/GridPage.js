Ext.define('erp.view.pm.make.GridPage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
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
			}] 
		}); 
		me.callParent(arguments); 
	} 
});