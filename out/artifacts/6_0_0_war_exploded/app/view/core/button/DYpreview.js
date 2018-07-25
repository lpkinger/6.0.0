Ext.define('erp.view.core.button.DYpreview',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDYpreviewButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'dypreviewbutton',
    	text: $I18N.common.button.erpDYpreviewButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});