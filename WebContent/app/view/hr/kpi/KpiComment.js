Ext.define('erp.view.hr.kpi.KpiComment',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
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
					getIdUrl: 'common/getId.action?seq=KpiComment_SEQ',
					keyField: 'kc_id',
					codeField: ''
				},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});