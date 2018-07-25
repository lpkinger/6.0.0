/**
 * 生成预算单
 */	
Ext.define('erp.view.core.button.ProduceBudgetBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProduceBudgetBillButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpProduceBudgetBillButton',
    	text: $I18N.common.button.erpProduceBudgetBillButton,
    	style: {
    		marginLeft: '20px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});