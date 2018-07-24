/**
 * 批量审核按钮
 */	
Ext.define('erp.view.core.button.VastAudit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastAuditButton',
		text: $I18N.common.button.erpVastAuditButton,
    	tooltip: '点击进入批量选择模式，可以审核多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastAuditButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90,
		handler: function(){
			alert('audit');
		}
	});