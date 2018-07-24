Ext.define('erp.view.hr.kpi.KpiResult',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				keyField: 'kt_id',
				codeField: ''
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				keyField: 'ktd_id',
				mainField: 'ktd_ktid',
				detno: 'ktd_detno'
		}] 
		}); 
		me.callParent(arguments); 
	} 
});