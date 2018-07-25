Ext.define('erp.view.pm.make.MakeInverse',{ 
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
					saveUrl: 'pm/make/saveMakeInverse.action',
					deleteUrl: 'pm/make/deleteMakeInverse.action',
					updateUrl: 'pm/make/updateMakeInverse.action',
					submitUrl: 'pm/make/submitMakeInverse.action',
					auditUrl: 'pm/make/auditMakeInverse.action',
					resAuditUrl: 'pm/make/resAuditMakeInverse.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeInverse.action',
					getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
					keyField: 'ma_id',
					statusField: 'ma_checkstatus',
					codeField: 'ma_checkstatuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});