Ext.define('erp.view.core.button.Parameter',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpParameterButton',
		param: [],
		id:'confirmbutton',
		text: $I18N.common.button.erpParameterButton,
		iconCls: 'x-button-icon-save', 
    	cls: 'x-btn-gray',
    	width:100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});