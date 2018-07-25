/**
 * 合并设置按钮
 */	
Ext.define('erp.view.core.button.GroupSet',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGroupSetButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'groupsetbutton',
    	text: $I18N.common.button.erpGroupSetButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});