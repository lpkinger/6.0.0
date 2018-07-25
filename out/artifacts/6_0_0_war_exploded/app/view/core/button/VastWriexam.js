/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastWriexam',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastWriexamButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'VastWriexam',
    	tooltip: '批量转笔试',
    	id: 'erpVastWriexamButton',
    	text: $I18N.common.button.erpVastWriexamButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 100,
		handler: function(){
		}
	});