/**
 * 确认实际发料数
 */	
Ext.define('erp.view.core.button.SetMMQTY',{
		extend: 'Ext.Button', 
		alias: 'widget.erpSetMMQTYButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '确认实际可发数',
    	id: 'erpSetMMQTYButton',
        formBind: true,
    	text: $I18N.common.button.erpSetMMQTYButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 130
	});