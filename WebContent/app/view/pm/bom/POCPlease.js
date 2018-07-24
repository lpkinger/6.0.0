Ext.define('erp.view.pm.bom.POCPlease',{ 
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
					saveUrl: 'pm/bom/savePOCPlease.action',
					deleteUrl: 'pm/bom/deletePOCPlease.action',
					updateUrl: 'pm/bom/updatePOCPlease.action',
					auditUrl: 'pm/bom/auditPOCPlease.action',
					resAuditUrl: 'pm/bom/resAuditPOCPlease.action',
					submitUrl: 'pm/bom/submitPOCPlease.action',
					resSubmitUrl: 'pm/bom/resSubmitPOCPlease.action',
					getIdUrl: 'common/getId.action?seq=POCPLEASE_SEQ',
					keyField: 'poc_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});