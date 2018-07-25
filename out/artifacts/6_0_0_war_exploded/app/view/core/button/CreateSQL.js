/**
 * 生成SQL语句
 */
Ext.define('erp.view.core.button.CreateSQL',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateSQLButton',
		text: $I18N.common.button.erpCreateSQLButton,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});