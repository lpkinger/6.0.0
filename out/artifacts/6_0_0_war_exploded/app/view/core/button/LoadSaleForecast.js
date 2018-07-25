/*
 *装载销售预测订单按钮 
 */
Ext.define('erp.view.core.button.LoadSaleForecast',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadSaleForecastButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'LoadSaleForecast',
    	text: $I18N.common.button.erpLoadSaleForecastButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});