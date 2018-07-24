/**
 * 批量处理按钮
 */	
Ext.define('erp.view.core.button.NotAgreeToPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpNotAgreeToPriceButton',
		text: $I18N.common.button.erpNotAgreeToPriceButton,
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpNotAgreeToPriceButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 110		
	});