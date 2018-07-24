/**
 * 自动开票
 */	
Ext.define('erp.view.core.button.AutoInvoice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAutoInvoiceButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAutoInvoiceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});