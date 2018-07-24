/**
 * 生产日报，载入工序按钮
 */
Ext.define('erp.view.core.button.LoadProcess',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadProcessButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'loadprocessbutton',
    	text: $I18N.common.button.erpLoadProcessButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 85,
		initComponent : function(){
			this.callParent(arguments);
		}
	});