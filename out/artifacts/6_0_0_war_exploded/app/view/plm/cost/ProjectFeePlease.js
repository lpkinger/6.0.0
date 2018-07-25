Ext.define('erp.view.plm.cost.ProjectFeePlease',{ 
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
					saveUrl: 'plm/cost/saveProjectFeePlease.action',
					deleteUrl:'plm/cost/deleteProjectFeePlease.action',
					updateUrl:'plm/cost/updateProjectFeePlease.action',
					submitUrl:'plm/cost/submitProjectFeePlease.action',
					resSubmitUrl:'plm/cost/resSubmitProjectFeePlease.action',
					auditUrl:'plm/cost/auditProjectFeePlease.action',
					resAuditUrl:'plm/cost/resAuditProjectFeePlease.action',
					getIdUrl:'common/getId.action?seq=ProjectFeePlease_SEQ',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 45%',
				  }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});