Ext.define('erp.view.core.button.Publish',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPublishButton',
		param: [],
		text: $I18N.common.button.erpPublishButton,
		iconCls: 'x-button-icon-confirm',
		id: 'publishbutton',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});