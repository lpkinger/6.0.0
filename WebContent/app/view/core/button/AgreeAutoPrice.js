/**
 * 询价最终判定按钮
 */	
Ext.define('erp.view.core.button.AgreeAutoPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAgreeAutoPriceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAgreePriceButton,
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});