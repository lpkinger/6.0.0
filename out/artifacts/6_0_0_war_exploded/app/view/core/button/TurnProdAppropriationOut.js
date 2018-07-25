/**
 * 转拨出单按钮
 */	
Ext.define('erp.view.core.button.TurnProdAppropriationOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProdAppropriationOutButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnProdAppropriationOutButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});