/**
 * 转模具出货单
 */	
Ext.define('erp.view.core.button.TurnDeliveryOrder',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnDeliveryOrderButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnDeliveryOrderButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});