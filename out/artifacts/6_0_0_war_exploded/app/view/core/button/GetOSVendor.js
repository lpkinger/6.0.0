/**
 * 自动获取委外商信息按钮
 */	
Ext.define('erp.view.core.button.GetOSVendor',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetOSVendorButton',
		iconCls : 'x-button-icon-submit',
		cls : 'x-btn-gray',
	    id: 'GetOSVendor',
    	text: $I18N.common.button.erpGetOSVendorButton,
    	style: {
    		marginLeft: '10px'
        },

        width:140,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});