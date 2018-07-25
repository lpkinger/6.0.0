/**
 * 拆分
 */
Ext.define('erp.view.core.button.Split',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSplitButton',
		param: [],
		id: 'erpSplitButton',
		text: $I18N.common.button.erpSplitButton,
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