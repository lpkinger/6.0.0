Ext.define('erp.view.core.button.HandleHang',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpHandleHangButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpHandleHangButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});