/**
 * 商城确认付款
 */	
Ext.define('erp.view.core.button.ConfirmPayB2c',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmPayB2cButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: '确认付款(商城)',
    	id: 'erpConfirmPayB2cButton',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});