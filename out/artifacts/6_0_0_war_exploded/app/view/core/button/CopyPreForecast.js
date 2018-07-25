/**
 * 复制业务员预测按钮
 */	
Ext.define('erp.view.core.button.CopyPreForecast',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCopyPreForecast',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'copypreforecast',
    	text: $I18N.common.button.erpCopyPreForecast,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});