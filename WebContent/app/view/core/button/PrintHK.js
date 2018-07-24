/**
 * 打印按钮--香港
 */	
Ext.define('erp.view.core.button.PrintHK',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintHKButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintHKButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});