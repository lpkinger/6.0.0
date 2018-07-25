/**
 * 转冲应收款单
 */	
Ext.define('erp.view.core.button.TurnRecBalanceIMRE',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRecBalanceIMREButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnRecBalanceIMREButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});