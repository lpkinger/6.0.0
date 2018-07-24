/**
 * 制造ECN转退料单
 */
Ext.define('erp.view.core.button.TurnProdIOMReturn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProdIOMReturnButton',
		param: [],
		id: 'erpTurnProdIOMReturnButton',
		text: $I18N.common.button.erpTurnProdIOMReturnButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});