Ext.define('erp.view.core.button.OutLabelPrint',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpOutLabelPrintButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpOutLabelPrintButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){
			var me=this;
			this.callParent(arguments); 
		}
	});