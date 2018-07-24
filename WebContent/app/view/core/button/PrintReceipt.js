/**
 * 打印签收单
 */	
Ext.define('erp.view.core.button.PrintReceipt',{ 
		id:'printReceipt',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintReceiptButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintReceiptButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});