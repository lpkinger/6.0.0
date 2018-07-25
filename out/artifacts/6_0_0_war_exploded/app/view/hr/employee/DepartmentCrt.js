Ext.define('erp.view.hr.employee.DepartmentCrt',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'hr/saveDepartmentCrt.action',
					deleteUrl: 'hr/deleteDepartmentCrt.action',
					updateUrl: 'hr/updateDepartmentCrt.action',
					getIdUrl: 'common/getId.action?seq=DepartmentCrt_SEQ',
					auditUrl: 'hr/auditDepartmentCrt.action',
					resAuditUrl: 'hr/resAuditDepartmentCrt.action',
					submitUrl: 'hr/submitDepartmentCrt.action',
					resSubmitUrl: 'hr/resSubmitDepartmentCrt.action',
					keyField: 'dc_id',
					codeField: 'dc_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					keyField: 'dcd_id',
					detno: 'dcd_detno',
					mainField: 'dcd_dcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});