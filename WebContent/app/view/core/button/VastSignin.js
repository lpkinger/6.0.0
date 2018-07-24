/**
 * 批量签收按钮
 */	
Ext.define('erp.view.core.button.VastSignin',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSigninButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'save',
    	tooltip: '签收多条记录',
    	id: 'erpVastSigninButton',
    	text: $I18N.common.button.erpVastSigninButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90,
		handler: function(){
		}
	});