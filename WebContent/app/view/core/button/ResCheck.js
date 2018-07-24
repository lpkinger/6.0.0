/**
 * 反批准按钮
 */	
Ext.define('erp.view.core.button.ResCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResCheckButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'resCheck',
    	text: $I18N.common.button.erpResCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});