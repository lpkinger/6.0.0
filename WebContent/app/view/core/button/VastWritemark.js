/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastWritemark',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastWritemarkButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'VastWritemark',
    	tooltip: '批量录入笔试分数',
    	id: 'erpVastWritemarkButton',
    	text: $I18N.common.button.erpVastWritemarkButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});