Ext.define('erp.view.core.button.ExecuteOperation',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExecuteOperationButton',
		iconCls: 'do',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpExecuteOperationButton,
    	style: {
    		marginLeft: '10px'
        },
        formBind: true,
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});