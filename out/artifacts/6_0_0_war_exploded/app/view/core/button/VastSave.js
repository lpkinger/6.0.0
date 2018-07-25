/**
 * 批量保存按钮
 */	
Ext.define('erp.view.core.button.VastSave',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSaveButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'save',
    	tooltip: '保存多条记录',
    	id: 'erpVastSaveButton',
    	text: $I18N.common.button.erpVastSaveButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 60,
		handler: function(){
			
		}
	});