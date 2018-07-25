/**
 * 转为新客户
 */
Ext.define('erp.view.core.button.TransToCustomer',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTransToCustomerButton',
		param: [],
		id: 'erpTransToCustomerButton',
		text: $I18N.common.button.erpTransToCustomerButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});