/**
 * 过账按钮
 */	
Ext.define('erp.view.core.button.CatchPR',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchPRButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchPRButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});