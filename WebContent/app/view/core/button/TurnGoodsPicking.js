/**
 * 转用品领用
 */	
Ext.define('erp.view.core.button.TurnGoodsPicking',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnGoodsPickingButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'turngoodspicking',
    	text: $I18N.common.button.erpTurnGoodsPickingButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});