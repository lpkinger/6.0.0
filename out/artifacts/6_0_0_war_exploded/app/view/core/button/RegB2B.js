/**
 * 提交按钮
 */	
Ext.define('erp.view.core.button.RegB2B',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRegB2BButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpRegB2BButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});