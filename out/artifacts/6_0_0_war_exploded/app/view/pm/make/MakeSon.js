Ext.define('erp.view.pm.make.MakeSon',{ 
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
					saveUrl: 'pm/make/saveMakeSon.action',
					deleteUrl: 'pm/make/deleteMakeSon.action',
					updateUrl: 'pm/make/updateMakeSon.action',
					submitUrl: 'pm/make/submitMakeSon.action',
					auditUrl: 'pm/make/auditMakeSon.action',
					resAuditUrl: 'pm/make/resAuditMakeSon.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeSon.action',
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