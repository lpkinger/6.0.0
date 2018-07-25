/**
 * 转应收票据异动按钮
 */	
Ext.define('erp.view.core.button.TurnBillARChange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnBillARChangeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnBillARChangeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});