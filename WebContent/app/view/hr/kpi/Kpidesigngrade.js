Ext.define('erp.view.hr.kpi.Kpidesigngrade',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: '/Kpidesigngrade/saveKpidesigngrade.action',
					deleteUrl: '/Kpidesigngrade/deleteKpidesigngrade.action',
					updateUrl: '/Kpidesigngrade/updateKpidesigngrade.action',
					getIdUrl: 'common/getId.action?seq=Kpidesigngrade_SEQ',
					keyField: 'kg_id',
					codeField: ''
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});