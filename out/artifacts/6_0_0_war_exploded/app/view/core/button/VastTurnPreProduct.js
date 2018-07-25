/**
 * 批量转新物料按钮
 */	
Ext.define('erp.view.core.button.VastTurnPreProduct',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnPreProductButton',
		text: $I18N.common.button.erpVastTurnPreProductButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 120,
    	id: 'erpVastTurnPreProductButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});