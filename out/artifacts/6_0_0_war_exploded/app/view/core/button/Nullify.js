/**
 * 作废按钮
 */	
Ext.define('erp.view.core.button.Nullify',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpNullifyButton',
	iconCls: 'x-button-icon-check',
	cls: 'x-btn-gray',
    id: 'Freeze',
	text: $I18N.common.button.erpNullifyButton,
	style: {
		marginLeft: '10px'
    },

    width: 60,
	initComponent : function(){ 
		this.callParent(arguments); 
	}
});