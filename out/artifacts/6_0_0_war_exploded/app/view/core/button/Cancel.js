Ext.define('erp.view.core.button.Cancel',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCancelButton',
		param: [],
		text: $I18N.common.button.erpCancelButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});