Ext.define('erp.view.hr.emplmana.EmpTransferCheck',{ 
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
					anchor: '100% 30%',
					saveUrl: 'hr/emplmana/saveEmpTransferCheck.action',
					deleteUrl: 'hr/emplmana/deleteEmpTransferCheck.action',
					updateUrl: 'hr/emplmana/updateEmpTransferCheck.action',
					getIdUrl: 'common/getId.action?seq=EmpTransferCheck_SEQ',
					keyField: 'ec_id',
					auditUrl: 'hr/emplmana/auditEmpTransferCheck.action',
					resAuditUrl: 'hr/emplmana/resEmpTransferCheck.action',
					submitUrl: 'hr/emplmana/submitEmpTransferCheck.action',
					resSubmitUrl: 'hr/emplmana/resSubmitEmpTransferCheck.action'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});