/**
 * 转发出商品
 */	
Ext.define('erp.view.core.button.TurnGoodsSend',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnGoodsSendButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnGoodsSendButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});