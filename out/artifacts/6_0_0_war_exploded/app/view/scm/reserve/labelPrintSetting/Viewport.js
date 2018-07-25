Ext.define('erp.view.scm.reserve.labelPrintSetting.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpLabelPrintSettingGrid',
				anchor: '100% 100%',
				keyField: 'lps_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});