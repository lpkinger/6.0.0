/**
 * 关联客户
 */	
Ext.define('erp.view.core.button.Connectcustomer',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConnectcustomerButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'connectcustomer',
    	text: $I18N.common.button.erpConnectcustomerButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});