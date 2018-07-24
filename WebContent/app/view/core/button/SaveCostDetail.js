/**
 * 批量保存按钮
 */	
Ext.define('erp.view.core.button.SaveCostDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSaveCostDetailButton',
    	cls: 'x-btn-gray',
    	tooltip: '保存多条记录',
    	id: 'erpSaveCostDetailButton',
    	text: $I18N.common.button.erpSaveCostDetailButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 100
	});