/**
 * 更新流程类型
 */	
Ext.define('erp.view.core.button.UpdateMaStyle',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateMaStyleButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: $I18N.common.button.erpUpdateMaStyleButton,
    	id: 'erpUpdateMaStyleButton',
        formBind: true,
    	text: $I18N.common.button.erpUpdateMaStyleButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120
	});