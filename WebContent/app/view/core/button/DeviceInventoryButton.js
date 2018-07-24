/**
 * 确认盘亏
 */
Ext.define('erp.view.core.button.DeviceInventoryButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeviceInventoryButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDeviceInventoryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});