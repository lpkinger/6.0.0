Ext.define('erp.view.fa.ars.PreapBill',{ 
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
					anchor: '100% 70%',
					/*saveUrl: 'crm/chance/saveCustomer.action',
					deleteUrl: 'crm/chance/deleteCustomer.action',
					updateUrl: 'crm/chance/updateCustomer.action',
					auditUrl: 'crm/chance/auditCustomer.action',
					resAuditUrl: 'crm/chance/resAuditCustomer.action',
					submitUrl: 'crm/chance/submitCustomer.action',
					resSubmitUrl:  'crm/chance/resSubmitCustomer.action',*/
					getIdUrl: 'common/getId.action?seq=PreapBill_SEQ',
					keyField: 'pb_id',
					codeField: 'pb_code',
					statusField: 'pb_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'pbd_detno',
					necessaryField: 'pbd_name',
					keyField: 'pbd_id',
					mainField: 'pbd_pbid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});