Ext.define('erp.view.plm.change.ProjectChange',{ 
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
					saveUrl: 'plm/change/saveProjectChange.action',
					deleteUrl: 'plm/change/deleteProjectChange.action',
					updateUrl: 'plm/change/updateProjectChange.action',
					auditUrl: 'plm/change/auditProjectChange.action',
					resAuditUrl: 'plm/change/resAuditProjectChange.action',
					submitUrl: 'plm/change/submitProjectChange.action',
					resSubmitUrl: 'plm/change/resSubmitProjectChange.action',
					getIdUrl: 'common/getId.action?seq=PROJECTCHANGE_SEQ',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});