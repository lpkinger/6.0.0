Ext.define('erp.view.fa.ars.AccountRegister',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'AccountRegisterViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveAccountRegister.action',
					deleteUrl: 'fa/ars/deleteAccountRegister.action',
					updateUrl: 'fa/ars/updateAccountRegister.action',
					auditUrl: 'fa/ars/auditAccountRegister.action',
					resAuditUrl: 'fa/ars/resAuditAccountRegister.action',
					submitUrl: 'fa/ars/submitAccountRegister.action',
					resSubmitUrl: 'fa/ars/resSubmitAccountRegister.action',
					postUrl: 'fa/ars/postAccountRegister.action',
					getIdUrl: 'common/getId.action?seq=AccountRegister_SEQ',
					keyField: 'ar_id',
					codeField: 'ar_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ard_detno',  
					necessaryField: 'ard_ordercode',
					keyField: 'ard_id',
					mainField: 'ard_arid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});