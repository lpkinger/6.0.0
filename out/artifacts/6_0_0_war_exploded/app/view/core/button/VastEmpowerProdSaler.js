/**
 * 物料批量授权按钮
 */	
Ext.define('erp.view.core.button.VastEmpowerProdSaler',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastEmpowerProdSalerButton',
		text: $I18N.common.button.erpVastEmpowerProdSalerButton,
    	iconCls: 'sendup',
    	cls: 'x-btn-gray',
    	id: 'erpVastEmpowerProdSalerButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});