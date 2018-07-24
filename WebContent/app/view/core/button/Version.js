Ext.define('erp.view.core.button.Version',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVersionButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpVersionButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});