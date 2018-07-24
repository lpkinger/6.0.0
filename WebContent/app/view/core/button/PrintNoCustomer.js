/**
 * 打印按钮无客户
 */	
Ext.define('erp.view.core.button.PrintNoCustomer',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintNoCustomerButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintNoCustomerButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});