/**
 * 转付款单
 */	
Ext.define('erp.view.core.button.TurnPayBalance',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPayBalanceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPayBalanceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});