Ext.define('erp.view.core.button.UpdateForecastQty',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateForecastQtyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '更改预测数量',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});