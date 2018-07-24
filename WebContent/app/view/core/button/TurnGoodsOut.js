/**
 * 转用品退仓单
 */	
Ext.define('erp.view.core.button.TurnGoodsOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnGoodsOutButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'TurnGoodsOut',
    	text: '转用品退仓单',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});