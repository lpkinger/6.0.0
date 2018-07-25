/**
 * 禁用按钮
 */	
Ext.define('erp.view.core.button.ForBidden',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpForBiddenButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpForBiddenButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});