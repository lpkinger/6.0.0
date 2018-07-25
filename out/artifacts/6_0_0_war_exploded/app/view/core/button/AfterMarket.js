/**
 * 索菱售后按钮
 */	
Ext.define('erp.view.core.button.AfterMarket',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAfterMarketButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'AfterMarketButton',
    	text: 'fdsfdsf',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});