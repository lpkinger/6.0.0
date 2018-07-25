/**
 * 拆件单转批量入库按钮
 */	
Ext.define('erp.view.core.button.VastTurnProdIn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnProdInButton',
		text: $I18N.common.button.erpVastTurnProdInButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 100,
    	id: 'erpVastTurnProdInButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});