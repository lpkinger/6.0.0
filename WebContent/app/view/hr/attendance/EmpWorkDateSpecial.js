Ext.define('erp.view.hr.attendance.EmpWorkDateSpecial',{
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
					saveUrl: 'hr/attendance/saveEmpWorkDateSpecial.action',
					deleteUrl: 'hr/attendance/deleteEmpWorkDateSpecial.action',
					updateUrl: 'hr/attendance/updateEmpWorkDateSpecial.action',
					submitUrl: 'hr/attendance/submitEmpWorkDateSpecial.action',
					resSubmitUrl: 'hr/attendance/resSubmitEmpWorkDateSpecial.action',
					auditUrl: 'hr/attendance/auditEmpWorkDateSpecial.action',
					resAuditUrl: 'hr/attendance/resAuditEmpWorkDateSpecial.action',
					printUrl: 'hr/attendance/printEmpWorkDateSpecial.action',
					getIdUrl: 'common/getId.action?seq=EMPWORKDATESPECIAL_SEQ',
					keyField: 'ews_id',
                    codeField: 'ews_code',
					statusField: 'ews_status',
					statuscodeField: 'ews_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});