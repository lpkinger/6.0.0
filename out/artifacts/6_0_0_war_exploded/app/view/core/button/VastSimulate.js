/**
 * 排货模拟按钮
 */	
Ext.define('erp.view.core.button.VastSimulate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSimulateButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray-1',
    	id: 'delete',
    	tooltip: '排货模拟多条记录',
    	id: 'erpVastSimulateButton',
    	text: $I18N.common.button.erpVastSimulateButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90,
		handler: function(){
		}
	});