/**
 * 更新信息
 */	
Ext.define('erp.view.core.button.UpdateInfo',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateInfoButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '更新信息',
    	id: 'erpUpdateInfoButton',
    	text: $I18N.common.button.erpUpdateInfoButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});