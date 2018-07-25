/**
 * 供应商索赔单转其他应付单
 */
Ext.define('erp.view.core.button.TurnVCtoAPBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnVCtoAPBill',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '转其它应付单',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});