Ext.define('erp.view.core.button.Consistency',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConsistencyButton',
		param: [],
		id:'confirmbutton',
		text: $I18N.common.button.erpConsistencyButton,
		iconCls: 'x-button-icon-save',
		id:'consistencybutton',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});