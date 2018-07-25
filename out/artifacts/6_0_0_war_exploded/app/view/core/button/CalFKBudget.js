/**
 * 计算付款预算
 */	
Ext.define('erp.view.core.button.CalFKBudget',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCalFKBudgetButton',
		iconCls: 'x-button-icon-reset',
    	cls: 'x-btn-gray',
    	id: 'erpCalFKBudgetButton',
    	text: $I18N.common.button.erpCalFKBudgetButton,
    	style: {
    		marginLeft: '20px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});