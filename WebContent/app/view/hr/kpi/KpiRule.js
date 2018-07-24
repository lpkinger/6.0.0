Ext.define('erp.view.hr.kpi.KpiRule',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: '/KpiRule/saveKpiRule.action',
					deleteUrl: '/KpiRule/deleteKpiRule.action',
					updateUrl: '/KpiRule/updateKpiRule.action',
					getIdUrl: 'common/getId.action?seq=KpiRule_SEQ',
					keyField: 'kr_id',
					codeField: 'kr_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});