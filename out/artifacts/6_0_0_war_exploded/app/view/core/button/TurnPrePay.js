/**
 * 转预付款单
 */	
Ext.define('erp.view.core.button.TurnPrePay',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPrePayButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPrePayButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});