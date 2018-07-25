Ext.define('erp.view.hr.kpi.KpiType',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=KpiType_SEQ',
					keyField: 'kt_id',
					codeField: ''
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});