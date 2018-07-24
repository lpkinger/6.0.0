/**
 * 批量备置按钮
 */	
Ext.define('erp.view.core.button.VastSpare',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSpareButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'save',
    	tooltip: '备置多条记录',
    	id: 'erpVastSpareButton',
    	text: $I18N.common.button.erpVastSpareButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 60,
		handler: function(){
		}
	});