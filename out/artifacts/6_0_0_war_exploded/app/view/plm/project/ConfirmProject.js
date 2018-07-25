Ext.define('erp.view.plm.project.ConfirmProject',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					auditUrl:'plm/projectplan/auditProjectPlan.action',
					deleteUrl: 'plm/projectplan/deleteProjectPlan.action',
					updateUrl: 'plm/projectplan/updateProjectPlan.action',
					getIdUrl: 'common/getId.action?seq=CONFIRMPROJECK_SEQ',
					keyField: 'cp_id'
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});