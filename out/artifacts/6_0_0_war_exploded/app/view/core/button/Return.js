/**
 * 还款
 */	
Ext.define('erp.view.core.button.Return',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReturnButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpReturnButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});