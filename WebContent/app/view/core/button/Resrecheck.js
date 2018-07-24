/**
 * 反复审按钮
 */	
Ext.define('erp.view.core.button.Resrecheck',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpResrecheckButton',
	iconCls: 'x-button-icon-check',
	cls: 'x-btn-gray',
    id:'resRecheckbutton',
	text: $I18N.common.button.erpResrecheckButton,
	style: {
		marginLeft: '10px'
    },
    width: 80,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});