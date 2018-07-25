/**
 * 收款计划
 */	
Ext.define('erp.view.core.button.ReceivablePlan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReceivablePlanButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpReceivablePlanButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});