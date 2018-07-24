/**
 * 转模具发票按钮
 */	
Ext.define('erp.view.core.button.TurnAPBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnAPBillButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnAPBillButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});