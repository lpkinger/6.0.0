/**
 * 反提交按钮
 */	
Ext.define('erp.view.core.button.ResSubmit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResSubmitButton',
		iconCls: 'x-button-icon-resend',
    	cls: 'x-btn-gray',
    	id: 'resSubmit',
    	text: $I18N.common.button.erpResSubmitButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});