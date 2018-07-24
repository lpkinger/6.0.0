/**
 * 商品报价
 */
Ext.define('erp.view.core.button.BatchQuotePrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchPriceButton',
		param: [],
		id: 'erpBatchPriceButton',
		text: $I18N.common.button.erpBatchPriceButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});