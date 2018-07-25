Ext.define('erp.view.oa.persontask.workPlan.Register',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpWorkPlanFormPanel',
					anchor: '100% 100%',
					saveUrl: 'oa/persontask/workPlan/saveWorkPlan.action',
					deleteUrl: 'oa/persontask/workPlan/deleteWorkPlan.action',
					updateUrl: 'oa/persontask/workPlan/updateWorkPlan.action',
					getIdUrl: 'common/getId.action?seq=WORKPLAN_SEQ',
					keyField: 'wp_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});