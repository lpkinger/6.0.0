/**
 * 制造单批量关闭
 */	
Ext.define('erp.view.core.button.VastMakeClose',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastMakeCloseButton',
		text: $I18N.common.button.erpVastMakeCloseButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastMakeCloseButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});