Ext.define('erp.view.hr.emplmana.EmpTransferCheckSet',{ 
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
					saveUrl: 'hr/emplmana/saveEmpTransferCheckSet.action',
					updateUrl: 'hr/emplmana/updateEmpTransferCheckSet.action',
					formCondition: getUrlParam('formCondition')?"caller='"+getUrlParam('formCondition').split('callerIS')[1]+"'":''
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					mainField: 'caller',
					keyField: 'id',
					gridCondition : getUrlParam('gridCondition')?"caller='"+getUrlParam('gridCondition').split('callerIS')[1]+"'":''
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});