/**
 * 转收款单
 */	
Ext.define('erp.view.core.button.TurnRecBalance',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRecBalanceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnRecBalanceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});