/**
 * 报废按钮
 */
Ext.define('erp.view.core.button.Scrap',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpScrapButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'scrapbutton',
    	text: $I18N.common.button.erpScrapButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
	});