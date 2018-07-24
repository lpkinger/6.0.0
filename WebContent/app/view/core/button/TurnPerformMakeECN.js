/**
 * 制造ECN批量转执行
 */	
Ext.define('erp.view.core.button.TurnPerformMakeECN',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPerformMakeECN',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPerformMakeECN,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});