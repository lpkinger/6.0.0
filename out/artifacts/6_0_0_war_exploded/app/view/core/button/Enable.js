/**
 * 启用
 */
Ext.define('erp.view.core.button.Enable',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEnableButton',
		param: [],
		id: 'erpEnableButton',
		text: $I18N.common.button.erpEnableButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});