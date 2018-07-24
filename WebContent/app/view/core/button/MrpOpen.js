/**
 * MRP打开
 */	
Ext.define('erp.view.core.button.MrpOpen',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMrpOpenButton',
		//iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'MrpOpen',
    	text: $I18N.common.button.erpMrpOpenButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});