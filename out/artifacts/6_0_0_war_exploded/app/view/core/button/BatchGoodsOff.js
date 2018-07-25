/**
 * 商品批量下架
 */	
Ext.define('erp.view.core.button.BatchGoodsOff',{ 
		id:'batchgoodsoff',
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchGoodsOffButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBatchGoodsOffButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
	});