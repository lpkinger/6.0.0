/**
 * 转上架申请
 */	
Ext.define('erp.view.core.button.TurnGoodsUp',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnGoodsUpButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnGoodsUpButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});