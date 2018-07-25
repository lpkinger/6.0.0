Ext.define('erp.view.oa.fee.CarAllowance',{ 
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
					saveUrl: 'oa/fee/saveCarAllowance.action',
					deleteUrl: 'oa/fee/deleteCarAllowance.action',
					updateUrl: 'oa/fee/updateCarAllowance.action',
					getIdUrl: 'common/getId.action?seq=CarAllowance_SEQ',
					auditUrl: 'oa/fee/auditCarAllowance.action',
					resAuditUrl: 'oa/fee/resAuditCarAllowance.action',
					submitUrl: 'oa/fee/submitCarAllowance.action',
					resSubmitUrl: 'oa/fee/resSubmitCarAllowance.action',
					confirmUrl:'oa/fee/confirmCarAllowance.action',
					keyField: 'ca_id',
					codeField: 'ca_code',
					statusField: 'ca_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});