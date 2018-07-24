/**
 * 调整差异凭证生成按钮
 */	
Ext.define('erp.view.core.button.DifferVoucherCredit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDifferVoucherCreditButton',
    	cls: 'x-btn-gray',
    	id: 'erpDifferVoucherCreditButton',
    	text: $I18N.common.button.erpDifferVoucherCreditButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 125
	});