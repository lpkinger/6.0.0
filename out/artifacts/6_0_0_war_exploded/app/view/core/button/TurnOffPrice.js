/**
 * 转报价单按钮
 */	
Ext.define('erp.view.core.button.TurnOffPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnOffPriceButton',
		iconCls: 'x-button-icon-delete',
		id: 'turnPurcPrice',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnOffPriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments);
		}
	});