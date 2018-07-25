/**
 * 强制结案
 */
Ext.define('erp.view.core.button.ForceEnd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpForceEndButton',
		param: [],
		id: 'erpForceEndButton',
		text: $I18N.common.button.erpForceEndButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 80,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});