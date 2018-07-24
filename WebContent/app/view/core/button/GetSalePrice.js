/**
 * 失效
 */
Ext.define('erp.view.core.button.GetSalePrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetSalePriceButton',
		param: [],
		id: 'erpGetSalePriceButton',
		text: '获取订单单价',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});