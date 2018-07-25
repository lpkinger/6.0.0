Ext.define('erp.view.pm.make.DeleteDeal',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveDeleteDeal.action',
					deleteUrl: 'pm/make/deleteDeleteDeal.action',
					updateUrl: 'pm/make/updateDeleteDeal.action',
					auditUrl: 'pm/make/auditDeleteDeal.action',
					resAuditUrl: 'pm/make/resAuditDeleteDeal.action',
					submitUrl: 'pm/make/submitDeleteDeal.action',
					resSubmitUrl: 'pm/make/resSubmitDeleteDeal.action',
					getIdUrl: 'common/getId.action?seq=MAKE_SEQ',
					keyField: 'ma_id',
					codeField: 'ma_code',
					statusField: 'ma_checkstatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'mm_detno',
					necessaryField: 'mm_prodcode',
					keyField: 'mm_id',
					mainField: 'mm_maid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});