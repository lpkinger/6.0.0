/**
 * 反禁用按钮
 */	
Ext.define('erp.view.core.button.ResBanned',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResBannedButton',
		iconCls: 'x-button-icon-recall',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpResBannedButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});