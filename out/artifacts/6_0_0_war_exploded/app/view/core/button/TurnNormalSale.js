Ext.define('erp.view.core.button.TurnNormalSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnNormalSaleButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'erpTurnNormalSaleButton',
    	text: '转正常订单',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});