Ext.define('erp.view.hr.kpi.Kpigradetype',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=KpiType_SEQ',
					keyField: 'kp_id',
					codeField: 'kp_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%'
			}]
		}); 
		me.callParent(arguments); 
	} 
});