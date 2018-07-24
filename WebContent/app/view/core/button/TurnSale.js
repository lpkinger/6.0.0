/**
 * 转销售按钮
 */	
Ext.define('erp.view.core.button.TurnSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnSaleButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnSaleButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});