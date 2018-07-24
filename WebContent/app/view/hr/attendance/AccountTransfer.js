Ext.define('erp.view.hr.attendance.AccountTransfer',{
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
					saveUrl: 'oa/fee/saveAccountTransfer.action',
					deleteUrl: 'oa/fee/deleteAccountTransfer.action',
					updateUrl: 'oa/fee/updateAccountTransfer.action',
					auditUrl: 'oa/fee/auditAccountTransfer.action',
					resAuditUrl: 'oa/fee/resAuditAccountTransfer.action',
					submitUrl: 'oa/fee/submitAccountTransfer.action',
					resSubmitUrl: 'oa/fee/resSubmitAccountTransfer.action',
					getIdUrl: 'common/getId.action?seq=AccountTransfer_SEQ',
					keyField: 'at_id',
					codeField: 'at_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});