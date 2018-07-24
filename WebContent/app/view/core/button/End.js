/**
 * 结案
 */
Ext.define('erp.view.core.button.End',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEndButton',
		param: [],
		id: 'erpEndButton',
		text: $I18N.common.button.erpEndButton,
		iconCls: 'x-button-icon-banned',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});