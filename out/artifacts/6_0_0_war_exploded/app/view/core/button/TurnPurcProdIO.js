/**
 * 转采购验收单按钮
 */	
Ext.define('erp.view.core.button.TurnPurcProdIO',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPurcProdIOButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPurcProdIOButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});