/**
 * 生成CheckList
 */
Ext.define('erp.view.core.button.CreateCheckList',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateCheckListButton',
		text: $I18N.common.button.erpCreateCheckListButton,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});