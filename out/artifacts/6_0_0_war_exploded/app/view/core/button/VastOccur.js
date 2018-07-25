/**
 * 批量生成按钮
 */	
Ext.define('erp.view.core.button.VastOccur',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastOccurButton',
		text: $I18N.common.button.erpVastOccurButton,
    	tooltip: '可以生成多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpVastOccurButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});