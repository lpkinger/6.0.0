/**
 * 获取关联下拉项caller
 */	
Ext.define('erp.view.core.button.DlcCaller',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDlcCallerButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'dlccallerbtn',
    	text: $I18N.common.button.erpDlcCallerButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});