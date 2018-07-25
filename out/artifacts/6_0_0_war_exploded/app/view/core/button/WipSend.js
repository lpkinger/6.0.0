Ext.define('erp.view.core.button.WipSend',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpWipSendButton',
		iconCls: 'x-button-icon-add',
		cls: 'x-btn-gray',
		id: 'WipSend',
    	text: $I18N.common.button.erpWipSendButton,
    	style: {
    		marginLeft: '10px'
        }, 
        width: 120,   
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	}); 