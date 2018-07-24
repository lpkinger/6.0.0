/**
 * 审核按钮
 */	
Ext.define('erp.view.core.button.Audit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAuditButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'auditbutton',
    	text: $I18N.common.button.erpAuditButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});