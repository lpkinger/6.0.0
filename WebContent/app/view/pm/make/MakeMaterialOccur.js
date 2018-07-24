Ext.define('erp.view.pm.make.MakeMaterialOccur',{ 
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
					saveUrl: 'pm/make/saveMakeMaterialOccur.action',
					deleteUrl: 'pm/make/deleteMakeMaterialOccur.action',
					updateUrl: 'pm/make/updateMakeMaterialOccur.action',
					submitUrl: 'pm/make/submitMakeMaterialOccur.action',
					auditUrl: 'pm/make/auditMakeMaterialOccur.action',
					resAuditUrl: 'pm/make/resAuditMakeMaterialOccur.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeMaterialOccur.action',
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