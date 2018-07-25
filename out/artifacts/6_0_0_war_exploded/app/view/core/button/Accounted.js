/**
 * 记账按钮
 */	
Ext.define('erp.view.core.button.Accounted',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAccountedButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'accountbutton',
    	text: $I18N.common.button.erpAccountedButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});