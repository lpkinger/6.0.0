Ext.define('erp.view.core.button.RunATP',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRunATPButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpRunATPButton,
    	style: {
    		marginLeft: '5px'
        },
        width: 100, 
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});