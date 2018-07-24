/**
 * 解挂按钮
 */	
Ext.define('erp.view.core.button.ReHung',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReHungButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpReHungButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});