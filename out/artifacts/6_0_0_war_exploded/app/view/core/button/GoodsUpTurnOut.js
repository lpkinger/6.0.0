/**
 * 上架申请转拨出单
 */	
Ext.define('erp.view.core.button.GoodsUpTurnOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGoodsUpTurnOutButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpGoodsUpTurnOutButton',
    	text: $I18N.common.button.erpGoodsUpTurnOutButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
	});