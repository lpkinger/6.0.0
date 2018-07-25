/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastDelete',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastDeleteButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray-1',
    	id: 'delete',
    	tooltip: '删除多条记录',
    	id: 'erpVastDeleteButton',
    	text: $I18N.common.button.erpVastDeleteButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90,
		handler: function(){
		}
	});