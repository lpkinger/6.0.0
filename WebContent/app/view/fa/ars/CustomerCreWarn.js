Ext.define('erp.view.fa.ars.CustomerCreWarn',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'customerCreWarnViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveCustomerCreWarn.action',
					deleteUrl: 'fa/ars/deleteCustomerCreWarn.action',
					updateUrl: 'fa/ars/updateCustomerCreWarn.action',
					auditUrl: 'fa/ars/auditCustomerCreWarn.action',
					resAuditUrl: 'fa/ars/resAuditCustomerCreWarn.action',
					submitUrl: 'fa/ars/submitCustomerCreWarn.action',
					resSubmitUrl: 'fa/ars/resSubmitCustomerCreWarn.action',
					getIdUrl: 'common/getId.action?seq=CustomerCreWarn_SEQ',
					keyField: 'cu_id',
					codeField: 'cu_code',
					statusField: 'cu_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					/*detno: 'abd_detno', */ //  整个要改啊 啊啊 啊………………
					/*necessaryField: 'abd_prodcode',*/
					keyField: 'cm_id',
					mainField: 'cm_custid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});