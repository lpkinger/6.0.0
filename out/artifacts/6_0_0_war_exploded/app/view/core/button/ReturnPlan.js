/**
 * 还款计划
 */	
Ext.define('erp.view.core.button.ReturnPlan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReturnPlanButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpReturnPlanButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});