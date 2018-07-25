/**
 * 批量确认BUG
 */	
Ext.define('erp.view.core.button.ConfirmBug',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmBugButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpConfirmBugButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});