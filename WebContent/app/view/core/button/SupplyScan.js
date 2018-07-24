Ext.define('erp.view.core.button.SupplyScan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSupplyScanButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpSupplyScanButton,
    	style: {
    		marginLeft: '10px'
        },
        formBind: true,
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});