/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.VastTurnCaree',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnCareeButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'VastTurnCaree',
    	tooltip: '批量转录用申请单',
    	id: 'erpVastTurnCareeButton',
    	text: $I18N.common.button.erpVastTurnCareeButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});