Ext.define('erp.view.core.button.ProcessRemind',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessRemindButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpProcessRemindButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});