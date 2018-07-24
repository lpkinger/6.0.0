/**
 * 转评估单
 */	
Ext.define('erp.view.core.button.TurnKBIBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnKBIBillButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnKBIBillButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});