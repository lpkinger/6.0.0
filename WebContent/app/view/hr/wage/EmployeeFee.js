Ext.define('erp.view.hr.wage.EmployeeFee',{ 
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
					anchor: '100% 100%',
					saveUrl: 'hr/wage/saveEmployeeFee.action',
					deleteUrl: 'hr/wage/deleteEmployeeFee.action',
					updateUrl: 'hr/wage/updateEmployeeFee.action',
					getIdUrl: 'common/getId.action?seq=EmployeeFee_SEQ',
					keyField: 'ef_id',
					codeField: 'ef_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});