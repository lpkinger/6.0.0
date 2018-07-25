/**
 * 按凭证号打印按钮
 */	
Ext.define('erp.view.core.button.PrintVoucherCode',{ 
		id:'printVoucherCode',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintVoucherCodeButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintVoucherCodeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});