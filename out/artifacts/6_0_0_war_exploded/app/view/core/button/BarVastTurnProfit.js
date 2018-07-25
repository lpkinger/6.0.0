/**
 * 生成条码盘盈
 */
Ext.define('erp.view.core.button.BarVastTurnProfit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBarVastTurnProfitButton',
		text: $I18N.common.button.erpBarVastTurnProfitButton,
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpBarVastTurnProfitButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 110
	});