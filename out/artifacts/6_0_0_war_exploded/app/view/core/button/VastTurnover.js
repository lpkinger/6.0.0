/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastTurnover',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnoverButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'VastTurnover',
    	tooltip: '批量转离职',
    	id: 'erpVastTurnoverButton',
    	text: $I18N.common.button.erpVastTurnoverButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});