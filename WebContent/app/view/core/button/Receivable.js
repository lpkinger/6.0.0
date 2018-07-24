/**
 * 收款
 */	
Ext.define('erp.view.core.button.Receivable',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReceivableButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpReceivableButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});