/**
 * 批量转销售订单按钮
 */	
Ext.define('erp.view.core.button.VastSocailaccount',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSocailaccountButton',
		text: $I18N.common.button.erpVastSocailaccountButton,
    	tooltip: '点击进入批量选择模式，可以转入多条记录',
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray-1',
    	width: 150,
    	id: 'erpVastSocailaccountButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});