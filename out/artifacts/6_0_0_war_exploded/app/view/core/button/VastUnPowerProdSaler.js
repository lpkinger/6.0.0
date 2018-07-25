/**
 * 物料批量取消授权按钮
 */	
Ext.define('erp.view.core.button.VastUnPowerProdSaler',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastUnPowerProdSalerButton',
		text: $I18N.common.button.erpVastUnPowerProdSalerButton,
    	iconCls: 'senddown',
    	cls: 'x-btn-gray',
    	id: 'erpVastUnPowerProdSalerButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});