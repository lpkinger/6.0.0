/**
 * 分配权限按钮
 */	
Ext.define('erp.view.core.button.Distribute',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDistributeButton',
		iconCls: 'x-button-icon-power',
    	cls: 'x-btn-gray-1',
	    id: 'distribute',
    	text: $I18N.common.button.erpDistributeButton,
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});