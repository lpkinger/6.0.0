Ext.define('erp.view.plm.cost.ProjectFeeClaim',{ 
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
					anchor: '100% 55%',
					saveUrl: 'plm/cost/saveProjectFeeClaim.action',
					deleteUrl:'plm/cost/deleteProjectFeeClaim.action',
					updateUrl:'plm/cost/updateProjectFeeClaim.action',
					submitUrl:'plm/cost/submitProjectFeeClaim.action',
					resSubmitUrl:'plm/cost/resSubmitProjectFeeClaim.action',
					auditUrl:'plm/cost/auditProjectFeeClaim.action',
					resAuditUrl:'plm/cost/resAuditProjectFeeClaim.action',
					getIdUrl:'common/getId.action?seq=ProjectFeeClaim_SEQ',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 45%',
				  }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});