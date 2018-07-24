
Ext.define('erp.view.core.button.DeviceAttributeButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeviceAttributeButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDeviceAttributeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});