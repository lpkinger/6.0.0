Ext.define('erp.view.hr.wage.EmployeeFeeBatch',{ 
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
					anchor: '100% 10%',
					updateUrl: 'hr/wage/updateEmployeeFeeBatch.action',
					keyField: 'emf_id',
					codeField: 'emf_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 90%', 
					keyField: 'ef_id',
					detno: 'ef_detno',
					mainField: 'ef_emfid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});