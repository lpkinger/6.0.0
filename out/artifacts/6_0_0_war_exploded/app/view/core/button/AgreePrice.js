/**
 * 询价最终判定按钮
 */	
Ext.define('erp.view.core.button.AgreePrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAgreePriceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAgreePriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});