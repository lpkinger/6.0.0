/**
 * 获取定价
 */
Ext.define('erp.view.core.button.POGetPrice',{
		extend: 'Ext.Button', 
		alias: 'widget.erpPOGetPriceButton',
		param: [],
		id: 'erpPOGetPriceButton',
		text: '获取定价',
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