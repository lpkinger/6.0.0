/**
 * 无值转出生成凭证
 */	
Ext.define('erp.view.core.button.NowhVoucherCredit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpNowhVoucherCreditButton',
    	cls: 'x-btn-gray',
    	id: 'erpNowhVoucherCreditButton',
    	text: $I18N.common.button.erpNowhVoucherCreditButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 125
	});