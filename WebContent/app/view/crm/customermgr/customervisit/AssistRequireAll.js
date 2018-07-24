Ext.define('erp.view.crm.customermgr.customervisit.AssistRequireAll',{ 
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
					saveUrl: 'crm/customermgr/saveAssistRequire.action',
					deleteUrl: 'crm/customermgr/deleteAssistRequire.action',
					updateUrl: 'crm/customermgr/updateAssistRequire.action',
					auditUrl: 'crm/customermgr/auditAssistRequire.action',
					resAuditUrl: 'crm/customermgr/resAuditAssistRequire.action',
					submitUrl: 'crm/customermgr/submitAssistRequire.action',
					resSubmitUrl:  'crm/customermgr/resSubmitAssistRequire.action',
					getIdUrl: 'common/getId.action?seq=PREAssistRequire_SEQ',
					keyField: 'ar_id',
					codeField: 'ar_code',
					statusField: 'ar_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'ard_detno',
					necessaryField: '',
					keyField: 'ard_id',
					mainField: 'ard_arid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});