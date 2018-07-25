Ext.define('erp.view.ma.logic.Enterprise',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					//saveUrl: 'ma/logic/saveEnterprise.action',
					//deleteUrl: 'ma/logic/deleteEnterprise.action',
					updateUrl: 'ma/logic/updateEnterprise.action',
					getIdUrl: 'common/getId.action?seq=Enterprise_SEQ',
					auditUrl: 'ma/logic/auditEnterprise.action',
					resAuditUrl: 'ma/logic/resAuditEnterprise.action',
					submitUrl: 'ma/logic/regB2BEnterprise.action',
					resSubmitUrl: 'ma/logic/resSubmitEnterprise.action',
					keyField: 'kb_id'
					//codeField: 'kb_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});