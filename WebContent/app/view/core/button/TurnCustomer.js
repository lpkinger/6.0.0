/**
 * 转客户按钮
 */	
Ext.define('erp.view.core.button.TurnCustomer',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnCustomerButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnCustomerButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});