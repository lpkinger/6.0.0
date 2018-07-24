/**
 * 批量转销售订单按钮
 */	
Ext.define('erp.view.core.button.VastSocailsecu',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSocailsecuButton',
		text: $I18N.common.button.erpVastSocailsecuButton,
    	tooltip: '点击进入批量选择模式，可以转入多条记录',
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray-1',
    	width: 150,
    	id: 'erpVastSocailsecuButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});