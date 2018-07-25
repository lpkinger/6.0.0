/**
 * 获取月初汇率
 */	
Ext.define('erp.view.core.button.GetCRRate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetCRRateButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'getcrratebutton',
    	text: $I18N.common.button.erpGetCRRateButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});