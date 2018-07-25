/**
 * 冻结按钮
 */	
Ext.define('erp.view.core.button.CancelFreeze',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCancelFreezeButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'CancelFreeze',
    	text: $I18N.common.button.erpCancelFreezeButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});