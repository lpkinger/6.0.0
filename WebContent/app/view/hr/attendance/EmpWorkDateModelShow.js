Ext.define('erp.view.hr.attendance.EmpWorkDateModelShow',{
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
					anchor: '100% 35%',
//					saveUrl: 'hr/attendance/saveEmpWorkDateModel.action?caller=' +caller,
//					deleteUrl: 'hr/attendance/deleteEmpWorkDateModel.action?caller=' +caller,
//					updateUrl: 'hr/attendance/updateEmpWorkDateModel.action?caller=' +caller,
//					printUrl: 'hr/attendance/printEmpWorkDateModel.action?caller=' +caller,
//					getIdUrl: 'common/getId.action?seq=EMPWORKDATEMODEL_SEQ',
					keyField: 'em_id',
                    codeField: 'em_code',
					statusField: 'em_status',
					statuscodeField: 'em_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'emd_id',
					detno: 'emd_detno',
					mainField: 'emd_emid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});