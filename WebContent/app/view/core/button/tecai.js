/**
 * 特采按钮
 */	
Ext.define('erp.view.core.button.tecai',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erptecaiButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'getprice',
    	text: $I18N.common.button.erptecaiButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});