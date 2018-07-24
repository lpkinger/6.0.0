/**
 * 发票打印按钮
 */	
Ext.define('erp.view.core.button.PrintInvoice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintInvoiceButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintInvoiceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});