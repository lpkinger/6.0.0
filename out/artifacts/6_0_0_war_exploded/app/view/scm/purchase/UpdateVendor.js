Ext.define('erp.view.scm.purchase.UpdateVendor',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpGridPanel4',
				defaultCondition: '1=1 order by ve_id desc',
				anchor: '100% 100%',
				saveUrl: 'scm/vendor/batchUpdateVendor.action',
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