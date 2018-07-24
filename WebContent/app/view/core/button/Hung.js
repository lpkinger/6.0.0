/**
 * 挂起按钮
 */	
Ext.define('erp.view.core.button.Hung',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpHungButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpHungButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});