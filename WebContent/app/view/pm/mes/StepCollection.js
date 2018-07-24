Ext.define('erp.view.pm.mes.StepCollection',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				updateUrl: 'pm/mes/saveStepCollection.action',				
				keyField: 'cr_id',
				codeField: 'cr_code', 
				statusField: 'cr_status',
				statuscodeField: 'cr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'sp_detno',
				keyField: 'sp_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});