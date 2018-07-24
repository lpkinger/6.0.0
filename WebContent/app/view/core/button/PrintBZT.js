/**
 * 打印出货单，补客退，天派
 */	
Ext.define('erp.view.core.button.PrintBZT',{ 
		id:'printBZT',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintBZTButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintBZTButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});