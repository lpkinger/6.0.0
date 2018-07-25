Ext.define('erp.view.core.button.PlanMainTask',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPlanMainTaskButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPlanMainTaskButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});