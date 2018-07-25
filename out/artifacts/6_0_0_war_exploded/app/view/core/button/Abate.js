/**
 * 失效
 */
Ext.define('erp.view.core.button.Abate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAbateButton',
		param: [],
		id: 'erpAbateButton',
		text: $I18N.common.button.erpAbateButton,
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