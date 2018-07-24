Ext.define('erp.view.pm.make.MakeOrder',{ 
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
					saveUrl: 'pm/make/saveMakeOrder.action',
					deleteUrl: 'pm/make/deleteMakeOrder.action',
					updateUrl: 'pm/make/updateMakeOrder.action',
					submitUrl: 'pm/make/submitMakeOrder.action',
					auditUrl: 'pm/make/auditMakeOrder.action',
					resAuditUrl: 'pm/make/resAuditMakeOrder.action',					
					resSubmitUrl: 'pm/make/resSubmitMakeOrder.action',
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