/**
 * 查看位置维护按钮
 */	
Ext.define('erp.view.core.button.Location',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLocationButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'location',
	    disabled: true,
    	text: $I18N.common.button.erpLocationButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});