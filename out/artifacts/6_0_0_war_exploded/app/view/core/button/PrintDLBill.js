/**
 * 打印收据
 */	
Ext.define('erp.view.core.button.PrintDLBill',{ 
		id:'printDLBill',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintDLBillButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintDLBillButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});