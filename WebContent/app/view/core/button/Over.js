/**
 * 结案
 */
Ext.define('erp.view.core.button.Over',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpOverButton',
		param: [],
		id: 'erpOverButton',
		text: $I18N.common.button.erpOverButton,
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