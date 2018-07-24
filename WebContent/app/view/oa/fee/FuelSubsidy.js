Ext.define('erp.view.oa.fee.FuelSubsidy',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'oa/fee/saveFuelSubsidy.action',
				deleteUrl: 'oa/fee/deleteFuelSubsidy.action',
				updateUrl: 'oa/fee/updateFuelSubsidy.action',
				auditUrl: 'oa/fee/auditFuelSubsidy.action',
				resAuditUrl: 'oa/fee/resAuditFuelSubsidy.action',
				submitUrl: 'oa/fee/submitFuelSubsidy.action',
				resSubmitUrl: 'oa/fee/resSubmitFuelSubsidy.action',
				confirmUrl:'oa/fee/confirmFuelSubsidy.action',
				getIdUrl: 'common/getId.action?seq=FuelSubsidy_SEQ',
				keyField: 'fs_id',
				codeField: 'fs_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});