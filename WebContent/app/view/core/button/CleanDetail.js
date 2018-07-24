/**
 * 清除明细
 */	
Ext.define('erp.view.core.button.CleanDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCleanDetailButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'CleanDetail',
    	text: $I18N.common.button.erpCleanDetailButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});