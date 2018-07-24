/**
 * 转价格库按钮（采纳）
 */	
Ext.define('erp.view.core.button.AgreeToPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAgreeToPriceButton',
		text: $I18N.common.button.erpAgreeToPriceButton,
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpAgreeToPriceButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});