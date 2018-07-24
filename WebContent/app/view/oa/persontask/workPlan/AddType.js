Ext.define('erp.view.oa.persontask.workPlan.AddType',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'oa/persontask/workPlan/saveWorkPlanType.action',
					deleteUrl: 'oa/persontask/workPlan/deleteWorkPlanType.action',
					updateUrl: 'oa/persontask/workPlan/updateWorkPlanType.action',
					getIdUrl: 'common/getId.action?seq=WORKPLANTYPE_SEQ',
					keyField: 'wpt_id',
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});