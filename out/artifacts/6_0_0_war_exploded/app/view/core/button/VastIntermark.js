/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastIntermark',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastIntermarkButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray-1',
    	id: 'VastIntermark',
    	tooltip: '批量录入面试分数',
    	id: 'erpVastIntermarkButton',
    	text: $I18N.common.button.erpVastIntermarkButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 140,
		handler: function(){
		}
	});