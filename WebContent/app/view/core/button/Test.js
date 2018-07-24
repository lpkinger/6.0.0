Ext.define('erp.view.core.button.Test',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTestButton',
		param: [],
		text: $I18N.common.button.erpTestButton,
		iconCls: 'x-button-icon-help',
		id: 'testbutton',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});