/**
 * 账龄计算按钮
 */	
Ext.define('erp.view.core.button.Zhangling',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpZhanglingButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpZhanglingButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});