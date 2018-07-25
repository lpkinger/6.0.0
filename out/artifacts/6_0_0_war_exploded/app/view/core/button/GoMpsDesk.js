Ext.define('erp.view.core.button.GoMpsDesk',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGoMpsDeskButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'gompsdesk',
    	text:$I18N.common.button.erpGoMpsDeskButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,     
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});