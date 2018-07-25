/**
 * 转冲应付款单
 */	
Ext.define('erp.view.core.button.TurnPayBalanceCYF',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPayBalanceCYFButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPayBalanceCYFButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});