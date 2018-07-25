/**
 * 刷新年度计划金额
 */	
Ext.define('erp.view.core.button.UpdateGMYearPlan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateGMYearPlanButton',
		iconCls: 'x-button-icon-reset',
    	cls: 'x-btn-gray',
    	id: 'erpUpdateGMYearPlanButton',
    	text: $I18N.common.button.erpUpdateGMYearPlanButton,
    	style: {
    		marginLeft: '20px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});