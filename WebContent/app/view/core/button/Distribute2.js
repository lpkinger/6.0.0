/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.Distribute2',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDistribute2Button',
		iconCls: 'x-button-icon-add',
		id: 'distributebtn',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDistribute2Button,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});