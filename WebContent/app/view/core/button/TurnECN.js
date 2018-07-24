/**
 * 转ECN按钮
 */	
Ext.define('erp.view.core.button.TurnECN',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnECNButton',
		iconCls: 'x-button-icon-ecn',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnECNButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});