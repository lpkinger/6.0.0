/**
 * 提交按钮
 */	
Ext.define('erp.view.core.button.Submit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSubmitButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'submit',
    	text: $I18N.common.button.erpSubmitButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});