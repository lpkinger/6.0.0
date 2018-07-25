Ext.define('erp.view.core.button.TrayLabelPrint',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTrayLabelPrintButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTrayLabelPrintButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){
			var me=this;
			this.callParent(arguments); 
		}
	});