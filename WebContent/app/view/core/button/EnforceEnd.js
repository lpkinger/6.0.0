/**
 * 强制结案
 */
Ext.define('erp.view.core.button.EnforceEnd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEnforceEndButton',
		param: [],
		id: 'erpEndButton',
		text: $I18N.common.button.erpEnforceEndButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});