Ext.define('erp.view.core.button.ConfirmIn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmInButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpConfirmInButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});