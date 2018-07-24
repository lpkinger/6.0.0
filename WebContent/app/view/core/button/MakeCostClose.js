Ext.define('erp.view.core.button.MakeCostClose',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMakeCostCloseButton',
		param: [],
		text: $I18N.common.button.erpMakeCostCloseButton,
		iconCls: 'x-button-icon-check',
		id:'makeCostCloseButton',
    	cls: 'x-btn-gray',
    	width: 150,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});