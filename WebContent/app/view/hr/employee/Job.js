Ext.define('erp.view.hr.employee.Job',{ 
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
					saveUrl: 'hr/employee/saveJob.action',
					deleteUrl: 'hr/employee/deleteJob.action',
					updateUrl: 'hr/employee/updateJob.action',
					auditUrl: 'hr/employee/auditJob.action?caller=' +caller,
					resAuditUrl: 'hr/employee/resAuditJob.action?caller=' +caller,
					submitUrl: 'hr/employee/submitJob.action?caller='+caller,
					resSubmitUrl: 'hr/employee/resSubmitJob.action?caller='+caller,
					bannedUrl: 'hr/employee/bannedJob.action',
					resBannedUrl: 'hr/employee/resBannedJob.action',
					getIdUrl: 'common/getId.action?seq=JOB_SEQ',
					keyField: 'jo_id',
					codeField: 'jo_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});