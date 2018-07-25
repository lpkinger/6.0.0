/**
 * 转借货出货单按钮
 */	
Ext.define('erp.view.core.button.TurnBorrow',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnBorrowButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnBorrowButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});