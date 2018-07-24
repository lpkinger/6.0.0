/**
 * 失效
 */
Ext.define('erp.view.core.button.AllowNumber',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAllowNumberButton',
		param: [],
		id: 'erpAllowNumberButton',
		text: $I18N.common.button.erpAllowNumberButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});