Ext.define('erp.view.pm.mes.MakeSerial',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MakeSerialViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'pm/mes/updateMakeSerial.action',
					updateUrl: 'pm/mes/updateMakeSerial.action',
					keyField: 'mc_id',
					codeField: 'mc_code', 
					statusField: 'mc_status',
					statuscodeField: 'mc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					keyField: 'ms_id',
					mainField: 'ms_mcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});