Ext.define('erp.view.pm.make.MakeWork',{ 
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
					saveUrl: 'pm/make/saveMakeWork.action',
					deleteUrl: 'pm/make/deleteMakeWork.action',
					updateUrl: 'pm/make/updateMakeWork.action',
					submitUrl: 'pm/make/submitMakeWork.action',
					auditUrl: 'pm/make/auditMakeWork.action',
					resAuditUrl: 'pm/make/resAuditMakeWork.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeWork.action',
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