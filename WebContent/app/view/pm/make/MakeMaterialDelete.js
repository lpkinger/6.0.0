Ext.define('erp.view.pm.make.MakeMaterialDelete',{ 
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
					saveUrl: 'pm/make/saveMakeMaterialDelete.action',
					deleteUrl: 'pm/make/deleteMakeMaterialDelete.action',
					updateUrl: 'pm/make/updateMakeMaterialDelete.action',
					submitUrl: 'pm/make/submitMakeMaterialDelete.action',
					auditUrl: 'pm/make/auditMakeMaterialDelete.action',
					resAuditUrl: 'pm/make/resAuditMakeMaterialDelete.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeMaterialDelete.action',
					getIdUrl: 'common/getId.action?seq=MAKEMATERIAL_SEQ',
					keyField: 'mm_id',
					statusField: 'mm_status',
					codeField: 'mm_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});