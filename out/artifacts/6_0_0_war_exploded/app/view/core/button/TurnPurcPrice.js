/**
 * 转物料核价单按钮
 */	
Ext.define('erp.view.core.button.TurnPurcPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPurcPriceButton',
		iconCls: 'x-button-icon-delete',
		id: 'turnPurcPrice',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPurcPriceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments);
		}
	});