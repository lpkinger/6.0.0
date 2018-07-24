/**
 * 转模具付款申请单
 */	
Ext.define('erp.view.core.button.TurnFeePlease',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnFeePleaseButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnFeePleaseButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});