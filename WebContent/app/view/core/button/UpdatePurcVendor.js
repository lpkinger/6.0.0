/**
 * 询价单修改终端供应商信息
 */	
Ext.define('erp.view.core.button.UpdatePurcVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdatePurcVendorButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpUpdatePurcVendorButton,
    	id:'updatePurcVendorbutton',
    	style: {
    		marginLeft: '10px'
        },
        width: 120
		
	});