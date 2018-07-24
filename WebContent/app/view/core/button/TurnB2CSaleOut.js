/**
 * 销售订单转商城发货
 */	
Ext.define('erp.view.core.button.TurnB2CSaleOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnB2CSaleOutButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'TurnB2CSaleOut',
    	text: $I18N.common.button.erpTurnB2CSaleOutButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});