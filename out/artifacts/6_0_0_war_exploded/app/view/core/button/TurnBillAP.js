/**
 * 转应付票据付款按钮
 */	
Ext.define('erp.view.core.button.TurnBillAP',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnBillAPButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnBillAPButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});