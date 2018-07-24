Ext.define('erp.view.core.button.TurnAbNormalSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnAbNormalSaleButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'erpTurnAbNormalSaleButton',
    	text: '非转正常订单',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});