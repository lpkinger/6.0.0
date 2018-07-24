/**
 * 过账按钮
 */	
Ext.define('erp.view.core.button.CatchYS',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchYSButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchYSButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});