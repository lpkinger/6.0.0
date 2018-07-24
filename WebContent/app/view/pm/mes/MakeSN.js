Ext.define('erp.view.pm.mes.MakeSN',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MakeSNViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'pm/mes/updateMakeSN.action',
					updateUrl: 'pm/mes/updateMakeSN.action',
					keyField: 'ma_id',
					codeField: 'ma_code', 
					statusField: 'ma_status',
					statuscodeField: 'ma_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					keyField: 'msl_id',
					mainField: 'msl_maid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});