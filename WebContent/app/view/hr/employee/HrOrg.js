Ext.define('erp.view.hr.employee.HrOrg',{ 
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
					saveUrl: 'hr/employee/saveHrOrg.action',
					deleteUrl: 'hr/employee/deleteHrOrg.action',
					updateUrl: 'hr/employee/updateHrOrg.action',	
					auditUrl: 'hr/employee/auditHrOrg.action',
					resAuditUrl: 'hr/employee/resAuditHrOrg.action',
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					bannedUrl: 'hr/employee/bannedHrOrg.action',
					resBannedUrl:'hr/employee/resBannedHrOrg.action', 
					getIdUrl: 'common/getId.action?seq=HrOrg_SEQ',
					keyField: 'or_id',
					codeField: 'or_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});