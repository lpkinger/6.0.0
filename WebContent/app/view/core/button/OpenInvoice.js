/**
 * 发票开具按钮
 */	
Ext.define('erp.view.core.button.OpenInvoice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpOpenInvoiceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'openinvoice',
    	text: $I18N.common.button.erpOpenInvoiceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});