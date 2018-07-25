Ext.define('erp.view.pm.mes.CraftMake',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'CraftMakeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'pm/mes/updateCraftMake.action',
					updateUrl: 'pm/mes/updateCraftMake.action',
					keyField: 'ma_id',
					codeField: 'ma_code', 
					statusField: 'ma_status',
					statuscodeField: 'ma_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					detno: 'cd_detno',
					keyField: 'cd_id',
					mainField: 'cd_crid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});