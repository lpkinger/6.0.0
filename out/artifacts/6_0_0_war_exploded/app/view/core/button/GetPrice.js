/**
 * 取价按钮
 */	
Ext.define('erp.view.core.button.GetPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetPriceButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'getprice',
    	text: $I18N.common.button.erpGetPriceButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});