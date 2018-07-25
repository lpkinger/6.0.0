Ext.define('erp.view.hr.attendance.EmpWorkDateChange',{
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
					anchor: '100% 65%',
					saveUrl: 'hr/attendance/saveEmpWorkDateChange.action?caller=' +caller,
					deleteUrl: 'hr/attendance/deleteEmpWorkDateChange.action?caller=' +caller,
					updateUrl: 'hr/attendance/updateEmpWorkDateChange.action?caller=' +caller,
					auditUrl: 'hr/attendance/auditEmpWorkDateChange.action?caller=' +caller,
					//resAuditUrl: 'hr/attendance/resAuditEmpWorkDateChange.action?caller=' +caller,
					submitUrl: 'hr/attendance/submitEmpWorkDateChange.action?caller=' +caller,
					printUrl: 'hr/attendance/printEmpWorkDateChange.action?caller=' +caller,
					resSubmitUrl: 'hr/attendance/resSubmitEmpWorkDateChange.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=EMPWORKDATECHANGE_SEQ',
					keyField: 'edc_id',
                    codeField: 'edc_code',
					statusField: 'edc_status',
					statuscodeField: 'edc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%',
                    necessaryField: '',
					keyField: 'edcd_id',
					detno: 'edcd_detno',
					mainField: 'edcd_edcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});