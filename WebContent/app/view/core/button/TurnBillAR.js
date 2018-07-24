/**
 * 转应收票据按钮
 */	
Ext.define('erp.view.core.button.TurnBillAR',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnBillARButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnBillARButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});