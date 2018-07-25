/**
 * 批量转销售订单按钮
 */	
Ext.define('erp.view.core.button.VastTurnSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnSaleButton',
		text: $I18N.common.button.erpVastTurnSaleButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 120,
    	id: 'erpVastTurnSaleButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});