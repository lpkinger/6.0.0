/**
 * A4纸打印按钮
 */	
Ext.define('erp.view.core.button.PrintA4',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintA4Button',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintA4Button,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});