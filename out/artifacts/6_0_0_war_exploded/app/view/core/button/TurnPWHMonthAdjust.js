/**
 * 生成期末调整单按钮
 */	
Ext.define('erp.view.core.button.TurnPWHMonthAdjust',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPWHMonthAdjustButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'save',
    	tooltip: '保存多条记录',
    	id: 'erpTurnPWHMonthAdjustButton',
    	text: $I18N.common.button.erpTurnPWHMonthAdjustButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 130
	});