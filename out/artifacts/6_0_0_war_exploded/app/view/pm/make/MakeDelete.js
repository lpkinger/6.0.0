Ext.define('erp.view.pm.make.MakeDelete',{ 
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
					saveUrl: 'pm/make/saveMakeDelete.action',
					deleteUrl: 'pm/make/deleteMakeDelete.action',
					updateUrl: 'pm/make/updateMakeDelete.action',
					submitUrl: 'pm/make/submitMakeDelete.action',
					auditUrl: 'pm/make/auditMakeDelete.action',
					resAuditUrl: 'pm/make/resAuditMakeDelete.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeDelete.action',
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