Ext.define('erp.view.fa.fp.CreditContractRegister',{ 
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
					saveUrl: 'fa/fp/saveCreditContractRegister.action',
					deleteUrl: 'fa/fp/deleteCreditContractRegister.action',
					updateUrl: 'fa/fp/updateCreditContractRegister.action',
					auditUrl: 'fa/fp/auditCreditContractRegister.action',
					resAuditUrl: 'fa/fp/resAuditCreditContractRegister.action',
					submitUrl: 'fa/fp/submitCreditContractRegister.action',
					resSubmitUrl: 'fa/fp/resSubmitCreditContractRegister.action',
					getIdUrl: 'common/getId.action?seq=CreditContractRegister_SEQ',
					keyField: 'ccr_id',	
					codeField: 'ccr_contractno',
					statusField: 'ccr_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
