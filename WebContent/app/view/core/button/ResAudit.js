/**
 * 反审核按钮
 */	
Ext.define('erp.view.core.button.ResAudit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResAuditButton',
		iconCls: 'x-button-icon-recall',
    	cls: 'x-btn-gray',
    	id: 'resAudit',
    	text: $I18N.common.button.erpResAuditButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});