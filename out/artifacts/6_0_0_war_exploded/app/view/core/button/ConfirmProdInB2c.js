/**
 * 商城确认收货
 */	
Ext.define('erp.view.core.button.ConfirmProdInB2c',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmProdInB2cButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: '确认收货(商城)',
    	id: 'erpConfirmProdInB2cButton',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});