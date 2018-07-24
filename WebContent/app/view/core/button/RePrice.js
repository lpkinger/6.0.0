/**
 * 单价重置按钮
 */	
Ext.define('erp.view.core.button.RePrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRePriceButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'reprice',
    	text: $I18N.common.button.erpRePriceButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});