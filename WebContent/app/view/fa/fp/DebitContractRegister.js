Ext.define('erp.view.fa.fp.DebitContractRegister',{ 
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
					saveUrl: 'fa/fp/saveDebitContractRegister.action',
					deleteUrl: 'fa/fp/deleteDebitContractRegister.action',
					updateUrl: 'fa/fp/updateDebitContractRegister.action',
					auditUrl: 'fa/fp/auditDebitContractRegister.action',
					resAuditUrl: 'fa/fp/resAuditDebitContractRegister.action',
					submitUrl: 'fa/fp/submitDebitContractRegister.action',
					resSubmitUrl: 'fa/fp/resSubmitDebitContractRegister.action',
					getIdUrl: 'common/getId.action?seq=DebitContractRegister_SEQ',
					keyField: 'dcr_id',	
					codeField: 'dcr_contractno',
					statusField: 'dcr_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
