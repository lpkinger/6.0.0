/**
 * 复审按钮
 */	
Ext.define('erp.view.core.button.Recheck',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpRecheckButton',
	iconCls: 'x-button-icon-check',
	cls: 'x-btn-gray',
    id:'recheckbutton',
	text: $I18N.common.button.erpRecheckButton,
	style: {
		marginLeft: '10px'
    },
    width: 60,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});