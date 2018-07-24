/**
 * 批量处理按钮
 */	
Ext.define('erp.view.core.button.VastDeal',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastDealButton',
		text: $I18N.common.button.erpVastDealButton,
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpVastDealButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});