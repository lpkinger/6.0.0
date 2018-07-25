/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.ItemsValueSet',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpItemsValueSetButton',
		iconCls: 'x-button-icon-code',
    	cls: 'x-btn-gray',
    	id: 'itemsvaluesetbtn',
    	text: '项目值设置',
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});