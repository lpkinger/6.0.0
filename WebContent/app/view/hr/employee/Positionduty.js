Ext.define('erp.view.hr.employee.Positionduty',{ 
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
					saveUrl: 'hr/employee/savePositionduty.action',
					deleteUrl: 'hr/employee/deletePositionduty.action',
					updateUrl: 'hr/employee/updatePositionduty.action',
					auditUrl: 'hr/employee/auditPositionduty.action',
					resAuditUrl: 'hr/employee/resAuditPositionduty.action',
					submitUrl: 'hr/employee/submitPositionduty.action',
					resSubmitUrl: 'hr/employee/resSubmitPositionduty.action',
					getIdUrl: 'common/getId.action?seq=Positionduty_SEQ',
					keyField: 'pd_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					necessaryField: 'pdd_name',
					keyField: 'pdd_id',
					detno: 'pdd_detno',
					mainField: 'pdd_pdid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});