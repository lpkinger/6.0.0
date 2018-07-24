/**
 * 更新信息
 */	
Ext.define('erp.view.core.button.UpdateOtherExplistInfo',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateOtherExplistInfoButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '更新信息',
    	id: 'erpUpdateOtherExplistInfoButton',
    	text: $I18N.common.button.erpUpdateOtherExplistInfoButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 180
	});