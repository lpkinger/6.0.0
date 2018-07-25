/**
 * 批量分配按钮
 */	
Ext.define('erp.view.core.button.VastAllot',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastAllotButton',
		text: $I18N.common.button.erpVastAllotButton,
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpVastAllotButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 110
	});