/**
 * 载入按钮按钮
 */	
Ext.define('erp.view.core.button.Get',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetButton',
		param: [],
		text: $I18N.common.button.erpGetButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});