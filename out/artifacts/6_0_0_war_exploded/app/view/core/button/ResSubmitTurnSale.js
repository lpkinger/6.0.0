/**
 * 反提交(转正常)订单按钮
 */	
Ext.define('erp.view.core.button.ResSubmitTurnSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResSubmitTurnSaleButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '反提交(转正常)',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});