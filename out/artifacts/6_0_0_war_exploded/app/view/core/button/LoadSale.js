/*
 *装载销售订单按钮 
 */
Ext.define('erp.view.core.button.LoadSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadSaleButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'LoadSale',
    	text: $I18N.common.button.erpLoadSaleButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});