/**
 * 打印按钮
 */	
Ext.define('erp.view.core.button.PrintwithPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintwithPriceButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintwithPriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});