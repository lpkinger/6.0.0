/**
 * 反审核按钮
 */	
Ext.define('erp.view.core.button.Appstatus',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAppstatusButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray', 
    	id:'erpAppstatusButton',
    	text: $I18N.common.button.erpAppstatusButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});