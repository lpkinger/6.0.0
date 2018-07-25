/**
 * 生成付款预算单
 */	
Ext.define('erp.view.core.button.ProduceFKBudgetBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProduceFKBudgetBillButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpProduceFKBudgetBillButton',
    	text: $I18N.common.button.erpProduceFKBudgetBillButton,
    	style: {
    		marginLeft: '20px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});