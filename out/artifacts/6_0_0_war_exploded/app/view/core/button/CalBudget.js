/**
 * 计算收款预算
 */	
Ext.define('erp.view.core.button.CalBudget',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCalBudgetButton',
		iconCls: 'x-button-icon-reset',
    	cls: 'x-btn-gray',
    	id: 'erpCalBudgetButton',
    	text: $I18N.common.button.erpCalBudgetButton,
    	style: {
    		marginLeft: '20px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});