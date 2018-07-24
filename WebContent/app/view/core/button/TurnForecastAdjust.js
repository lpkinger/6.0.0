/**
 * 转业务员预测调整
 */	
Ext.define('erp.view.core.button.TurnForecastAdjust',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnForecastAdjustButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnForecastAdjustButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});