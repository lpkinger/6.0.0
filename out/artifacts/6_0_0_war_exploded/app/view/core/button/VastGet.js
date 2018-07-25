/**
 * 确认领取按钮
 */	
Ext.define('erp.view.core.button.VastGet',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastGetButton',
		iconCls: 'x-button-icon-Get',
    	cls: 'x-btn-gray-1',
    	id: 'get',
    	tooltip: '确认领取多条记录',
    	id: 'erpVastGetButton',
    	text: $I18N.common.button.erpVastGetButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 110,
		handler: function(){
			
		}
	});