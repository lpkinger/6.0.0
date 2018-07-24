/**
 * 制造单批量打开
 */	
Ext.define('erp.view.core.button.VastMakeOpen',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastMakeOpenButton',
		text: $I18N.common.button.erpVastMakeOpenButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastMakeOpenButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});