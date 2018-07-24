/**
 * 反记账按钮
 */	
Ext.define('erp.view.core.button.ResAccounted',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResAccountedButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'resaccountbutton',
    	text: $I18N.common.button.erpResAccountedButton,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});